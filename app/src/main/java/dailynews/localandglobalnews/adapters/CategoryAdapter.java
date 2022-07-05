package dailynews.localandglobalnews.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.utils.ApiWebServices;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    List<CatModel> newCatModelList = new ArrayList<>();
    Activity context;
    CategoryInterface categoryInterface;
    SharedPreferences preferences;

    public CategoryAdapter(Activity context, CategoryInterface categoryInterface) {
        this.context = context;
        this.categoryInterface = categoryInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.carousel_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(ApiWebServices.base_url + "all_categories_images/" + newCatModelList.get(position).getBanner()).into(holder.catNewsImageView);
        holder.catNewsTitle.setText(newCatModelList.get(position).getTitle());
        holder.itemView.setOnClickListener(view -> categoryInterface.OnCatClicked(newCatModelList.get(position)));
        if (preferences.getString("action", "").equals("cat")) {
            if (!preferences.getString("pos", "").equals("")) {
                categoryInterface.OnCatClicked(newCatModelList.get(Integer.parseInt(preferences.getString("pos", "0"))));
//                preferences.edit().clear().apply();
            }
        }
    }

    @Override
    public int getItemCount() {
        return newCatModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateCatList(List<CatModel> catModelList) {
        newCatModelList.clear();
        newCatModelList.addAll(catModelList);
        notifyDataSetChanged();
    }

    public interface CategoryInterface {
        void OnCatClicked(CatModel catModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView catNewsImageView;
        TextView catNewsTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            catNewsImageView = itemView.findViewById(R.id.newsImage);
            catNewsTitle = itemView.findViewById(R.id.newsTitle);
            preferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());

        }
    }
}
