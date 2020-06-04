package com.demo.zhuaqudemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abroad.crawllibrary.main.CrawlMainHandler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

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
