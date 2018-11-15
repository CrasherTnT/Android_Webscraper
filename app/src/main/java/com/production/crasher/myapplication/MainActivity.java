package com.production.crasher.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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

        @Override
        protected Void doInBackground(Void... params) {
            try {

                for (int urlCount=0; urlCount < resourceUrl.length; urlCount++){
                    String randomUrl = resourceUrl[urlCount];
                    Document mBlogDocument = Jsoup.connect(randomUrl).get();

                    if (randomUrl == resourceUrl[0]) {
                        firstResource(mBlogDocument); //1st URL
                        //This resource (The Hacker News) encoded their images using base64...
                        // I'm still finding a solution to get the raw url but for now I put a temporary image
                    }
                    else if (randomUrl == resourceUrl[1]) {
                        secondResource(mBlogDocument); //2nd URL
                    }
                    else if (randomUrl == resourceUrl[2]) {
                        thirdResource(mBlogDocument); //3rd URL
                    }
                    /*
                    else if (randomUrl == resourceUrl[3]){
                        fourthResource(mBlogDocument);
                    }
                    else if (randomUrl == resourceUrl[4]){
                        fifthResource(mBlogDocument);
                    }
                    else if (randomUrl == resourceUrl[5]){
                        sixthResource(mBlogDocument);
                    }
                    else if (randomUrl == resourceUrl[6]){
                        seventhResource(mBlogDocument);
                    }
                    else if (randomUrl == resourceUrl[7]){
                        eightResource(mBlogDocument);
                    }
                    else if (randomUrl == resourceUrl[8]){
                        ninethResource(mBlogDocument);
                    }
                    else if (randomUrl == resourceUrl[9]){
                        tenthResource(mBlogDocument);
                    }
                    */
                }
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

            String mBlogTitle = mElementBlogTitle.text();
            String mUrl = mElementUrl.attr("abs:href");
            String mImage = mElementImage.attr("alt");

            newsTitle.add(mBlogTitle);
            newsUrl.add(mUrl);
            newsImage.add("https://jeffreymannfinejewelers.com/static/images/temp-inventory-landing.jpg");
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
    private void fourthResource(Document mBlogDocument){
        Elements mElementDataSize = mBlogDocument.select("div[class=article]");
        int mElementSize = mElementDataSize.size();

        for (int i = 0; i < mElementSize; i++) {
            Elements mNewsUrl = mBlogDocument.select("div[class=promo-headline]").select("h3").select("a").eq(i);
            Elements mNewsTitle = mBlogDocument.select("div[class=promo-headline]").select("h3").select("a").eq(i);
            Elements mElementImage = mBlogDocument.select("div[class=promo-image]").select("img").eq(i);

            String mTitle = mNewsTitle.text();
            String mUrl = mNewsUrl.attr("abs:href");
            String mImage = mElementImage.attr("src");

            newsTitle.add(mTitle);
            newsUrl.add(mUrl);
            newsImage.add(mImage);

        }
    }
    private void fifthResource(Document mBlogDocument){
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
    private void sixthResource(Document mBlogDocument){
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
    private void seventhResource(Document mBlogDocument){
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
    private void eightResource(Document mBlogDocument){
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
    private void ninethResource(Document mBlogDocument){
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
    private void tenthResource(Document mBlogDocument){
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
    */
}
