package com.production.crasher.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.production.crasher.myapplication.App.CHANNEL_ID;

public class HackunaWorker extends Worker {

    private ArrayList<String> newsTitle = new ArrayList<>();
    private ArrayList<String> newsUrl = new ArrayList<>();
    private ArrayList<String> newsImage = new ArrayList<>();

    private String[] resourceUrl = {"https://thehackernews.com/",
            "https://www.reuters.com/news/archive/cybersecurity",
            "https://www.securitymagazine.com/topics/2236-cyber-security-news",
            "https://www.csoonline.com/news/",
            "https://www.infosecurity-magazine.com/news/",
            "https://www.helpnetsecurity.com/view/news/"
    };

    public HackunaWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Worker.Result doWork() {

        //NOTE If the speed is slow then use the NewsGathererFast() method
        //It would only scan and notify one news in the firstResource
        NewsGatherer();
        hackunaNotification(newsUrl.get(0), newsTitle.get(0));
        return Result.SUCCESS;

    }

    public void NewsGatherer() {
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


    public void hackunaNotification(String mUrl, String mTitle){
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsUrl.get(0)));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        Bitmap myBit = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.thn);

        NotificationCompat.Builder nb= new NotificationCompat.Builder(getApplicationContext(), "default");
        nb.setSmallIcon(R.drawable.logo);
        nb.setContentTitle(mTitle);
        nb.setContentIntent(pendingIntent);
        nb.setAutoCancel(true);
        nb.setLargeIcon(myBit);

        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(myBit).bigLargeIcon(null);
        s.setSummaryText(mUrl);
        nb.setStyle(s);

        notificationManager.notify(2, nb.build());
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

    /*
    You can remove secondResource and thirdResource since it will only notify and use the first news in the firstResource.
    It would save make it faster to scrape and less data to consume
    ////////////////////////////////////////////////////////
    Here is the code if you want to change faster performance
    ////////////////////////////////////////////////////////

    public void NewsGathererFast() {
        try {
            Document mBlogDocument = Jsoup.connect(randomUrl).get();
            Elements mElementDataSize = mBlogDocument.select("div[class=clear home-right]");

            Elements mElementBlogTitle = mBlogDocument.select("h2[class=home-title]").eq(0);
            Elements mElementUrl = mBlogDocument.select("a.story-link").eq(0);

            final String mBlogTitle = mElementBlogTitle.text();
            final String mUrl = mElementUrl.attr("abs:href");

            newsTitle.add(mBlogTitle);
            newsUrl.add(mUrl);
            newsImage.add("https://1.bp.blogspot.com/-AaptImXE5Y4/WzjvqBS8HtI/AAAAAAAAxSs/BcCIwpWJszILkuEbDfKZhxQJwOAD7qV6ACLcBGAs/s728-e100/the-hacker-news.jpg");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

     */

}
