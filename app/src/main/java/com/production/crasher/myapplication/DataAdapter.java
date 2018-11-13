package com.production.crasher.myapplication;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private ArrayList<String> newsTitle = new ArrayList<>();
    private ArrayList<String> newsAuthor = new ArrayList<>();
    private ArrayList<String> newsDate = new ArrayList<>();
    private Activity mActivity;
    private int lastPosition = -1;
    private CardView newsListsCard;


    public DataAdapter(MainActivity activity, ArrayList<String> newsTitle, ArrayList<String> newsAuthor, ArrayList<String> newsDate) {
        this.mActivity = activity;
        this.newsTitle = newsTitle;
        this.newsAuthor = newsAuthor;
        this.newsDate = newsDate;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_blog_title, tv_blog_author, tv_blog_upload_date;

        public MyViewHolder(View view) {
            super(view);
            tv_blog_title = (TextView) view.findViewById(R.id.row_tv_blog_title);
            tv_blog_author = (TextView) view.findViewById(R.id.row_tv_blog_author);
            tv_blog_upload_date = (TextView) view.findViewById(R.id.row_tv_blog_upload_date);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_data, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.tv_blog_title.setText(newsTitle.get(position));
        holder.tv_blog_author.setText(newsAuthor.get(position));
        holder.tv_blog_upload_date.setText(newsDate.get(position));
    }

    @Override
    public int getItemCount() {
        return newsAuthor.size();
    }
}
