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

public class OtherNewsAdapter extends RecyclerView.Adapter<OtherNewsAdapter.NewsViewHolder> {
    List<NewsModel> otherNewsModelList = new ArrayList<>();
    Activity context;
    OtherNewsInterface otherNewsInterface;

    public OtherNewsAdapter(Activity context, OtherNewsInterface otherNewsInterface) {
        this.context = context;
        this.otherNewsInterface = otherNewsInterface;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.other_news_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        holder.title.setText(HtmlCompat.fromHtml(otherNewsModelList.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.time.setText(otherNewsModelList.get(position).getTime()+",");
        holder.date.setText(otherNewsModelList.get(position).getDate());
        Glide.with(context).load(ApiWebServices.base_url + "all_news_images/" + otherNewsModelList.get(position).getNewsImg()).into(holder.newsImg);
        holder.itemView.setOnClickListener(view -> otherNewsInterface.OnOtherNewsClicked(otherNewsModelList.get(position)));
    }

    @Override
    public int getItemCount() {
        return otherNewsModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NewsModel> newsModelList) {
        otherNewsModelList.clear();
        otherNewsModelList.addAll(newsModelList);
        notifyDataSetChanged();
    }

    public interface OtherNewsInterface {
        void OnOtherNewsClicked(NewsModel newsModel);
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
