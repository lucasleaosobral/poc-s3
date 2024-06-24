package com.lucasleao.pocaws.services;


import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


@Service
@ConditionalOnProperty(
        value="app.localstack",
        havingValue = "true")
public class LocalStackBucketServiceImpl extends GenericBucketService {

    @Value("${app.localstack.url}")
    private String endpoint;

    public LocalStackBucketServiceImpl(AmazonS3 s3Client, ApplicationEventPublisher applicationEventPublisher) {
        super(s3Client, applicationEventPublisher);
    }

    @Override
    public String buildFileUrl(String fileName) {
        return endpoint + "/" + super.getBucketName() + "/" + fileName;
    }
}
