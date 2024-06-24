package com.lucasleao.pocaws.services;


import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadServiceImpl implements ReadService {

    private final FileService fileService;

    private final BucketService bucketService;

    public ReadServiceImpl(FileService fileRepository, BucketService bucketService) {
        this.fileService = fileRepository;
        this.bucketService = bucketService;
    }


    @Override
    public String findFileUrl(String fileId) {
        return fileService.findById(fileId);
    }

    @Override
    public S3Object downloadFile(String fileId) {
        return bucketService.downloadFile(fileId);
    }

    @Override
    public List<String> listFiles() {
        return bucketService.listFiles();
    }
}
