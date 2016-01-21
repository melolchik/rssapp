package ru.android.rssapp.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.bean.RssItem;

/**
 * Created by Olga on 21.01.2016.
 */
public class OneItemLoader extends DataLoader<List<BaseEntity>>{
    protected final int mItemID;

    public OneItemLoader(Context context, int id){
        super(context);
        mItemID = id;
    }
    @Override
    public List<BaseEntity> loadInBackground() {
        List<BaseEntity> result = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(
                Table.RssItems.CONTENT_URI, //uri
                null, //all list of column
                Table.RssItems.COLUMN_ID + " = ?", // where
                new String[]{String.valueOf(mItemID)}, // args for where
                Table.RssItems.COLUMN_ID + " desc"); // order
        if (cursor != null && cursor.moveToFirst()) {
            do {
                RssItem item = new RssItem();
                item.setTitle(SQLHelper.getString(cursor, Table.RssItems.COLUMN_TITLE, ""));
                item.setPubdate(SQLHelper.getString(cursor, Table.RssItems.COLUMN_PUB_DATE, ""));
                item.setId(SQLHelper.getInt(cursor, Table.RssItems.COLUMN_ID, 0));
                item.setDescription(SQLHelper.getString(cursor, Table.RssItems.COLUMN_DESCRIPTION, ""));
                result.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }
}
