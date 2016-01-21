package ru.android.rssapp.dao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;


public class DataContentProvider extends ContentProvider {
    public static final String AUTHORITY = "ru.android.rssapp.provider";
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int RSS_CHANNELS = 100;
    private static final int RSS_CHANNELS_ID = 101;
    private static final int RSS_ITEMS = 200;
    private static final int RSS_ITEMS_ID = 201;

    static {
        sURIMatcher.addURI(AUTHORITY, Table.RssChannels.TABLE_NAME + "/#", RSS_CHANNELS_ID);
        sURIMatcher.addURI(AUTHORITY, Table.RssChannels.TABLE_NAME, RSS_CHANNELS);
        sURIMatcher.addURI(AUTHORITY, Table.RssItems.TABLE_NAME + "/#", RSS_ITEMS_ID);
        sURIMatcher.addURI(AUTHORITY, Table.RssItems.TABLE_NAME, RSS_ITEMS);
    }

    private DBHelper dbHelper;
    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        dbHelper = new DBHelper(mContext);
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB;
        int rowsDeleted;
        switch (uriType) {
            case RSS_CHANNELS: {
                sqlDB = dbHelper.getWritableDatabase();
                rowsDeleted = sqlDB.delete(Table.RssChannels.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case RSS_CHANNELS_ID: {
                sqlDB = dbHelper.getWritableDatabase();
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(Table.RssChannels.TABLE_NAME, BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(Table.RssChannels.TABLE_NAME, BaseColumns._ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            }

            case RSS_ITEMS: {
                sqlDB = dbHelper.getWritableDatabase();
                rowsDeleted = sqlDB.delete(Table.RssItems.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case RSS_ITEMS_ID: {
                sqlDB = dbHelper.getWritableDatabase();
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(Table.RssItems.TABLE_NAME, BaseColumns._ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(Table.RssItems.TABLE_NAME, BaseColumns._ID + "=" + id
                            + " and " + selection, selectionArgs);
                }
                break;
            }

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB;
        Uri outUri;
        long id;
        switch (uriType) {
            case RSS_CHANNELS: {
                sqlDB = dbHelper.getWritableDatabase();
                id = sqlDB.insert(Table.RssChannels.TABLE_NAME, null, values);
                outUri = Uri.parse(Table.RssChannels.TABLE_NAME + "/" + id);
                break;
            }
            case RSS_ITEMS: {
                sqlDB = dbHelper.getWritableDatabase();
                id = sqlDB.insert(Table.RssItems.TABLE_NAME, null, values);
                outUri = Uri.parse(Table.RssItems.TABLE_NAME + "/" + id);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return outUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        int uriType = sURIMatcher.match(uri);
        String groupBy = null;
        SQLiteDatabase db;
        switch (uriType) {
            case RSS_CHANNELS_ID: {
                queryBuilder.appendWhere(BaseColumns._ID + "="
                        + uri.getLastPathSegment());
                db = dbHelper.getReadableDatabase();
                queryBuilder.setTables(Table.RssChannels.TABLE_NAME);
                break;
            }
            case RSS_CHANNELS: {
                db = dbHelper.getReadableDatabase();
                queryBuilder.setTables(Table.RssChannels.TABLE_NAME);
                break;
            }
            case RSS_ITEMS_ID: {
                queryBuilder.appendWhere(BaseColumns._ID + "="
                        + uri.getLastPathSegment());
                db = dbHelper.getReadableDatabase();
                queryBuilder.setTables(Table.RssItems.TABLE_NAME);
                break;
            }
            case RSS_ITEMS: {
                db = dbHelper.getReadableDatabase();
                queryBuilder.setTables(Table.RssItems.TABLE_NAME);
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, groupBy, null, sortOrder);
        cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB;
        int rowsUpdated;
        switch (uriType) {
            case RSS_CHANNELS: {
                sqlDB = dbHelper.getWritableDatabase();
                rowsUpdated = sqlDB.update(Table.RssChannels.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case RSS_ITEMS: {
                sqlDB = dbHelper.getWritableDatabase();
                rowsUpdated = sqlDB.update(Table.RssItems.TABLE_NAME, values, selection, selectionArgs);
                break;
            }

            default: {
                throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
