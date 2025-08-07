package com.example.filemanagerbylufic;

import android.net.Uri;
public class RecentFilesModel {
    private String fileName;
    private Uri fileUri;
    private String fileType;
    private long dateAdded;
    private String folderName;

    public RecentFilesModel(String fileName, Uri fileUri, String fileType, long dateAdded, String folderName) {
        this.fileName = fileName;
        this.fileUri = fileUri;
        this.fileType = fileType;
        this.dateAdded = dateAdded;
        this.folderName = folderName;
    }

    public String getFileName() {
        return fileName;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public String getFileType() {
        return fileType;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public String getFolderName() {
        return folderName;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecentFilesModel that = (RecentFilesModel) o;

        return fileUri.equals(that.fileUri);
    }

    @Override
    public int hashCode() {
        return fileUri.hashCode();
    }

}