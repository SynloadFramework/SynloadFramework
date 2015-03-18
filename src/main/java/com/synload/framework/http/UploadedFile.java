package com.synload.framework.http;

public class UploadedFile {
    public String name, path, tempName, user = "";
    public long size = 0;

    public UploadedFile(String name, String path, String tempName, String user,
            long size) {
        this.size = size;
        this.name = name;
        this.user = user;
        this.path = path;
        this.tempName = tempName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTempName() {
        return tempName;
    }

    public void setTempName(String tempName) {
        this.tempName = tempName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
