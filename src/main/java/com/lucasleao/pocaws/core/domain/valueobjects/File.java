package com.lucasleao.pocaws.core.domain.valueobjects;


import lombok.Builder;

@Builder
public class File {

    private final String url;
    private final String id;

    public File(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }


}
