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
public class ItemsLoader extends DataLoader<List<BaseEntity>>{
    protected final String mParentUrl;

    public ItemsLoader(Context context,String parentUrl){
        super(context);
        mParentUrl = parentUrl;
    }
    @Override
    public List<BaseEntity> loadInBackground() {
        List<BaseEntity> result = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(
                Table.RssItems.CONTENT_URI, //uri
                new String[]{Table.RssItems.COLUMN_ID, Table.RssItems.COLUMN_TITLE, Table.RssItems.COLUMN_PUB_DATE}, //list of column
                Table.RssItems.COLUMN_PARENT_URL + " like ?", // where
                new String[]{mParentUrl}, // args for where
                Table.RssItems.COLUMN_ID + " desc"); // order TODO:
        if (cursor != null && cursor.moveToFirst()) {
            do {
                RssItem item = new RssItem();
                item.setTitle(SQLHelper.getString(cursor, Table.RssItems.COLUMN_TITLE, ""));
                item.setPubdate(SQLHelper.getString(cursor, Table.RssItems.COLUMN_PUB_DATE, ""));
                item.setId(SQLHelper.getInt(cursor,Table.RssItems.COLUMN_ID,0));
                result.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }
}
