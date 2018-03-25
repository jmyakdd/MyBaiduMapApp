package com.jmy.mybaidumapapp;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by jmy on 2018/3/16.
 */

public class App extends Application{

    @Override
    public void onCreate() {
        SDKInitializer.initialize(getApplicationContext());
        super.onCreate();
    }
}
