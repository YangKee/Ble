package com.yangyee.example;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * author: Yangxusong
 * created on: 2018/9/6 0006
 */
public class BaseActivity extends AppCompatActivity {
    protected String TAG;
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.TAG = getClass().getSimpleName();
        this.mContext = this;
    }


}
