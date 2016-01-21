package ru.android.rssapp.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.android.rssapp.AppLogger;

public class ServiceManager implements ServiceConnection {

    protected BroadcastReceiver mBroadcastReceiver;
    private Map<String, IServiceCallbackListener> mRequestMap = new HashMap<String, IServiceCallbackListener>();
    private Context mContext;
    private IntentFilter mIntentFilter;
    private DataService mDataService = null;
    private List<String> mQueueTaskList;

    private static ServiceManager sInstance;

    public static void createInstance(Context context) {
        sInstance = new ServiceManager(context);
    }

    public static ServiceManager getInstance() {
        return sInstance;
    }

    private ServiceManager(Context context) {
        mContext = context;
        mQueueTaskList = new ArrayList<String>();
        initTransport();
        //mRunManager = RunManager.get(context);
    }

    private void initTransport() {

        mIntentFilter = new IntentFilter(DataService.DEFAULT_INTENT_EVENT);

        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                TaskResult taskResult = intent.getParcelableExtra(DataService.DEFAULT_INTENT_DATA);
                log("ServiceManager: receiver " + taskResult.getTaskURL());
                IServiceCallbackListener callbackListener = mRequestMap.remove(taskResult.getTaskURL());
                if (callbackListener != null && taskResult != null) {
                    callbackListener.onServiceCallback(taskResult);
                }
                //log("receiver Message " + taskResult + " " + mRequestMap.size());
            }
        };
    }

    public int getCountQueueTaskInfo() {
        return mQueueTaskList.size();
    }

    public void onResume() {
        log("onResume ");
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mBroadcastReceiver, mIntentFilter);
        if (mDataService == null) {
            mContext.bindService(new Intent(mContext, DataService.class), this, Context.BIND_AUTO_CREATE);
        }

    }

    public void onPause() {
        log("onPause ");
        if (mDataService != null) {
            mContext.unbindService(this);
            mDataService = null;
        }
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // mRunManager.startLocationUpdates();
        log("onServiceConnected");
        DataService.MyBinder binder = (DataService.MyBinder) service;
        mDataService = binder.getService();

        if (mQueueTaskList.size() > 0) {
            for (int i = 0; i < mQueueTaskList.size(); i++) {
                String taskInfoItem = mQueueTaskList.get(i);
                mDataService.downloadChannel(taskInfoItem);
            }
            mQueueTaskList.clear();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //mRunManager.stopLocationUpdates();
        mDataService = null;
        log("onServiceDisconnected");
    }


    public void invokeRequest(String url, IServiceCallbackListener iServiceCallbackListener) {

        log("invokeRequest url = " + url);
        ArrayList<IServiceCallbackListener> callbackArray = new ArrayList<>();
        callbackArray.add(iServiceCallbackListener);
        mRequestMap.put(url, iServiceCallbackListener);

        if (mDataService != null) {
            mDataService.downloadChannel(url);
        } else {
            if (!mQueueTaskList.contains(url)) {
                mQueueTaskList.add(url);
            }
        }
    }


    public void cancelAllRequest() {
       /* mQueueTaskList.clear();

        for (TaskInfo key : mRequestMap.keySet()) {
            mDataService.cancelTask(key);
        }
        mRequestMap.clear();*/
    }

    protected void log(String message) {
        AppLogger.log(getClass().getSimpleName() + " " + message);
    }

}