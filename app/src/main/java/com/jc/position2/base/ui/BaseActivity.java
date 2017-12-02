package com.jc.position2.base.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

/**
 * Created by tconan on 16/4/13.
 */
public class BaseActivity extends AppCompatActivity {

    public BaseActivity activity;
    public static BaseActivity currentActivity;
    public long lastRefreshTime = 0;
    public static long dataUpdateTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        currentActivity = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }

    public void baseOnRefresh() {

    }

    public static synchronized void staticOnRefresh() {
        if (BaseActivity.currentActivity!=null) {
            BaseActivity.currentActivity.baseOnRefresh();
        }
    }
}
