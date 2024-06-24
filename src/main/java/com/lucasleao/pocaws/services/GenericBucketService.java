package com.lucasleao.pocaws.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.lucasleao.pocaws.core.domain.events.FileDeletedEvent;
import com.lucasleao.pocaws.core.domain.exceptions.FileException;
import com.lucasleao.pocaws.core.domain.valueobjects.File;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Getter
public abstract class GenericBucketService implements BucketService {


    @Value("${app.bucket.name}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;


    private final AmazonS3 s3Client;

    private final ApplicationEventPublisher applicationEventPublisher;

    public GenericBucketService(AmazonS3 s3Client, ApplicationEventPublisher applicationEventPublisher) {
        this.s3Client = s3Client;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public File uploadFile(MultipartFile file) {


        String fileName = buildFileName(file.getOriginalFilename());
        String fileUrl = buildFileUrl(fileName);

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    bucketName,
                    fileName,
                    convertMultipartFileToFile(file)
            );

            s3Client.putObject(putObjectRequest);

        }catch (IOException e) {
            throw new FileException("Error while converting request file: " + e.getMessage());
        }

        return com.lucasleao.pocaws.core.domain.valueobjects.File.builder()
                .id(fileName)
                .url(fileUrl)
                .build();
    }

    @Override
    public S3Object downloadFile(String id) {
        try {
            return s3Client.getObject(bucketName, id);
        }catch (AmazonS3Exception e) {
            throw new FileException("file not found: " + e.getMessage());
        }
    }

    public String buildFileName(String originalFilename) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalFilename.getBytes(StandardCharsets.UTF_8));

            String base64Hash = Base64.getUrlEncoder().encodeToString(hash);

            return base64Hash.substring(0, 10) + "." + originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract String buildFileUrl(String fileName);


    @Override
    public List<String> listFiles() {

        ListObjectsV2Result objects = s3Client.listObjectsV2(bucketName);

        return objects.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteFile(String id) {

        objectExists(bucketName, id);

        s3Client.deleteObject(bucketName, id);

        applicationEventPublisher.publishEvent(new FileDeletedEvent(this, id));

    }

    private java.io.File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        java.io.File convFile = new java.io.File(System.getProperty("java.io.tmpdir") + "/" + multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(multipartFile.getBytes());
        }
        return convFile;
    }


    private void objectExists(String bucketName, String id) {
        try {
           s3Client.getObjectMetadata(bucketName, id);
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() == 404) {
                throw new FileException("File does not exists: " + id + e.getMessage());
            } else {
                throw new FileException("Error while searching for file: " + e.getMessage());
            }
        }
    }

}
