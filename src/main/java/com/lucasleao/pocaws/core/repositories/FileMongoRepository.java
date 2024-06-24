package com.lucasleao.pocaws.core.repositories;

import com.lucasleao.pocaws.core.entities.FileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMongoRepository extends MongoRepository<FileEntity, String> {
}
