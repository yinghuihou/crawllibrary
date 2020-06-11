package com.demo.zhuaqudemo;

import android.app.Application;

import com.stu.lon.lib.CrawlMainHandler;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrawlMainHandler.init(this);
    }
}
