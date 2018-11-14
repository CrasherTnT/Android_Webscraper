package com.production.crasher.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

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
                                    "https://www.securitymagazine.com/topics/2236-cyber-security-news"
                                    };
    private ArrayList<String> newsAuthor = new ArrayList<>();
    private ArrayList<String> newsTitle = new ArrayList<>();
    private ArrayList<String> newsUrl = new ArrayList<>();
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
                }, 1000);
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
                    Log.d("url",randomUrl);

                    if (randomUrl == resourceUrl[0]){
                        //2nd URL
                        Elements mElementDataSize = mBlogDocument.select("div[class=clear home-right]");
                        int mElementSize = mElementDataSize.size() - 7;
                        for (int i = 0; i < mElementSize; i++) {
                            Elements mElementAuthorName = mBlogDocument.select("div[class=item-label]").select("span").eq(i);
                            String mAuthorName = mElementAuthorName.text();

                            Elements mElementBlogTitle = mBlogDocument.select("h2[class=home-title]").eq(i);
                            String mBlogTitle = mElementBlogTitle.text();

                            Elements mElementUrl = mBlogDocument.select("a.story-link").eq(i);
                            String mUrl = mElementUrl.attr("abs:href");

                            newsAuthor.add(" ");
                            newsTitle.add(mBlogTitle);
                            newsUrl.add(mUrl);
                        }
                    }

                    else if(randomUrl == resourceUrl[1]){
                        //1st URL
                        Elements mElementDataSize = mBlogDocument.select("article[class=story]");
                        int mElementSize = mElementDataSize.size()-7;

                        for (int a = 0; a < mElementSize; a++ ){
                            Elements mNewsUrl = mBlogDocument.select("div[class=story-content]").select("a").eq(a);
                            Elements mNewsTitle = mBlogDocument.select("div[class=story-content]").select("h3").eq(a);
                            Elements mNewsDate = mBlogDocument.select("span[class=timestamp]").eq(a);

                            String mTitle = mNewsTitle.text();
                            String mUrl = mNewsUrl.attr("abs:href");
                            String mDate = mNewsDate.text();

                            newsTitle.add(mTitle);
                            newsAuthor.add(mDate);
                            newsUrl.add(mUrl);
                        }
                    }
                    else if (randomUrl == resourceUrl[2]){
                        Elements mElementDataSize = mBlogDocument.select("article[class=record article-summary]");

                        int mElementSize = mElementDataSize.size();
                        for (int a = 0; a < mElementSize; a++) {
                            Elements mNewsUrl = mBlogDocument.select("div[class=image]").select("a").eq(a);
                            Elements mNewsTitle = mBlogDocument.select("div[class=image]").select("a").eq(a);
                            Elements mNewsDate = mBlogDocument.select("div[class=post-meta]").select("div[class=date article-summary__post-date]").eq(a);

                            String mTitle = mNewsTitle.attr("title");
                            String mUrl = mNewsUrl.attr("abs:href");
                            String mDate = mNewsDate.text();

                            newsTitle.add(mTitle);
                            newsAuthor.add(mDate);
                            newsUrl.add(mUrl);
                        }
                    }
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
            DataAdapter mDataAdapter = new DataAdapter(MainActivity.this, newsTitle, newsAuthor, newsUrl);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setAdapter(mDataAdapter);

            mProgressDialog.dismiss();
        }
    }

}
