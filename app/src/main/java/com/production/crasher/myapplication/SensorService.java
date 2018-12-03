package com.production.crasher.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.production.crasher.myapplication.App.CHANNEL_ID;

public class SensorService extends Service {

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


    public int counter=0;
    public SensorService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public SensorService() {
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, SensorRestarterBroadcastReceiver.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 0, 10000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                doInBackground();
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
            Log.v("Debug", mTitle);
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
            Log.v("Debug", mTitle);

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
            Log.v("Debug", mTitle);
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
