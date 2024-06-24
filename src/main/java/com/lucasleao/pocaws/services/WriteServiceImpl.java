package com.lucasleao.pocaws.services;

import com.lucasleao.pocaws.core.domain.valueobjects.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WriteServiceImpl implements WriteService {

    private final BucketService bucketService;

    private final FileService fileService;

    public WriteServiceImpl(BucketService bucketService, FileService fileService) {
        this.bucketService = bucketService;
        this.fileService = fileService;
    }


    @Override
    public String process(MultipartFile file) {
        File uploadedFile = bucketService.uploadFile(file);

        fileService.save(uploadedFile);

        return uploadedFile.getUrl();
    }

    @Override
    public void deleteFile(String fileId) {
        bucketService.deleteFile(fileId);
    }
}
