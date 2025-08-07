package com.example.filemanagerbylufic.tresh;

public class TrashModel {
    private String path;
    private String originalPath;
    private String name;
    private String size;
    private String deletedAt;
    public TrashModel(String path, String name, String size, String deletedAt) {
        this.path = path;
        this.name = name;
        this.size = size;
        this.deletedAt = deletedAt;
    }

    public TrashModel(String path, String originalPath, String name, String size, String deletedAt) {
        this.path = path;
        this.originalPath = originalPath;
        this.name = name;
        this.size = size;
        this.deletedAt = deletedAt;
    }

    public TrashModel() {

    }


    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }

    public String getOriginalPath() {
        return originalPath;
    }
}
