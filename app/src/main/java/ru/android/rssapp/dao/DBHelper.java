package ru.android.rssapp.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ru.android.rssapp.db";
    private static final int DATABASE_VERSION = 3;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Table.RssChannels.onCreate(db);
        Table.RssItems.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Table.RssChannels.onUpgrade(db, oldVersion, newVersion);
        Table.RssItems.onUpgrade(db, oldVersion, newVersion);
    }
}
