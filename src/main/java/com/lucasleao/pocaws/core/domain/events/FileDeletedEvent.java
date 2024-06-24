package com.lucasleao.pocaws.core.domain.events;

import org.springframework.context.ApplicationEvent;

public class FileDeletedEvent extends ApplicationEvent {

    private final String fileId;

    public FileDeletedEvent(Object source, String fileId) {
        super(source);
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }
}
