package ru.android.rssapp.bean;

import android.text.TextUtils;

/**
 * Created by Olga on 21.01.2016.
 */
public abstract class BaseEntity {
    private String mTitle;
    private String mDescription;
    public String getTitle() {
        return TextUtils.isEmpty(mTitle) ? "" : mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getDescription() {
        return mDescription == null ? "" : mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

}
