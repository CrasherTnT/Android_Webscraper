package com.production.crasher.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private ArrayList<String> newsTitle;
    private ArrayList<String> newsAuthor;
    private Activity mActivity;
    private ArrayList<String> newsUrl;

    public DataAdapter(MainActivity activity, ArrayList<String> newsTitle, ArrayList<String> newsAuthor, ArrayList<String> newsUrl) {
        this.mActivity = activity;
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
        this.newsAuthor = newsAuthor;
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
        holder.tv_blog_url.setText(newsUrl.get(position));
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    Toast.makeText(mActivity, "Long" + newsUrl.get(position), Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(newsUrl.get(position))); //This is used to give intent the command to open the url
                    mActivity.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView tv_blog_title, tv_blog_author, tv_blog_url;
        private ItemClickListener itemClickListener;

        public MyViewHolder(View view) {
            super(view);
            tv_blog_title = view.findViewById(R.id.row_tv_blog_title);
            tv_blog_author = view.findViewById(R.id.row_tv_blog_author);
            tv_blog_url = view.findViewById(R.id.row_tv_blog_upload_date);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(),true);
            return false;
        }
    }
}
