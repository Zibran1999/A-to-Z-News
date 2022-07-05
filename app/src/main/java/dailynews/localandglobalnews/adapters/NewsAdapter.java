package dailynews.localandglobalnews.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.activities.ShowAllItemsActivity;
import dailynews.localandglobalnews.databinding.AdLayoutBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.ShowAds;

public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private static final int ITEM_FEED_COUNT = 7;

    private static final int BUTTON_VIEW_ALL = 1;
    private static final int BUTTON_COUNT = 8;

    private final boolean shouldShowAllItems;
    List<NewsModel> newsModels = new ArrayList<>();
    Activity context;
    NewsInterface newsInterface;
    ShowAds showAds = new ShowAds();
    SharedPreferences preferences;

    public NewsAdapter(Activity context, NewsInterface newsInterface, boolean shouldShowAllItems) {
        this.context = context;
        this.newsInterface = newsInterface;
        this.shouldShowAllItems = shouldShowAllItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (shouldShowAllItems) {
            if ((position + 1) % ITEM_FEED_COUNT == 0) {
                return AD_VIEW;
            }
        } else {
            if ((position + 1) % BUTTON_COUNT == 0) {
                return BUTTON_VIEW_ALL;
            }
        }
        return ITEM_VIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (shouldShowAllItems) {
            if (viewType == ITEM_VIEW) {
                View view = LayoutInflater.from(context).inflate(R.layout.all_news_layout, parent, false);
                return new NewsViewHolder(view);
            } else if (viewType == AD_VIEW) {
                View view = LayoutInflater.from(context).inflate(R.layout.ad_layout, parent, false);
                final ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                    StaggeredGridLayoutManager.LayoutParams glp = (StaggeredGridLayoutManager.LayoutParams) lp;
                    glp.setFullSpan(true);
                }
                return new AdViewHolder(view);
            }
        } else {
            if (viewType == ITEM_VIEW) {
                View view = LayoutInflater.from(context).inflate(R.layout.news_layout, parent, false);
                return new NewsViewHolder(view);
            } else if (viewType == BUTTON_VIEW_ALL) {
                View view = LayoutInflater.from(context).inflate(R.layout.view_all_news_layout, parent, false);
                return new ButtonViewHolder(view);
            }

        }
        return null;
        //return new NewsViewHolder(LayoutInflater.from(context).inflate(R.layout.news_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (shouldShowAllItems) {
            if (holder.getItemViewType() == ITEM_VIEW) {
                int position = pos - Math.round(pos / ITEM_FEED_COUNT);
                ((NewsViewHolder) holder).title.setText(HtmlCompat.fromHtml(newsModels.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                ((NewsViewHolder) holder).date.setText(newsModels.get(position).getDate());
                ((NewsViewHolder) holder).time.setText(newsModels.get(position).getTime());
                Glide.with(context).load(ApiWebServices.base_url + "all_news_images/" + newsModels.get(position).getNewsImg()).into(((NewsViewHolder) holder).newsImg);
                holder.itemView.setOnClickListener(view -> newsInterface.newsOnClicked(newsModels.get(position)));
                if (preferences.getString("action", "").equals("cat")) {
                    if (!preferences.getString("cat_item_pos", "").equals("")) {
                        newsInterface.newsOnClicked(newsModels.get(Integer.parseInt(preferences.getString("cat_item_pos", "0"))));
                    }
                }
                if (preferences.getString("action", "").equals("bre")) {
                    if (!preferences.getString("pos", "").equals("")) {
                        newsInterface.newsOnClicked(newsModels.get(Integer.parseInt(preferences.getString("cat_item_pos", "0"))));
                    }
                }
            } else if (holder.getItemViewType() == AD_VIEW) {
                ((AdViewHolder) holder).bindAdData();
            }
        } else {
            if (holder.getItemViewType() == ITEM_VIEW) {
                int position = pos - Math.round(pos / ITEM_FEED_COUNT);
                ((NewsViewHolder) holder).title.setText(HtmlCompat.fromHtml(newsModels.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                ((NewsViewHolder) holder).date.setText(newsModels.get(position).getDate());
                ((NewsViewHolder) holder).time.setText(newsModels.get(position).getTime());
                Glide.with(context).load(ApiWebServices.base_url + "all_news_images/" + newsModels.get(position).getNewsImg()).into(((NewsViewHolder) holder).newsImg);
                holder.itemView.setOnClickListener(view -> newsInterface.newsOnClicked(newsModels.get(position)));
                if (preferences.getString("action", "").equals("cat")) {
                    if (!preferences.getString("cat_item_pos", "").equals("")) {
                        newsInterface.newsOnClicked(newsModels.get(Integer.parseInt(preferences.getString("cat_item_pos", "0"))));
                    }
                }
                if (preferences.getString("action", "").equals("bre")) {
                    if (!preferences.getString("pos", "").equals("")) {
                        newsInterface.newsOnClicked(newsModels.get(Integer.parseInt(preferences.getString("cat_item_pos", "0"))));
                    }
                }
            } else if (holder.getItemViewType() == BUTTON_VIEW_ALL) {
                ((ButtonViewHolder) holder).viewAllCardBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(context, ShowAllItemsActivity.class);
                    intent.putExtra("key", "breakingNews");
                    context.startActivity(intent);
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        if (shouldShowAllItems) {
            return Math.min(newsModels.size(), 25);

        } else {
            return Math.min(newsModels.size(), BUTTON_COUNT);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<NewsModel> newsModelList) {
        newsModels.clear();
        newsModels.addAll(newsModelList);
        Collections.reverse(newsModels);
        notifyDataSetChanged();
    }

    public interface NewsInterface {
        void newsOnClicked(NewsModel newsModel);
    }

    public static class ButtonViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView viewAllCardBtn;

        public ButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            viewAllCardBtn = itemView.findViewById(R.id.view_all_card);
        }
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView newsImg;
        TextView title, date, time;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            newsImg = itemView.findViewById(R.id.news_imageView);
            title = itemView.findViewById(R.id.news_title);
            date = itemView.findViewById(R.id.news_date_text);
            time = itemView.findViewById(R.id.newsTime_text);
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
//            if (Objects.equals(Paper.book().read(Prevalent.nativeAdsType), "Native")) {
//                showAds.showNativeAds(context, binding.adLayout);
//            } else if (Objects.equals(Paper.book().read(Prevalent.nativeAdsType), "MREC")) {
//                showAds.showMrec(context, binding.adLayout);
//            }

        }


    }
}
