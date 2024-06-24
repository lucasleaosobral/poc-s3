package com.lucasleao.pocaws.core.repositories;

import com.lucasleao.pocaws.core.domain.valueobjects.File;

public interface FileRepository {

    void save(File file);

    String findById(String id);

    void deleteById(String id);
}
