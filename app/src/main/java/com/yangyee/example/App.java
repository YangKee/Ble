package com.yangyee.example;

import android.app.Application;

/**
 * author: Yangxusong
 * created on: 2018/9/7 0007
 */
public class App extends Application {
    public static App instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static App getInstance() {
        return instance;
    }
}
