package com.example.chiennt.musicplayer.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;

import de.greenrobot.event.EventBus;

/**
 * Created by Nguyen on 11/07/2015.
 */
public abstract class BaseActivity extends AppCompatActivity {

    abstract int getLayoutSource(Bundle savedInstanceState);

    abstract void initVaribles(Bundle saveInstanceState);

    abstract void initData(Bundle saveInstanceState);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutSource(savedInstanceState));
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        initVaribles(savedInstanceState);
        initData(savedInstanceState);
    }

    /**
     * Start Activity with bundle
     *
     * @param clazz
     * @param bundle
     */
    protected void startActivity(Class clazz, Bundle bundle) {
        try {
            Intent intent = new Intent(this, clazz);
            if (bundle != null)
                intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Start Activity without bundle
     *
     * @param clazz
     */
    protected void startActivity(Class clazz) {
        startActivity(clazz, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
