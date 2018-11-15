package com.production.crasher.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private ArrayList<String> newsTitle;
    private Activity mActivity;
    private ArrayList<String> newsUrl;
    private ArrayList<String> newsImage;
    private ImageView news_image_view;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private TextView tv_blog_title;
        private ItemClickListener itemClickListener;
        String url = "https://images.techhive.com/images/article/2017/04/computer_forensics-100719659-medium.3x2.jpg";

        public MyViewHolder(View view) {
            super(view);
            tv_blog_title = view.findViewById(R.id.row_tv_blog_title);
            news_image_view = view.findViewById(R.id.news_image);
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
            return true;
        }
    }

    public DataAdapter(MainActivity activity, ArrayList<String> newsTitle, ArrayList<String> newsUrl, ArrayList<String> newsImage) {
        this.mActivity = activity;
        this.newsTitle = newsTitle;
        this.newsUrl = newsUrl;
        this.newsImage = newsImage;
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
        Picasso
                .get()
                .load(newsImage.get(position))
                .resize(90,90)
                .into(news_image_view);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick){
                    Toast.makeText(mActivity, newsUrl.get(position), Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(newsUrl.get(position))); //parsing the url
                    mActivity.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10; //To limit the list of news to display
    }
}
