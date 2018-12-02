package com.production.crasher.myapplication;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

@SuppressLint("ParcelCreator")
public class NewsReceiver extends ResultReceiver {
    private MainActivity.NewsData data;
    public NewsReceiver(MainActivity.NewsData data) {
        super(new Handler());
        this.data = data;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData){
        data.displayMessage(resultCode,resultData);
    }
}
