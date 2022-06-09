package com.atoz.atoznewsadmin.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.R;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.CatModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    List<CatModel> catModels = new ArrayList<>();
    Activity context;
    CategoryListener categoryListener;

    public CategoryAdapter(Activity context, CategoryListener categoryListener) {
        this.context = context;
        this.categoryListener = categoryListener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(context).inflate(R.layout.cat_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {

        Glide.with(context).load(ApiWebServices.base_url + "all_categories_images/" + catModels.get(position).getBanner()).into(holder.catImage);
        holder.title.setText(HtmlCompat.fromHtml(catModels.get(position).getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        holder.itemView.setOnClickListener(view -> categoryListener.catOnClicked(catModels.get(position)));

    }

    @Override
    public int getItemCount() {
        return catModels.size();
    }

    public void updateList(List<CatModel> catModelList) {
        catModels.clear();
        catModels.addAll(catModelList);
        notifyDataSetChanged();
    }

    public interface CategoryListener {
        void catOnClicked(CatModel catModel);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView catImage;
        TextView title;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            catImage = itemView.findViewById(R.id.item_img);
            title = itemView.findViewById(R.id.item_title);
        }
    }
}
