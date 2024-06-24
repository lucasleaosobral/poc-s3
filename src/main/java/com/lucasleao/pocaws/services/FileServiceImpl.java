package com.lucasleao.pocaws.services;

import com.lucasleao.pocaws.core.domain.valueobjects.File;
import com.lucasleao.pocaws.core.repositories.FileRepository;
import org.springframework.stereotype.Service;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void save(File file) {
        fileRepository.save(file);
    }

    @Override
    public String findById(String id) {
        return fileRepository.findById(id);
    }
}
