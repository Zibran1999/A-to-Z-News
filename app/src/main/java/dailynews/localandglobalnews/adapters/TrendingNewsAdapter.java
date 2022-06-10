package dailynews.localandglobalnews.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.ApiWebServices;

public class TrendingNewsAdapter extends RecyclerView.Adapter<TrendingNewsAdapter.NewsViewHolder> {
    List<NewsModel> trendingNewsModelList = new ArrayList<>();
    Activity context;
    TrendingNewsInterface trendingNewsInterface;

    public TrendingNewsAdapter(Activity context, TrendingNewsInterface trendingNewsInterface) {
        this.context = context;
        this.trendingNewsInterface = trendingNewsInterface;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.trending_news_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.title.setText(HtmlCompat.fromHtml(trendingNewsModelList.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.time.setText(trendingNewsModelList.get(position).getTime()+",");
        holder.date.setText(trendingNewsModelList.get(position).getDate());
        Glide.with(context).load(ApiWebServices.base_url + "all_news_images/" + trendingNewsModelList.get(position).getNewsImg()).into(holder.newsImg);
        holder.itemView.setOnClickListener(view -> trendingNewsInterface.OnTrendingNewsClicked(trendingNewsModelList.get(position)));
    }

    @Override
    public int getItemCount() {
        return trendingNewsModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NewsModel> newsModelList) {
        trendingNewsModelList.clear();
        trendingNewsModelList.addAll(newsModelList);
        notifyDataSetChanged();
    }

    public interface TrendingNewsInterface {
        void OnTrendingNewsClicked(NewsModel newsModel);
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImg;
        TextView title, date, time;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.other_news_imageView);
            title = itemView.findViewById(R.id.other_news_title);
            date = itemView.findViewById(R.id.other_news_date_txt);
            time = itemView.findViewById(R.id.other_news_time_txt);

        }
    }
}