package com.lucasleao.pocaws.core.domain.utils;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.lucasleao.pocaws.core.domain.exceptions.FileException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class FileUtils {

    public byte[] getBytesFromFile(S3Object file) {
        try{
            return IOUtils.toByteArray(file.getObjectContent());
        }catch (IOException e) {
            throw new FileException("error while reading file content: " + e.getMessage());
        }
    }

    public String getFileContentType(byte[] content) {
        Tika tika = new Tika();
        return  tika.detect(content);
    }
}
