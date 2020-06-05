package com.demo.zhuaqudemo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.abroad.crawllibrary.main.CommonUtil;

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
                     //String res = CommonUtil.getCommLocation().toString();
                     //Log.e("xxx", "result--：" + res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
