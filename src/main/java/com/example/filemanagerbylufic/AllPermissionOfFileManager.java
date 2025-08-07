package com.example.filemanagerbylufic;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AllPermissionOfFileManager {

    public static final int REQUEST_STORAGE_ALL = 101;
    public static final int REQUEST_IMAGE = 102;
    public static final int REQUEST_VIDEO = 103;
    public static final int REQUEST_AUDIO = 104;
    public static final int REQUEST_DOCUMENT = 105;
    public static final int REQUEST_DOCUMENT_TREE = 106;

    // ---------------------- General Full Storage Permission ----------------------
  /*  public static boolean hasFullStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void requestAllFilesAccessPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivity(intent);
            }
        } else {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_ALL);
        }
    }*/

    public static boolean hasFullStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ (API 30+)
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    public static void requestAllFilesAccessPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_STORAGE_ALL);
            } catch (Exception e) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, REQUEST_STORAGE_ALL);
            }
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_ALL);
        }
    }



//    public static boolean hasStoragePermission(Context context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
//                    && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
//                    && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
//        } else {
//            return true;
//        }
//    }
//
//    public static void requestStoragePermission(Activity activity) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            ActivityCompat.requestPermissions(activity, new String[]{
//                    Manifest.permission.READ_MEDIA_IMAGES,
//                    Manifest.permission.READ_MEDIA_VIDEO,
//                    Manifest.permission.READ_MEDIA_AUDIO
//            }, REQUEST_STORAGE_ALL );
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            ActivityCompat.requestPermissions(activity, new String[]{
//                    Manifest.permission.READ_EXTERNAL_STORAGE
//            }, REQUEST_STORAGE_ALL );
//        }
//    }




    // ---------------------- Individual Media Permissions ----------------------
    public static boolean hasImagePermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestImagePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_IMAGE);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_IMAGE);
        }
    }

    public static boolean hasVideoPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestVideoPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_VIDEO},
                    REQUEST_VIDEO);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_VIDEO);
        }
    }

    public static boolean hasAudioPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestAudioPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                    REQUEST_AUDIO);
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_AUDIO);
        }
    }

    // ---------------------- Document Access via SAF (No permission needed) ----------------------

    public static void openDocumentPicker(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        String[] mimeTypes = {
                "application/pdf",
                "text/plain",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        };
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        activity.startActivityForResult(intent, REQUEST_DOCUMENT);
    }


    public static void openDocumentTree(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION |
                Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        activity.startActivityForResult(intent, REQUEST_DOCUMENT_TREE);
    }



}


