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
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.utils.ApiWebServices;

public class NewsCategoryAdapter extends RecyclerView.Adapter<NewsCategoryAdapter.CategoryViewHolder> {

    List<CatModel> newCatModelList = new ArrayList<>();
    Activity context;
    CategoryListener categoryListener;

    public NewsCategoryAdapter(Activity context, CategoryListener categoryListener) {
        this.context = context;
        this.categoryListener = categoryListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.news_cat_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        Glide.with(context).load(ApiWebServices.base_url + "all_categories_images/" + newCatModelList.get(position).getBanner()).into(holder.newsCatImage);
        holder.newsCatTitle.setText(HtmlCompat.fromHtml(newCatModelList.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.itemView.setOnClickListener(view -> categoryListener.catOnClicked(newCatModelList.get(position)));

    }

    @Override
    public int getItemCount() {
        return newCatModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<CatModel> catModelList) {
        newCatModelList.clear();
        newCatModelList.addAll(catModelList);
        notifyDataSetChanged();
    }

    public interface CategoryListener {
        void catOnClicked(CatModel catModel);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView newsCatImage;
        TextView newsCatTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            newsCatImage = itemView.findViewById(R.id.newsCatImage);
            newsCatTitle = itemView.findViewById(R.id.newsCatTitle);
        }
    }
}
