package com.lucasleao.pocaws.core.domain.mapper;


import com.lucasleao.pocaws.core.domain.valueobjects.File;
import com.lucasleao.pocaws.core.entities.FileEntity;
import org.springframework.stereotype.Component;

@Component
public class FileDataMapper {

    public FileEntity fileDomainToEntity(File file) {
        return FileEntity.builder()
                .id(file.getId())
                .url(file.getUrl())
                .build();
    }
}
