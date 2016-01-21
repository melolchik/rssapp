package ru.android.rssapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import ru.android.rssapp.adapters.ChannelAdapter;
import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.bean.RssChannel;
import ru.android.rssapp.dao.ChannelsLoader;
import ru.android.rssapp.service.IServiceCallbackListener;
import ru.android.rssapp.service.ServiceManager;
import ru.android.rssapp.service.TaskResult;

/**
 * Created by Olga on 20.01.2016.
 */
public class ChannelListFragment extends BaseFragment implements AdapterView.OnItemClickListener,IServiceCallbackListener {

    public static final String[] DEFAULT_URL_LIST =
            {"https://news.yandex.ua/hardware.rss",
                    "http://www.vesti.ru/vesti.rss",
                    "http://www.itar-tass.com/rss/all.xml"
            };

    @InjectView(R.id.channel_list)
    protected ListView mChannelListView;

    @InjectView(R.id.progressbar)
    protected ProgressBar mProgressBar;

    protected String mSearchString = "http://www.svobodanews.ru/rss/";

    protected ChannelAdapter mAdapter;

    public static BaseFragment newInstance() {
        ChannelListFragment fragment = new ChannelListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getViewLayoutID() {
        return R.layout.fragment_channel_list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void onCreateView(View mainView) {
        mAdapter = new ChannelAdapter(getActivity());
        mChannelListView.setAdapter(mAdapter);
        mChannelListView.setOnItemClickListener(this);
        startLoadFromDB();
    }

    protected void loadDefaultChannels(){
        List<RssChannel> list = new ArrayList<>();
        for(String url : DEFAULT_URL_LIST) {
            list.add(new RssChannel(url));
        }
        mAdapter.addAll(list);
        if(!list.isEmpty()){
            for(RssChannel channel : list){
                loadChannel(channel);
            }
        }

    }


    protected void loadChannel(RssChannel rssChannel) {

        if (rssChannel == null)
            return;

        rssChannel.setLoadingState(true);
        mAdapter.notifyDataSetInvalidated();
        ServiceManager.getInstance().invokeRequest(rssChannel.getURL(), this);

    }


    protected void updateItem(RssChannel rssChannel,boolean addIfNotExist) {
        if (mAdapter == null)
            return;
        if (rssChannel == null)
            return;
        int position = mAdapter.getPosition(rssChannel);
        log(" updateItem position = " + position);
        if(position >= 0){
            RssChannel existChannel = (RssChannel) mAdapter.getItem(position);
            existChannel.setLoadingState(false);
            existChannel.setTitle(rssChannel.getTitle());
            existChannel.setLastBuildDate(rssChannel.getLastBuildDate());
        }else if(addIfNotExist){
            rssChannel.setLoadingState(false);
            mAdapter.add(rssChannel);
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_channels, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_action_add: {
                showAddDialog();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void showAddDialog(){
        if(getActivity() == null)
            return;
        final EditText editText = new EditText(getActivity());
        editText.setText(mSearchString);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(editText)
                .setTitle(R.string.input_rss)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mSearchString = editText.getText().toString().trim();
                        if(TextUtils.isEmpty(mSearchString))
                            return;
                        showProgressBar(true);
                        loadChannel(new RssChannel(mSearchString));
                    }
                });
        AlertDialog ad = builder.create();
        ad.show();
    }

    @Override
    public void onServiceCallback(TaskResult taskResult) {
        if (taskResult == null)
            return;
        RssChannel loadedChannel = taskResult.getResult();
        log(" onServiceCallback " + loadedChannel);
        updateItem(loadedChannel,taskResult.getErrorType() == TaskResult.ERROR_NULL);
        if (taskResult.getErrorType() != TaskResult.ERROR_NULL) {
            showLoadingError(taskResult.getErrorType());
        }
        showProgressBar(false);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        log("onClick position = " + position);
        RssChannel clickChannel = (RssChannel)mAdapter.getItem(position);
        if(getActivity() == null)
            return;
        MainActivity activity = (MainActivity) getActivity();
        activity.addFragment(ItemsListFragment.newInstance(clickChannel));


    }

    @Override
    protected Loader<List<BaseEntity>> onCreateLoader() {
        if(getActivity() == null)
            return null;
        return new ChannelsLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<BaseEntity>> loader, List<BaseEntity> data) {
        super.onLoadFinished(loader, data);
        if(data == null || data.isEmpty()){
            loadDefaultChannels();
        }else {
            mAdapter.clear();
            mAdapter.addAll(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    protected void showProgressBar(boolean isShow){
        if(mProgressBar == null)
            return;
        mProgressBar.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
    }
}
