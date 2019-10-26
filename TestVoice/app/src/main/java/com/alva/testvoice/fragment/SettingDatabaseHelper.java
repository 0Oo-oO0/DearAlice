package com.alva.testvoice.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SettingDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "alice";
    private static final int DB_VERSION = 1;

    public SettingDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE TEXTTOSPEACH (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, VALUE INTEGAR)");
            insertTTSItem(db, "Rate", 5);
            insertTTSItem(db, "Pitch", 5);
            insertTTSItem(db,"Switch",0);
        }
        if (oldVersion < 2) {
        }
    }

    private static void insertTTSItem(SQLiteDatabase db, String name, int value) {
        ContentValues TTSItem = new ContentValues();
        TTSItem.put("NAME", name);
        TTSItem.put("VALUE", value);
        db.insert("TEXTTOSPEACH", null, TTSItem);
    }
}
