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
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.baoyz.widget.PullRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import static com.production.crasher.myapplication.App.CHANNEL_ID;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private String[] resourceUrl = {"https://thehackernews.com/",
            "https://www.reuters.com/news/archive/cybersecurity",
            "https://www.securitymagazine.com/topics/2236-cyber-security-news",
            "https://www.csoonline.com/news/",
            "https://www.infosecurity-magazine.com/news/",
            "https://www.helpnetsecurity.com/view/news/"
    };
    private ArrayList<String> newsTitle = new ArrayList<>();
    private ArrayList<String> newsUrl = new ArrayList<>();
    private ArrayList<String> newsImage = new ArrayList<>();
    private PullRefreshLayout swipeRefreshLayout;

    private Button notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new NewsGatherer().execute();
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

        //To be able to run the worker it would require the device to be connected to network
        Constraints myConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //For Testing Purposes you can choose OneTimeWorkRequest or PeriodicWorkRequest
        //By default I implement PeriodicWorkRequest and notify the user every 15 minutes

        /* START
        final OneTimeWorkRequest hackunaWork = new OneTimeWorkRequest.Builder(HackunaWorker.class)
                        .setConstraints(myConstraints)
                        .build();
        END */


        //You need to wait 15 minutes again before you'll be able use the notify button
        PeriodicWorkRequest.Builder hackunaBuilder = new PeriodicWorkRequest.Builder(HackunaWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(myConstraints);

        final PeriodicWorkRequest hackunaWork = hackunaBuilder.build();


        //To test if the Work Manager is working
        notify = (findViewById(R.id.notify));
        notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkManager.getInstance().enqueue(hackunaWork);
            }
        });


    }

    //Still need to scrape data in onCreate method to be able to show the progress bar
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

        @Override
        protected Void doInBackground(Void... params) {
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
                hackunaNotification(newsUrl.get(0), newsTitle.get(0));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
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
    private void firstResource(Document mBlogDocument) {
        Elements mElementDataSize = mBlogDocument.select("div[class=clear home-right]");
        int mElementSize = mElementDataSize.size() - 7;
        for (int i = 0; i < mElementSize; i++) {

            Elements mElementBlogTitle = mBlogDocument.select("h2[class=home-title]").eq(i);
            Elements mElementUrl = mBlogDocument.select("a.story-link").eq(i);
            Elements mElementImage = mBlogDocument.select("img").eq(i);

            final String mBlogTitle = mElementBlogTitle.text();
            final String mUrl = mElementUrl.attr("abs:href");
            String mImage = mElementImage.attr("alt");

            newsTitle.add(mBlogTitle);
            newsUrl.add(mUrl);
            newsImage.add("https://1.bp.blogspot.com/-AaptImXE5Y4/WzjvqBS8HtI/AAAAAAAAxSs/BcCIwpWJszILkuEbDfKZhxQJwOAD7qV6ACLcBGAs/s728-e100/the-hacker-news.jpg");
        }
    }
    private void secondResource(Document mBlogDocument){
        Elements mElementDataSize = mBlogDocument.select("article[class=story]");
        int mElementSize = mElementDataSize.size()-7;
        for (int i = 0; i < mElementSize; i++ ){

            Elements mNewsUrl = mBlogDocument.select("div[class=story-content]").select("a").eq(i);
            Elements mNewsTitle = mBlogDocument.select("div[class=story-content]").select("h3").eq(i);
            Elements mElementImage = mBlogDocument.select("img").eq(i);

            String mTitle = mNewsTitle.text();
            String mUrl = mNewsUrl.attr("abs:href");
            String mImage = mElementImage.attr("org-src");

            newsTitle.add(mTitle);
            newsUrl.add(mUrl);
            newsImage.add(mImage);
        }
    }
    private void thirdResource(Document mBlogDocument){
        Elements mElementDataSize = mBlogDocument.select("article[class=record article-summary]");
        int mElementSize = mElementDataSize.size();

        for (int i = 0; i < mElementSize; i++) {
            Elements mNewsUrl = mBlogDocument.select("div[class=image]").select("a").eq(i);
            Elements mNewsTitle = mBlogDocument.select("div[class=image]").select("a").eq(i);
            Elements mElementImage = mBlogDocument.select("div[class=image]").select("img").eq(i);

            String mTitle = mNewsTitle.attr("title");
            String mUrl = mNewsUrl.attr("abs:href");
            String mImage = mElementImage.attr("src");

            newsTitle.add(mTitle);
            newsUrl.add(mUrl);
            newsImage.add(mImage);

        }
    }

    //It is optional to notify the user onCreate
    //It can be removed in NewsGatherer > doInBackground > inside the try method
    public void hackunaNotification(String mUrl, String mTitle){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl.get(0)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Hackuna News", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        Bitmap myBit = BitmapFactory.decodeResource(this.getResources(), R.drawable.thn);

        NotificationCompat.Builder nb= new NotificationCompat.Builder(this, CHANNEL_ID);
        nb.setSmallIcon(R.drawable.logo);
        nb.setContentTitle(mTitle);
        nb.setContentIntent(pendingIntent);
        nb.setAutoCancel(true);
        nb.setLargeIcon(myBit);

        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(myBit).bigLargeIcon(null);
        s.setSummaryText(mUrl);
        nb.setStyle(s);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(2, nb.build());
    }
}