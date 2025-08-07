package com.example.filemanagerbylufic;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileScanner {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface OnFilesLoadedListener {
        void onLoaded(List<RecentFilesModel> files);
    }


    public static void getRecentImagesAsync(Context context, OnFilesLoadedListener listener) {
        executor.execute(() -> {
            List<RecentFilesModel> result = getRecentImages(context);
            mainHandler.post(() -> listener.onLoaded(result));
        });
    }


    public static void getRecentVideosAsync(Context context, OnFilesLoadedListener listener) {
        executor.execute(() -> {
            List<RecentFilesModel> result = getRecentVideos(context);
            mainHandler.post(() -> listener.onLoaded(result));
        });
    }


    public static void getRecentDocumentsAsync(Context context, OnFilesLoadedListener listener) {
        executor.execute(() -> {
            List<RecentFilesModel> result = getRecentDocuments(context);
            mainHandler.post(() -> listener.onLoaded(result));
        });
    }

    // ---------------------
    // Existing sync methods
    // ---------------------
    public static List<RecentFilesModel> getRecentImages(Context context) {
        List<RecentFilesModel> recentImages = new ArrayList<>();
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.DATA
        };

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED);
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String name = cursor.getString(nameIndex);
                long dateAdded = cursor.getLong(dateIndex);
                String fullPath = cursor.getString(dataIndex);
                Uri contentUri = ContentUris.withAppendedId(uri, id);

                String folderName = "Unknown";
                if (fullPath != null) {
                    File parent = new File(fullPath).getParentFile();
                    if (parent != null) {
                        folderName = parent.getName();
                    }
                }

                recentImages.add(new RecentFilesModel(name, contentUri, "Images", dateAdded, folderName));
            }
            cursor.close();
        }

        return recentImages;
    }

    public static List<RecentFilesModel> getRecentVideos(Context context) {
        List<RecentFilesModel> recentVideos = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DATA
        };

        String sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC";

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String name = cursor.getString(nameIndex);
                long dateAdded = cursor.getLong(dateIndex);
                String fullPath = cursor.getString(dataIndex);
                Uri contentUri = ContentUris.withAppendedId(uri, id);

                String folderName = "Unknown";
                if (fullPath != null) {
                    File parent = new File(fullPath).getParentFile();
                    if (parent != null) {
                        folderName = parent.getName();
                    }
                }

                recentVideos.add(new RecentFilesModel(name, contentUri, "Videos", dateAdded, folderName));
            }
            cursor.close();
        }

        return recentVideos;
    }

    public static List<RecentFilesModel> getRecentDocuments(Context context) {
        List<RecentFilesModel> recentDocs = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");

        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA
        };

        String selection = MediaStore.Files.FileColumns.MIME_TYPE + " IN (?, ?, ?)";
        String[] selectionArgs = new String[]{
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };

        String sortOrder = MediaStore.Files.FileColumns.DATE_ADDED + " DESC";

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
            int dateIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED);
            int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String name = cursor.getString(nameIndex);
                long dateAdded = cursor.getLong(dateIndex);
                String fullPath = cursor.getString(dataIndex);
                Uri contentUri = ContentUris.withAppendedId(uri, id);

                String folderName = "Unknown";
                if (fullPath != null) {
                    File parent = new File(fullPath).getParentFile();
                    if (parent != null) {
                        folderName = parent.getName();
                    }
                }

                recentDocs.add(new RecentFilesModel(name, contentUri, "Documents", dateAdded, folderName));
            }
            cursor.close();
        }

        return recentDocs;
    }


}