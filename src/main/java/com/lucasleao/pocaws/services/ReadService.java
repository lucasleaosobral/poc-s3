package com.lucasleao.pocaws.services;

import com.amazonaws.services.s3.model.S3Object;

import java.util.List;

public interface ReadService {

    String findFileUrl(String fileId);

    S3Object downloadFile(String fileId);

    List<String> listFiles();
}
