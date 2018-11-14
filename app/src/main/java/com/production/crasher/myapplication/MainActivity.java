package com.production.crasher.myapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private String url = "https://thehackernews.com/";
    private ArrayList<String> newsAuthor = new ArrayList<>();
    private ArrayList<String> newsTitle = new ArrayList<>();
    private ArrayList<String> newsUrl = new ArrayList<>();
    private List<String> listData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Description().execute();
    }

    private class Description extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Android Basic JSoup Tutorial");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();


        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the web site
                Document mBlogDocument = Jsoup.connect(url).get();
                // Using Elements to get the Meta data
                Elements mElementDataSize = mBlogDocument.select("div[class=clear home-right]");

                // Locate the content attribute
                int mElementSize = mElementDataSize.size();

                for (int i = 0; i < mElementSize; i++) {
                    Elements mElementAuthorName = mBlogDocument.select("div[class=item-label]").select("span").eq(i);
                    String mAuthorName = mElementAuthorName.text();

                    Elements mElementBlogTitle = mBlogDocument.select("h2[class=home-title]").eq(i);
                    String mBlogTitle = mElementBlogTitle.text();

                    Elements mElementUrl = mBlogDocument.select("a.story-link").eq(i);
                    String mUrl = mElementUrl.attr("abs:href");

                    newsAuthor.add(mAuthorName);
                    newsTitle.add(mBlogTitle);
                    newsUrl.add(mUrl);
                }
            } catch (IOException e) {
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
