package com.lucasleao.pocaws.core.domain.events;

import com.lucasleao.pocaws.core.repositories.FileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FileDeletedEventListener {


    private final FileRepository fileRepository;

    public FileDeletedEventListener(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @EventListener
    public void handleFileDeleteEvent(FileDeletedEvent event) {
        fileRepository.deleteById(event.getFileId());
    }
}
