package ru.android.rssapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import butterknife.ButterKnife;
import ru.android.rssapp.bean.BaseEntity;
import ru.android.rssapp.dao.ItemsLoader;
import ru.android.rssapp.service.TaskResult;

/**
 * Created by Olga on 20.01.2016.
 */
abstract class BaseFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<BaseEntity>> {

    protected abstract void onCreateView(View mainView);

    protected abstract int getViewLayoutID();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(getViewLayoutID(), container, false);
        ButterKnife.inject(this, view);
        onCreateView(view);
        return view;
    }

    protected abstract Loader<List<BaseEntity>> onCreateLoader();

    protected void startLoadFromDB(){
        getActivity().getSupportLoaderManager().restartLoader(0, null, this);
    }
    @Override
    public Loader<List<BaseEntity>> onCreateLoader(int id, Bundle args) {
        return onCreateLoader();
    }

    @Override
    public void onLoadFinished(Loader<List<BaseEntity>> loader, List<BaseEntity> data) {
        log("onLoadFinished : data = " + data);
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    public void showErrorToast(int messageID) {
        if (messageID == 0)
            return;
        if (getActivity() == null)
            return;
        if (isVisible()) {
            Toast toast = Toast.makeText(getActivity(), messageID, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public  void showLoadingError(int errorType){
        switch (errorType){
            case TaskResult.ERROR_NETWORK:
                showErrorToast(R.string.error_internet);
                break;
            case TaskResult.ERROR_PARSE_DATA:
                showErrorToast(R.string.error_parsing);
                break;
            case TaskResult.ERROR_NOT_RSS:
                showErrorToast(R.string.error_no_rss);
                break;
        }
    }
    protected void log(String message) {
        AppLogger.log(getClass().getSimpleName() + " " + message);
    }
}
