package ru.android.rssapp.dao;

import android.database.Cursor;

public class SQLHelper {

    public static String getString(Cursor cursor, String columnName, String defaultValue) {
        int indx = cursor.getColumnIndex(columnName);
        if (indx > -1) {
            return cursor.getString(indx);
        }
        return defaultValue;
    }

    public static float getFloat(Cursor cursor, String columnName, float defaultValue) {
        int indx = cursor.getColumnIndex(columnName);
        if (indx > -1) {
            return cursor.getFloat(indx);
        }
        return defaultValue;
    }

    public static int getInt(Cursor cursor, String columnName, int defaultValue) {
        int indx = cursor.getColumnIndex(columnName);
        if (indx > -1) {
            return cursor.getInt(indx);
        }
        return defaultValue;
    }

    public static boolean getBoolean(Cursor cursor, String columnName, boolean defaultValue) {
        int indx = cursor.getColumnIndex(columnName);
        if (indx > -1) {
            return cursor.getInt(indx) == 1;
        }
        return defaultValue;
    }

    public static long getLong(Cursor cursor, String columnName, long defaultValue) {
        int indx = cursor.getColumnIndex(columnName);
        if (indx > -1) {
            return cursor.getLong(indx);
        }
        return defaultValue;
    }

    public static Double getDouble(Cursor cursor, String columnName, Double defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index > -1) {
            return cursor.isNull(index) ? null : cursor.getDouble(index);
        }
        return defaultValue;
    }
    public static Integer getInteger(Cursor cursor, String columnName, Integer defaultValue) {
        int index = cursor.getColumnIndex(columnName);
        if (index > -1) {
            return  cursor.isNull(index) ? null : cursor.getInt(index);
        }
        return defaultValue;
    }
}
