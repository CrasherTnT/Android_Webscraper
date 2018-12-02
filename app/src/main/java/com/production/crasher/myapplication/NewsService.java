package com.production.crasher.myapplication;

import android.app.IntentService;
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
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import static com.production.crasher.myapplication.App.CHANNEL_ID;

public class NewsService extends IntentService {

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

    public NewsService(){
        super("News Service");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("News", "News service has started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
        /* Return this, if the application is terminated
        * it re-executes the service*/

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        newsGatherer();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("newsTitle", newsTitle);
        bundle.putStringArrayList("newsImage", newsImage);
        bundle.putStringArrayList("newsUrl", newsUrl);
        receiver.send(1234, bundle);
    }

    protected void newsGatherer() {
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



}
