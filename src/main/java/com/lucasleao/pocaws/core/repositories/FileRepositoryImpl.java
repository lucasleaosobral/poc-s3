package com.lucasleao.pocaws.core.repositories;

import com.lucasleao.pocaws.core.domain.mapper.FileDataMapper;
import com.lucasleao.pocaws.core.domain.valueobjects.File;
import com.lucasleao.pocaws.core.entities.FileEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FileRepositoryImpl implements FileRepository {

    private final FileMongoRepository mongoRepository;

    private final FileDataMapper fileDataMapper;

    public FileRepositoryImpl(FileMongoRepository fileMongoRepository, FileDataMapper fileDataMapper) {
        this.mongoRepository = fileMongoRepository;
        this.fileDataMapper = fileDataMapper;
    }

    @Override
    public void save(File file) {
        mongoRepository.save(fileDataMapper.fileDomainToEntity(file));
    }

    @Override
    public String findById(String id) {
        Optional<FileEntity> file = mongoRepository.findById(id);
        return file.isPresent() ? file.get().getUrl() : null;
    }

    @Override
    public void deleteById(String id) {
        mongoRepository.deleteById(id);
    }
}
