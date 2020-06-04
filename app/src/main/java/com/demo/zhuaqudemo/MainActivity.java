package com.demo.zhuaqudemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abroad.crawllibrary.main.CrawlMainHandler;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String res = CrawlMainHandler.getDeviceInfo();
                    Log.e("xxx", "电池：");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
