package ru.android.rssapp.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Olga on 20.01.2016.
 */
public class RssChannel extends BaseEntity implements Parcelable {

    public static int DEFAULT_EXPIRATION_TIME = 10 * 60 * 1000; // 1 h in ms

    private String mTitle;
    private String mDescription;
    private String mLastBuildDate;
    private String mURL;
    private long mLastUpdateTime;
    protected boolean isLoadingState = false;

    private List<RssItem> mItemList = null;

    public static final Creator<RssChannel> CREATOR = new Creator<RssChannel>() {
        public RssChannel createFromParcel(Parcel source) {
            return new RssChannel(source);
        }

        public RssChannel[] newArray(int size) {
            return new RssChannel[size];
        }
    };

    public RssChannel(String url){
        mURL = url;
    }
    protected RssChannel(Parcel in) {
        this.mTitle = in.readString();
        this.mDescription = in.readString();
        this.mLastBuildDate = in.readString();
        this.mURL = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mLastBuildDate);
        dest.writeString(mURL);
    }

    public String getURL() {
        return mURL == null ? "" : mURL;
    }

    public void setURL(String mURL) {
        this.mURL = mURL;
    }

    public String getLastBuildDate() {
        return mLastBuildDate;
    }

    public void setLastBuildDate(String mLastBuildDate) {
        this.mLastBuildDate = mLastBuildDate;
    }

    public long getLastUpdateTime() {
        return mLastUpdateTime;
    }

    public void setLastUpdateTime(long mLastUpdateTime) {
        this.mLastUpdateTime = mLastUpdateTime;
    }

    public boolean needUpdate(){
        if(mLastUpdateTime == 0)
            return true;
         return System.currentTimeMillis() - mLastUpdateTime > DEFAULT_EXPIRATION_TIME;

    }

    public boolean isLoadingState() {
        return isLoadingState;
    }

    public void setLoadingState(boolean loadingState) {
        isLoadingState = loadingState;
    }

    public List<RssItem> getItemList() {
        return mItemList == null ? new ArrayList<RssItem>() : mItemList;
    }

    public void setItemList(List<RssItem> itemList) {
        mItemList = itemList;
    }

    @Override
    public boolean equals(Object obj) {
        return  (obj instanceof RssChannel && this.getURL().equals(((RssChannel) obj).getURL()));
    }

    @Override
    public String toString() {
        return "mTitle = " + getTitle() + " mUrl = "+ getURL() + " mDescr = " + getDescription();
    }
}
