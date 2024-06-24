package com.lucasleao.pocaws.core.entities;


import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "files")
@Builder
@Getter
public class FileEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private String hash;

    private String url;

}
