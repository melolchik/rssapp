package ru.android.rssapp;

import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.List;
import butterknife.InjectView;
import ru.android.rssapp.adapters.ChannelAdapter;
import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.bean.RssItem;
import ru.android.rssapp.dao.ItemsLoader;
import ru.android.rssapp.service.IServiceCallbackListener;
import ru.android.rssapp.service.ServiceManager;
import ru.android.rssapp.service.TaskResult;

/**
 * Created by Olga on 20.01.2016.
 */
public class ItemsListFragment extends BaseFragment implements IServiceCallbackListener,AdapterView.OnItemClickListener {

    protected final static String ARG_RSSCHANNEL = "rss_channel";
    @InjectView(R.id.channel_list)
    protected ListView mItemListView;

    @InjectView(R.id.progressbar)
    protected ProgressBar mProgressBar;

    protected ChannelAdapter mAdapter;
    protected RssChannel mRssChannel;


    public static BaseFragment newInstance(RssChannel channel) {

        ItemsListFragment fragment = new ItemsListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RSSCHANNEL, channel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null){
            mRssChannel = args.getParcelable(ARG_RSSCHANNEL);
        }

    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.fragment_channel_list;
    }

    @Override
    protected void onCreateView(View mainView) {
        mAdapter = new ChannelAdapter(getActivity());
        mItemListView.setAdapter(mAdapter);
        mItemListView.setOnItemClickListener(this);
        if(mRssChannel != null && mRssChannel.needUpdate()) {
            loadChannel();
        }else {
            startLoadFromDB();
        }
    }

    @Override
    protected Loader<List<BaseEntity>> onCreateLoader() {
        if(mRssChannel == null)
            return null;
        return new ItemsLoader(getActivity(),mRssChannel.getURL());
    }
    @Override
    public void onLoadFinished(Loader<List<BaseEntity>> loader, List<BaseEntity> data) {
        if(data.isEmpty()) {
            loadChannel();
        }else {
            mAdapter.clear();
            mAdapter.addAll(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    protected void loadChannel() {

        if(mRssChannel == null)
            return ;
        showProgressBar(true);
        ServiceManager.getInstance().invokeRequest(mRssChannel.getURL(), this);

    }
    @Override
    public void onServiceCallback(TaskResult taskResult) {
        if (taskResult == null)
            return;
        log(" onServiceCallback " + taskResult.getResult());
        showProgressBar(false);
        if (taskResult.getErrorType() == TaskResult.ERROR_NULL) {
            startLoadFromDB();
        }else {
            showLoadingError(taskResult.getErrorType());
        }
    }

    protected void showProgressBar(boolean isShow){
        if(mProgressBar == null)
            return;
        mProgressBar.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        log("onClick position = " + position);
        RssItem clickItem = (RssItem)mAdapter.getItem(position);
        if(getActivity() == null)
            return;
        if(clickItem == null)
            return;
        MainActivity activity = (MainActivity) getActivity();
        activity.addFragment(OneItemFragment.newInstance(clickItem.getId()));
    }
}
