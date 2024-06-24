package com.lucasleao.pocaws.services;

import org.springframework.web.multipart.MultipartFile;

public interface WriteService {

    String process(MultipartFile file);

    void deleteFile(String fileId);
}
