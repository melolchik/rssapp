package ru.android.rssapp.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ru.android.rssapp.AppLogger;
import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.bean.RssItem;
import ru.android.rssapp.dao.ContentValuesHelper;
import ru.android.rssapp.dao.Table;

/**
 * Created by Olga on 20.01.2016.
 */
public class Task implements Runnable {

    public final static String XML_CHANNEL = "CHANNEL";
    public final static String XML_ITEM = "ITEM";
    public static final String XML_TITLE = "TITLE";
    public static final String XML_DESCRIPTION = "DESCRIPTION";
    public static final String XML_LAST_BUILD_DATE = "LASTBUILDDATE";
    public static final String XML_PUB_DATE = "PUBDATE";

    private final Context mContext;
    private final String mUrl;


    public Task(Context context, String url) {
        mContext = context;
        mUrl = url;
    }

    byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/xml");
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }

    public String getUrl(String urlSpec) throws IOException,NullPointerException {
        return new String(getUrlBytes(urlSpec));
    }

    @Override
    public void run() {
        int errorType = TaskResult.ERROR_NULL;
        RssChannel channel = new RssChannel(mUrl);
        log("downloadChannel url:" + mUrl);
        try {
            String xmlString = getUrl(mUrl);
            //log("Recived xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));
            if(parseItems(channel, parser)) {
                saveData(channel);
            }else{
                errorType = TaskResult.ERROR_NOT_RSS;
            }
        } catch (IOException ioe) {
            errorType = TaskResult.ERROR_NETWORK;
            log("Failed to load " + ioe);
        }catch (NullPointerException inp){
            errorType = TaskResult.ERROR_NETWORK;
            log("Failed to load " + inp);
        }
        catch (XmlPullParserException xppe) {
            errorType = TaskResult.ERROR_PARSE_DATA;
            log("Failed to parse " + xppe);
        }
        TaskResult result = new TaskResult(mUrl, channel, errorType);
        sendResult(result);
    }

    protected void saveData(RssChannel channel) {
        if (channel == null)
            return;
        channel.setLastUpdateTime(System.currentTimeMillis());
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Table.RssChannels.CONTENT_URI;
        //resolver.update(Table.RssChannels.CONTENT_URI,Table.RssChannels.COLUMN_URL = channel.getURL())
        ContentValues values = ContentValuesHelper.convertRssChannel(channel);
        int count = contentResolver.update(uri, values
                , Table.RssChannels.COLUMN_URL + " like ?", new String[]{channel.getURL()});
        if (count == 0) {
            contentResolver.insert(uri, values);
        }
        log("channel.itemList : " + channel.getItemList());
        if(channel.getItemList() != null && !channel.getItemList().isEmpty()){
            for(RssItem item : channel.getItemList()){
                item.setParentURL(channel.getURL());
                updateItem(item);
            }
        }

    }

    protected void updateItem(RssItem item){
        ContentResolver contentResolver = mContext.getContentResolver();
        Uri uri = Table.RssItems.CONTENT_URI;
        //resolver.update(Table.RssChannels.CONTENT_URI,Table.RssChannels.COLUMN_URL = channel.getURL())
        ContentValues values = ContentValuesHelper.convertRssItem(item);
        int count = contentResolver.update(uri, values
                , Table.RssItems.COLUMN_TITLE + " like ?", new String[]{item.getTitle()});
        if (count == 0) {
            contentResolver.insert(uri, values);
        }
    }
    private boolean parseItems(RssChannel channel, XmlPullParser parser)
            throws XmlPullParserException, IOException {
        int eventType = parser.next();
        boolean intoChannel = false;
        boolean isRSSLink = false;
        RssItem currentRssItem = null;
        List<RssItem> rssItemList = new ArrayList<>();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                switch (parser.getName().toUpperCase()) {
                    case XML_CHANNEL:
                        isRSSLink = true;
                        intoChannel = true;
                        break;
                    case XML_ITEM:
                        intoChannel = false;
                        if(isRSSLink) {
                            currentRssItem = new RssItem();
                        }
                        break;
                    case XML_TITLE:
                        String title = parser.nextText();
                        if (intoChannel) {
                            channel.setTitle(title);
                        } else if (currentRssItem != null) {
                            currentRssItem.setTitle(title);
                        }
                        break;
                    case XML_DESCRIPTION:

                        String descr = parser.nextText();
                        if (intoChannel) {
                            channel.setDescription(descr);
                        } else if (currentRssItem != null) {
                            currentRssItem.setDescription(descr);
                        }
                        break;
                    case XML_LAST_BUILD_DATE:
                        String date = parser.nextText();
                        if (intoChannel) {
                            channel.setLastBuildDate(date);
                        }
                        break;
                    case XML_PUB_DATE:
                        String pubdate = parser.nextText();
                        if(currentRssItem != null){
                            currentRssItem.setPubdate(pubdate);
                        }
                        break;
                    default:
                        break;

                }
            } else if (eventType == XmlPullParser.END_TAG) {

                switch (parser.getName().toUpperCase()) {
                    case XML_ITEM:
                        //log("parseItems currentRssItem = "+currentRssItem);
                        if (currentRssItem != null) {
                            rssItemList.add(currentRssItem);
                            currentRssItem = null;
                        }
                        break;
                    case XML_CHANNEL:
                        log("parseItems rssItemList = "+rssItemList);
                        channel.setItemList(rssItemList);
                        break;
                }

            }
            eventType = parser.next();
        }
        return  isRSSLink;
    }

    public void sendResult(TaskResult result) {
        Intent intent = new Intent(DataService.DEFAULT_INTENT_EVENT);
        intent.putExtra(DataService.DEFAULT_INTENT_DATA, result);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    protected void log(String message) {
        AppLogger.log(getClass().getSimpleName() + " " + message);
    }
}
