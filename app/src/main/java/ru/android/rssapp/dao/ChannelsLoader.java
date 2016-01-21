package ru.android.rssapp.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.bean.RssItem;

/**
 * Created by Olga on 21.01.2016.
 */
public class ChannelsLoader extends DataLoader<List<BaseEntity>>{

    public ChannelsLoader(Context context){
        super(context);
    }
    @Override
    public List<BaseEntity> loadInBackground() {
        List<BaseEntity> result = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
        Cursor cursor = resolver.query(
                Table.RssChannels.CONTENT_URI, //uri
                null, //list of column
                null, // where
                null, // args for where
                null); // order
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String url = SQLHelper.getString(cursor, Table.RssChannels.COLUMN_URL, "");
                RssChannel item = new RssChannel(url);
                item.setTitle(SQLHelper.getString(cursor, Table.RssChannels.COLUMN_TITLE, ""));
                item.setLastBuildDate(SQLHelper.getString(cursor, Table.RssChannels.COLUMN_LAST_BUILD_DATE, ""));
                item.setLastUpdateTime(SQLHelper.getLong(cursor,Table.RssChannels.COLUMN_LAST_UPDATE_TIME,0));
                result.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return result;
    }
}
