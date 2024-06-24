package com.lucasleao.pocaws.services;

import com.amazonaws.services.s3.model.S3Object;
import com.lucasleao.pocaws.core.domain.valueobjects.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BucketService {

    File uploadFile(MultipartFile file);

    S3Object downloadFile(String id);

    List<String> listFiles();

    void deleteFile(String id);
}
