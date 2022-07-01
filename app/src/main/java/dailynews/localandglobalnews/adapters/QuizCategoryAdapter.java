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
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;

public class QuizCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW = 0;
    private static final int AD_VIEW = 1;
    private static final int ITEM_FEED_COUNT = 7;
    ShowAds showAds = new ShowAds();
    List<CatModel> newCatModelList = new ArrayList<>();
    Activity context;
    QuizListener quizListener;
    SharedPreferences preferences;


    public QuizCategoryAdapter(Activity context, QuizListener quizListener) {
        this.context = context;
        this.quizListener = quizListener;
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
            return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.quiz_cat_layout, parent, false));

        } else if (viewType == AD_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.ad_layout, parent, false);
            return new AdViewHolder(view);
        } else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {

        if (holder.getItemViewType() == ITEM_VIEW) {
            int position = pos - Math.round(pos / ITEM_FEED_COUNT);
            Glide.with(context).load(ApiWebServices.base_url + "all_categories_images/" + newCatModelList.get(position).getBanner()).into(((CategoryViewHolder) holder).quizImg);
            ((CategoryViewHolder) holder).quizTitle.setText(HtmlCompat.fromHtml(newCatModelList.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            holder.itemView.setOnClickListener(view -> quizListener.quizOnClicked(newCatModelList.get(position)));
            if (preferences.getString("action", "").equals("quiz")) {
                if (!preferences.getString("pos", "").equals("")) {
                    quizListener.quizOnClicked(newCatModelList.get(Integer.parseInt(preferences.getString("pos", "0"))));
//                preferences.edit().clear().apply();
                }
            }


        } else if (holder.getItemViewType() == AD_VIEW) {
            ((AdViewHolder) holder).bindAdData();
        }

    }

    @Override
    public int getItemCount() {
        if (newCatModelList.size() > 0) {
            return newCatModelList.size() + Math.round(newCatModelList.size() / ITEM_FEED_COUNT);

        }
        return 0;
    }


    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<CatModel> catModelList) {
        newCatModelList.clear();
        newCatModelList.addAll(catModelList);
        Collections.reverse(newCatModelList);
        notifyDataSetChanged();
    }

    public interface QuizListener {
        void quizOnClicked(CatModel catModel);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView quizImg;
        TextView quizTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            quizImg = itemView.findViewById(R.id.quiz_img);
            quizTitle = itemView.findViewById(R.id.quiz_title);
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
