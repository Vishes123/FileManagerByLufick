
package com.example.filemanagerbylufic.tresh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrashDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "trash.db";
    private static final int DATABASE_VERSION = 6;

    public static final String TABLE_NAME = "trash";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_ORIGINAL_PATH = "original_path";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_DELETED_AT = "deletedAt";

    public TrashDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PATH + " TEXT PRIMARY KEY, " +
                COLUMN_ORIGINAL_PATH + " TEXT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_SIZE + " TEXT, " +
                COLUMN_DELETED_AT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // ✅ Add to Trash with originalPath
    public void addToTrash(String path, String originalPath, String name, String size, String deletedAt) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATH, path);
        values.put(COLUMN_ORIGINAL_PATH, originalPath);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_SIZE, size);
        values.put(COLUMN_DELETED_AT, deletedAt);

        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }


    public int removeFromTrash(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_PATH + "=?", new String[]{path});
    }


    public List<TrashModel> getAllTrashItems() {
        List<TrashModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COLUMN_DELETED_AT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                TrashModel model = new TrashModel();
                model.setPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH)));
                model.setOriginalPath(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORIGINAL_PATH)));
                model.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                model.setSize(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SIZE)));
                model.setDeletedAt(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DELETED_AT)));
                list.add(model);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }


    public String getOriginalPath(String trashPath) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{COLUMN_ORIGINAL_PATH},
                COLUMN_PATH + "=?",
                new String[]{trashPath},
                null, null, null);

        String originalPath = null;
        if (cursor.moveToFirst()) {
            originalPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORIGINAL_PATH));
        }

        cursor.close();
        return originalPath;
    }


    public boolean isInTrash(String path) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_PATH + "=?", new String[]{path}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    public void deleteTrashOlderThan(int days) {
        long thresholdTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_DELETED_AT + " < ?", new String[]{String.valueOf(thresholdTime)});
    }


    public Set<String> getAllTrashPaths() {
        Set<String> trashPaths = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_PATH}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                trashPaths.add(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PATH)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trashPaths;
    }


    public void clearAllTrash() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}
