package com.production.crasher.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.production.crasher.myapplication.App.CHANNEL_ID;


public class MainActivity extends AppCompatActivity {

    Intent intent;
    TextView txtview;
    MyResultReceiver resultReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);

        resultReceiver = new MyResultReceiver(null);

        txtview = (TextView) findViewById(R.id.txtview);

        intent = new Intent(this, NewsService.class);
        intent.putExtra("receiver", resultReceiver);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }

    class UpdateUI implements Runnable
    {
        String updateString;

        public UpdateUI(String updateString) {
            this.updateString = updateString;
        }
        public void run() {
            txtview.setText(updateString);
        }
    }

    class MyResultReceiver extends ResultReceiver
    {
        public MyResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultCode == 100){
                runOnUiThread(new UpdateUI(resultData.getString("start")));
            }
            else if(resultCode == 200){
                runOnUiThread(new UpdateUI(resultData.getString("end")));
            }
            else{
                runOnUiThread(new UpdateUI("Result Received "+resultCode));
            }
        }
    }
}