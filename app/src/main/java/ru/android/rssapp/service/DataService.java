package ru.android.rssapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ru.android.rssapp.AppLogger;
import ru.android.rssapp.bean.RssChannel;


public class DataService extends Service {

    public static final String DEFAULT_INTENT_DATA = "ru.android.rssapp.service.DEFAULT_INTENT_DATA";
    public static final String DEFAULT_INTENT_EVENT = "ru.android.rssapp.service.DEFAULT_INTENT_EVENT";

    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 3;
    private static final long KEEP_ALIVE_TIME = 10L;

    private MyBinder mBinder = new MyBinder();
    private ThreadPoolExecutor mPoolExecutor;


    @Override
    public void onCreate() {
        super.onCreate();
        mPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public void downloadChannel(String url) {
        mPoolExecutor.execute(new Task(getApplicationContext(),url));
    }

    public class MyBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    }

    protected void log(String message) {
        AppLogger.log(getClass().getSimpleName() + " " + message);
    }


}
