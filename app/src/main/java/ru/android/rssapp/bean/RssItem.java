package ru.android.rssapp.bean;

/**
 * Created by Olga on 20.01.2016.
 */
public class RssItem extends BaseEntity{

    private int mId;
    private String mPubdate;
    private String mParentURL;

    public RssItem(){

    }

    public String getPubdate() {
        return mPubdate;
    }

    public void setPubdate(String mPubdate) {
        this.mPubdate = mPubdate;
    }

    public String getParentURL() {
        return mParentURL;
    }

    public void setParentURL(String mParentURL) {
        this.mParentURL = mParentURL;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
