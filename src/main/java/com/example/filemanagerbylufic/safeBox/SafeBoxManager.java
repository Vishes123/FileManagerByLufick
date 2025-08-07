package com.example.filemanagerbylufic.safeBox;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.filemanagerbylufic.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SafeBoxManager {

    private final Context context;
    private final File safeBoxDir;
    private final SafeBoxDbHelper dbHelper;

    public SafeBoxManager(Context context) {
        this.context = context;
        this.safeBoxDir = new File(context.getFilesDir(), "SafeBox");
        if (!safeBoxDir.exists()) safeBoxDir.mkdirs();
        this.dbHelper = new SafeBoxDbHelper(context);
    }

    public void showSafeBoxDialog(File fileOrFolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_safe_box_setup, null);
        builder.setView(view);

        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etConfirm = view.findViewById(R.id.etConfirmPassword);
        TextView etQuestion = view.findViewById(R.id.etSecurityQuestion);
        EditText etAnswer = view.findViewById(R.id.etAnswer);

        builder.setTitle("Add to Safe Box");
        builder.setPositiveButton("OK", (dialog, which) -> {
            String pass = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();
            String question = etQuestion.getText().toString();
            String answer = etAnswer.getText().toString();

            if (!pass.equals(confirm)) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            saveCredentials(pass, question, answer);
            moveToSafeBox(fileOrFolder);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveCredentials(String password, String question, String answer) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("safebox_credentials", null, null);

        ContentValues values = new ContentValues();
        values.put("password", password);
        values.put("question", question);
        values.put("answer", answer);
        db.insert("safebox_credentials", null, values);
        db.close();
    }

    private void moveToSafeBox(File original) {
        File dest = new File(safeBoxDir, original.getName());
        boolean success = false;

        try {
            if (original.isDirectory()) {
                success = copyFolder(original, dest);
            } else {
                success = copyFile(original, dest);
            }

            if (success) {
                saveFileInfo(original, dest);
                deleteRecursive(original);
                Toast.makeText(context, "Moved to SafeBox", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to move", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean copyFile(File source, File dest) throws IOException {
        try (FileInputStream in = new FileInputStream(source);
             FileOutputStream out = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            return true;
        }
    }

    private boolean copyFolder(File source, File dest) throws IOException {
        if (!dest.mkdirs()) return false;
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                File target = new File(dest, file.getName());
                if (file.isDirectory()) {
                    if (!copyFolder(file, target)) return false;
                } else {
                    if (!copyFile(file, target)) return false;
                }
            }
        }
        return true;
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    private void saveFileInfo(File original, File dest) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("file_name", original.getName());
        values.put("file_path", dest.getAbsolutePath());
        values.put("original_path", original.getAbsolutePath());
        values.put("is_folder", original.isDirectory() ? 1 : 0);
        values.put("date_added", System.currentTimeMillis());

        db.insert("safe_files", null, values);
        db.close();
    }

    public boolean verifyLogin(String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT password FROM safebox_credentials", null);
        boolean isValid = false;

        if (cursor.moveToFirst()) {
            String saved = cursor.getString(0);
            isValid = saved.equals(password);
        }

        cursor.close();
        db.close();
        return isValid;
    }

    public List<File> getSafeBoxFiles() {
        List<File> files = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT file_path FROM safe_files", null);
        while (cursor.moveToNext()) {
            File f = new File(cursor.getString(0));
            if (f.exists()) files.add(f);
        }

        cursor.close();
        db.close();
        return files;
    }

    public boolean restoreFile(File safeFile) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT original_path FROM safe_files WHERE file_path = ?",
                new String[]{safeFile.getAbsolutePath()});

        if (cursor.moveToFirst()) {
            String originalPath = cursor.getString(0);
            File originalFile = new File(originalPath);
            try {
                if (safeFile.isDirectory()) {
                    if (copyFolder(safeFile, originalFile)) {
                        deleteRecursive(safeFile);
                        cursor.close();
                        db.close();
                        return true;
                    }
                } else {
                    if (copyFile(safeFile, originalFile)) {
                        safeFile.delete();
                        cursor.close();
                        db.close();
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        cursor.close();
        db.close();
        return false;
    }

    public static class SafeBoxDbHelper extends SQLiteOpenHelper {

        private static final String DB_NAME = "safebox.db";
        private static final int DB_VERSION = 1;

        public SafeBoxDbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE safe_files (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "file_name TEXT," +
                    "file_path TEXT," +
                    "original_path TEXT," +
                    "is_folder INTEGER," +
                    "date_added INTEGER)");

            db.execSQL("CREATE TABLE safebox_credentials (" +
                    "password TEXT," +
                    "question TEXT," +
                    "answer TEXT)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS safe_files");
            db.execSQL("DROP TABLE IF EXISTS safebox_credentials");
            onCreate(db);
        }
    }
    public File getSafeBoxDir() {
        return safeBoxDir;
    }




    public SafeBoxDbHelper getDbHelper() {
        return dbHelper;
    }


}
