package ru.android.rssapp.dao;

import android.content.ContentValues;

import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.bean.RssItem;

/**
 * Created by Olga on 20.01.2016.
 */
public class ContentValuesHelper {

    public static ContentValues convertRssChannel(RssChannel channel) {
        ContentValues values = new ContentValues();
        values.put(Table.RssChannels.COLUMN_TITLE, channel.getTitle());
        values.put(Table.RssChannels.COLUMN_DESCRIPTION, channel.getDescription());
        values.put(Table.RssChannels.COLUMN_URL, channel.getURL());
        values.put(Table.RssChannels.COLUMN_LAST_BUILD_DATE,channel.getLastBuildDate());
        values.put(Table.RssChannels.COLUMN_LAST_UPDATE_TIME,channel.getLastUpdateTime());
        return values;
    }

    public static ContentValues convertRssItem(RssItem item) {
        ContentValues values = new ContentValues();
        values.put(Table.RssItems.COLUMN_TITLE, item.getTitle());
        values.put(Table.RssItems.COLUMN_DESCRIPTION, item.getDescription());
        values.put(Table.RssItems.COLUMN_PARENT_URL, item.getParentURL());
        values.put(Table.RssItems.COLUMN_PUB_DATE, item.getPubdate());
        return values;
    }
}
