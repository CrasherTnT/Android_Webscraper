package com.production.crasher.myapplication;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.production.crasher.myapplication.App.CHANNEL_ID;

public class NewsService extends Service {

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

    Timer timer = new Timer();
    MyTimerTask timerTask;
    ResultReceiver resultReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        resultReceiver = intent.getParcelableExtra("receiver");

        timerTask = new MyTimerTask();
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
        return START_STICKY;
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        Bundle bundle = new Bundle();
        bundle.putString("end", "Timer Stopped....");
        resultReceiver.send(200, bundle);
    }

    class MyTimerTask extends TimerTask
    {
        public MyTimerTask() {
            Bundle bundle = new Bundle();
            bundle.putString("start", "Timer Started....");
            resultReceiver.send(100, bundle);
        }
        @Override
        public void run() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("s");
            resultReceiver.send(Integer.parseInt(dateFormat.format(System.currentTimeMillis())), null);
        }
    }

    protected void NewsGatherer() {
        try {
            for (int urlCount = 0; urlCount < resourceUrl.length; urlCount++) {
                String randomUrl = resourceUrl[urlCount];
                Document mBlogDocument = Jsoup.connect(randomUrl).get();
                switch (randomUrl) {
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
        catch (Exception e) {
        }
    }

    private void firstResource(Document mBlogDocument) {
        Elements mElementDataSize = mBlogDocument.select("div[class=clear home-right]");
        int mElementSize = mElementDataSize.size() - 7;
        for (int i = 0; i < mElementSize; i++) {

            Elements mElementBlogTitle = mBlogDocument.select("h2[class=home-title]").eq(i);
            Elements mElementUrl = mBlogDocument.select("a.story-link").eq(i);
            Elements mElementImage = mBlogDocument.select("img").eq(i);

            String mTitle = mElementBlogTitle.text();
            String mUrl = mElementUrl.attr("abs:href");
            String mImage = mElementImage.attr("alt");

            newsTitle.add(mTitle);
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
            //Elements mElementImage = mBlogDocument.select("img").eq(i);

            String mTitle = mNewsTitle.text();
            String mUrl = mNewsUrl.attr("abs:href");
            //String mImage = mElementImage.attr("org-src");

            newsTitle.add(mTitle);
            newsUrl.add(mUrl);
            newsImage.add("https://1.bp.blogspot.com/-AaptImXE5Y4/WzjvqBS8HtI/AAAAAAAAxSs/BcCIwpWJszILkuEbDfKZhxQJwOAD7qV6ACLcBGAs/s728-e100/the-hacker-news.jpg");
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


