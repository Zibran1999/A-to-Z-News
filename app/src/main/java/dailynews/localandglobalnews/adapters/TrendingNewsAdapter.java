package dailynews.localandglobalnews.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.databinding.AdLayoutBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;

public class TrendingNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private static final int ITEM_FEED_COUNT = 3;
    List<NewsModel> trendingNewsModelList = new ArrayList<>();
    Activity context;
    TrendingNewsInterface trendingNewsInterface;
    ShowAds showAds = new ShowAds();
    SharedPreferences preferences;

    public TrendingNewsAdapter(Activity context, TrendingNewsInterface trendingNewsInterface) {
        this.context = context;
        this.trendingNewsInterface = trendingNewsInterface;
    }

    public int getItemViewType(int position) {
        if ((position + 1) % ITEM_FEED_COUNT == 0) {
            return AD_VIEW;
        }
        return ITEM_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW) {
            return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.trending_news_layout, parent, false));


        } else if (viewType == AD_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.ad_layout, parent, false);
//            final ViewGroup.LayoutParams lp = view.getLayoutParams();
//            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
//                StaggeredGridLayoutManager.LayoutParams glp = (StaggeredGridLayoutManager.LayoutParams) lp;
//                glp.setFullSpan(true);
//            }
            return new AdViewHolder(view);
        } else return null;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (holder.getItemViewType() == ITEM_VIEW) {
            int position = pos - Math.round(pos / ITEM_FEED_COUNT);
            ((NewsViewHolder) holder).title.setText(HtmlCompat.fromHtml(trendingNewsModelList.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            ((NewsViewHolder) holder).time.setText(String.format("%s,", trendingNewsModelList.get(position).getTime()));
            ((NewsViewHolder) holder).date.setText(trendingNewsModelList.get(position).getDate());
            Glide.with(context).load(ApiWebServices.base_url + "all_news_images/" + trendingNewsModelList.get(position).getNewsImg()).into(((NewsViewHolder) holder).newsImg);
            holder.itemView.setOnClickListener(view -> trendingNewsInterface.OnTrendingNewsClicked(trendingNewsModelList.get(position)));
            if (preferences.getString("action", "").equals("tre")) {
                if (!preferences.getString("pos", "").equals("")) {
                    trendingNewsInterface.OnTrendingNewsClicked(trendingNewsModelList.get(Integer.parseInt(preferences.getString("cat_item_pos", "0"))));
                }
            }


        } else if (holder.getItemViewType() == AD_VIEW) {
            ((AdViewHolder) holder).bindAdData();
        }
    }

    @Override
    public int getItemCount() {
        if (trendingNewsModelList.size() > 0) {
            return trendingNewsModelList.size() + Math.round(trendingNewsModelList.size() / ITEM_FEED_COUNT);

        }
        return 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NewsModel> newsModelList) {
        trendingNewsModelList.clear();
        trendingNewsModelList.addAll(newsModelList);
        Collections.reverse(trendingNewsModelList);
        notifyDataSetChanged();
    }

    public interface TrendingNewsInterface {
        void OnTrendingNewsClicked(NewsModel newsModel);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImg;
        TextView title, date, time;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.other_news_imageView);
            title = itemView.findViewById(R.id.other_news_title);
            date = itemView.findViewById(R.id.other_news_date_txt);
            time = itemView.findViewById(R.id.other_news_time_txt);
            preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

        }
    }

    public class AdViewHolder extends RecyclerView.ViewHolder {
        AdLayoutBinding binding;

        public AdViewHolder(@NonNull View itemAdView2) {
            super(itemAdView2);
            binding = AdLayoutBinding.bind(itemAdView2);


        }

        private void bindAdData() {
            Log.d("admobAdNative", Paper.book().read(Prevalent.nativeAds));
            if (Objects.equals(Paper.book().read(Prevalent.nativeAdsType), "Native")) {
                showAds.showNativeAds(context, binding.adLayout);
            } else if (Objects.equals(Paper.book().read(Prevalent.nativeAdsType), "MREC")) {
                showAds.showMrec(context, binding.adLayout);
            }

        }
    }

}
