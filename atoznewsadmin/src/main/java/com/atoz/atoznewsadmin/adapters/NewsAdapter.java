package com.atoz.atoznewsadmin.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.R;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.NewsModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    List<NewsModel> newsModels = new ArrayList<>();
    Activity context;
    NewsInterface newsInterface;

    public NewsAdapter(Activity context, NewsInterface newsInterface) {
        this.context = context;
        this.newsInterface = newsInterface;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.news_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        holder.title.setText(newsModels.get(position).getTitle());
        holder.date.setText(newsModels.get(position).getDate());
        holder.time.setText(newsModels.get(position).getTime());
        Glide.with(context).load(ApiWebServices.base_url + "all_news_images/" + newsModels.get(position).getNewsImg()).into(holder.newsImg);
        holder.itemView.setOnClickListener(view -> newsInterface.newsOnClicked(newsModels.get(position)));
    }

    @Override
    public int getItemCount() {
        return newsModels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NewsModel> newsModelList) {
        newsModels.clear();
        newsModelList.addAll(newsModels);
        notifyDataSetChanged();
    }

    public interface NewsInterface {
        void newsOnClicked(NewsModel newsModel);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImg;
        TextView title, date, time;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.item_img);
            title = itemView.findViewById(R.id.item_title);
            date = itemView.findViewById(R.id.item_date);
            time = itemView.findViewById(R.id.item_time);

        }
    }
}
