package ru.android.rssapp.dao;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

public class Table {

    public static class RssChannels {
        public static final String TABLE_NAME = "rss_channels";
        public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY
                + "/" + TABLE_NAME);
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_LAST_BUILD_DATE = "last_build_date";
        public static final String COLUMN_LAST_UPDATE_TIME = "last_update_time";

        private static final String DATABASE_CREATE = "create table "
                + TABLE_NAME
                + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_URL + " text,"
                + COLUMN_TITLE + " text,"
                + COLUMN_DESCRIPTION + " text,"
                + COLUMN_LAST_BUILD_DATE + " text,"
                + COLUMN_LAST_UPDATE_TIME + " integer default 0"
                + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(RssChannels.DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + RssChannels.TABLE_NAME);
            onCreate(database);
        }
    }

    public static class RssItems {
        public static final String TABLE_NAME = "rss_items";
        public static final Uri CONTENT_URI = Uri.parse("content://" + DataContentProvider.AUTHORITY
                + "/" + TABLE_NAME);
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PARENT_URL = "parent_url";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PUB_DATE = "pub_date";

        private static final String DATABASE_CREATE = "create table "
                + TABLE_NAME
                + "("
                + COLUMN_ID + " integer primary key autoincrement, "
                + COLUMN_PARENT_URL + " text,"
                + COLUMN_TITLE + " text,"
                + COLUMN_DESCRIPTION + " text,"
                + COLUMN_PUB_DATE + " text"
                + ");";

        public static void onCreate(SQLiteDatabase database) {
            database.execSQL(RssItems.DATABASE_CREATE);
        }

        public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                     int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + RssItems.TABLE_NAME);
            onCreate(database);
        }
    }

}
