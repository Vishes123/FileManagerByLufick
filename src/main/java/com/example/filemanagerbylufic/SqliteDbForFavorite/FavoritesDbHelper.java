package com.example.filemanagerbylufic.SqliteDbForFavorite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

public class FavoritesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "favorites";
    public static final String COLUMN_PATH = "path";

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_PATH + " TEXT PRIMARY KEY)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addFavorite(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PATH, path);
        db.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public void removeFavorite(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_PATH + "=?", new String[]{path});
    }
    public Set<String> getAllFavorites() {
        Set<String> favorites = new HashSet<>();
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_PATH},
                     null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                favorites.add(cursor.getString(0));
            }
        }
        return favorites;
    }


    public boolean isFavorite(String path) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null,
                COLUMN_PATH + "=?", new String[]{path},
                null, null, null);

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }
}
