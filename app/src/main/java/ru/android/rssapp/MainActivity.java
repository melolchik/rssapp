package ru.android.rssapp;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.android.rssapp.service.ServiceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServiceManager.createInstance(this);
        addFragment(ChannelListFragment.newInstance());
    }


    @Override
    protected void onResume() {
        super.onResume();
        ServiceManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ServiceManager.getInstance().onPause();

    }

    protected BaseFragment getCurrentFragment() {
        FragmentManager fm = getSupportFragmentManager();
        return (BaseFragment) fm.findFragmentById(R.id.fragmentContainer);
    }

    public void addFragment(BaseFragment newFragment) {
        FragmentManager fm = getSupportFragmentManager();
        BaseFragment current = getCurrentFragment();
        FragmentTransaction ft = fm.beginTransaction();

        if (current != null) {
            ft.hide(current);
        }
        ft.add(R.id.fragmentContainer, newFragment)
                .addToBackStack(newFragment.getClass().getSimpleName())
                .commit();

    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        log("onBackPressed count = " + count);
        if (count > 1) {
            fm.popBackStack();
            return;
        }
        finish();
        super.onBackPressed();
    }

    protected void log(String message) {
        AppLogger.log(getClass().getSimpleName() + " " + message);
    }
}
