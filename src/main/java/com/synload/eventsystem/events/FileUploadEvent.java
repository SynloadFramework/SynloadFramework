package com.synload.eventsystem.events;

import com.synload.eventsystem.EventClass;
import com.synload.eventsystem.Handler;
import com.synload.framework.http.modules.UploadedFile;

public class FileUploadEvent extends EventClass {
    public UploadedFile file;
    public String key = "";

    public FileUploadEvent(UploadedFile file, String key) {
        this.setHandler(Handler.EVENT);
        this.key = key;
        this.file = file;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}