package com.production.crasher.myapplication;

import android.app.ActivityManager;
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
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.production.crasher.myapplication.App.CHANNEL_ID;


public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private PullRefreshLayout swipeRefreshLayout;

    Intent mServiceIntent;
    private SensorService mSensorService;
    Context ctx;
    public Context getCtx() {
        return ctx;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;
        setContentView(R.layout.activity_main);
        mSensorService = new SensorService(getCtx());
        mServiceIntent = new Intent(getCtx(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
        }

        NewsReceiver receiver = new NewsReceiver(new NewsData());
        Intent intent = new Intent(this, NewsService.class);
        intent.putExtra("receiver", receiver);
        startService(intent);


        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 2000);
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        stopService(mServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    public class NewsData {
        public void displayMessage(int resultCode, Bundle resultData) {
            ArrayList<String> newsTitle = resultData.getStringArrayList("newsTitle");
            ArrayList<String> newsUrl = resultData.getStringArrayList("newsUrl");
            ArrayList<String> newsImage = resultData.getStringArrayList("newsImage");

            RecyclerView mRecyclerView = findViewById(R.id.act_recyclerview);
            DataAdapter mDataAdapter = new DataAdapter(MainActivity.this, newsTitle, newsUrl, newsImage);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);
        }
    }
}
/*

}








    private class NewsGatherer extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Hackuna News");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }
        protected void doInBackground() {
            try {
                for (int urlCount=0; urlCount < resourceUrl.length; urlCount++){
                    String randomUrl = resourceUrl[urlCount];
                    Document mBlogDocument = Jsoup.connect(randomUrl).get();
                    switch (randomUrl){
                        case "https://thehackernews.com/":
                            firstResource(mBlogDocument);
                            break;
                        case "https://www.reuters.com/news/archive/cybersecurity":
                            secondResource(mBlogDocument);
                            break;
                        case "https://www.securitymagazine.com/topics/2236-cyber-security-news":
                            thirdResource(mBlogDocument);
                            break;
                        default:
                            break;

                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(Void result) {
            // Set description into TextView

            RecyclerView mRecyclerView = findViewById(R.id.act_recyclerview);
            DataAdapter mDataAdapter = new DataAdapter(MainActivity.this, newsTitle, newsUrl, newsImage);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();
        }
    }
}*/
