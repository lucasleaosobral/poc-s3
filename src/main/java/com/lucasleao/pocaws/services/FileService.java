package com.lucasleao.pocaws.services;


import com.lucasleao.pocaws.core.domain.valueobjects.File;

public interface FileService {

    void save(File file);

    String findById(String id);

}
