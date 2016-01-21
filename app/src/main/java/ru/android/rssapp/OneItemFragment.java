package ru.android.rssapp;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import butterknife.InjectView;
import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.bean.RssItem;
import ru.android.rssapp.dao.OneItemLoader;

/**
 * Created by Olga on 21.01.2016.
 */
public class OneItemFragment extends BaseFragment {
    protected final static String ARG_ITEM_ID = "item_id";
    protected int mItemId;

    @InjectView(R.id.date)
    protected TextView mDate;
    @InjectView(R.id.title)
    protected TextView mTitle;
    @InjectView(R.id.description)
    protected TextView mDescription;

    @InjectView(R.id.progressbar)
    protected ProgressBar mProgressBar;

    public static BaseFragment newInstance(int id) {

        OneItemFragment fragment = new OneItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mItemId = args.getInt(ARG_ITEM_ID);
        }
    }

    @Override
    protected void onCreateView(View mainView) {
        startLoadFromDB();
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.fragment_one_item;
    }

    @Override
    protected Loader<List<BaseEntity>> onCreateLoader() {
        if (getActivity() == null)
            return null;
        return new OneItemLoader(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<List<BaseEntity>> loader, List<BaseEntity> data) {
        super.onLoadFinished(loader, data);
        if (data.isEmpty())
            return;
        RssItem item = (RssItem) data.get(0);
        mDate.setText(item.getPubdate());
        mTitle.setText(item.getTitle());
        mDescription.setText(item.getDescription());
    }
}
