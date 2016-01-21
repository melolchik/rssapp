package ru.android.rssapp.service;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

import ru.android.rssapp.bean.RssChannel;

public class TaskResult implements Parcelable {

    public static final Creator<TaskResult> CREATOR = new Creator<TaskResult>() {
        public TaskResult createFromParcel(Parcel in) {
            return new TaskResult(in);
        }

        @Override
        public TaskResult[] newArray(int size) {
            return new TaskResult[size];
        }
    };

    public static final int ERROR_NULL = 0;
    public static final int ERROR_NETWORK = 1;
    public static final int ERROR_PARSE_DATA = 2;
    public static final int ERROR_NOT_RSS = 3;
    private final String mURL;
    private final RssChannel mResult;
    private final int mErrorType;

    static TaskResult createOkTaskResult(String url,RssChannel result){
        return new TaskResult(url,result,ERROR_NULL);
    }

    static TaskResult createFailTaskResult(String url,int errorType){
        return new TaskResult(url,new RssChannel(url),errorType);
    }

    public TaskResult(String url,RssChannel result, int errorType) {

        mURL = url;
        mResult = result;
        mErrorType = errorType;
    }

    public TaskResult(Parcel in) {
        mURL = in.readString();
        mErrorType = in.readInt();
        mResult = in.readParcelable(Parcelable.class.getClassLoader());


    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTaskURL());
        dest.writeInt(getErrorType());
        if(getResult()!= null) {
            dest.writeParcelable(getResult(), Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }
    }

    public RssChannel getResult()
    {
        return mResult;
    }

    public String getTaskURL() {
        return mURL == null ? "" : mURL;
    }

    public int getErrorType() {
        return mErrorType;
    }

    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public String toString() {
        return "Task url : " + getTaskURL() + " result : " + (getErrorType() != ERROR_NULL ? "False" : "True") +
                "\nmResult = " + getResult() + " mErrorType: "+ mErrorType ;
    }
}
