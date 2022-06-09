package com.atoz.atoznewsadmin.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.R;
import com.atoz.atoznewsadmin.databinding.OwnAdsLayoutBinding;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.OwnAdsModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class OwnAdsAdapter extends RecyclerView.Adapter<OwnAdsAdapter.ViewHolder> {

    private final List<OwnAdsModel> ownAdsModels = new ArrayList<>();
    Context context;
    OwnAdsListener ownAdsListner;

    public OwnAdsAdapter(Context context, OwnAdsListener ownAdsListner) {
        this.context = context;
        this.ownAdsListner = ownAdsListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.own_ads_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context).load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModels.get(position).getBannerImg()).into(holder.binding.chooseBannerImage);
        Glide.with(context).load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModels.get(position).getNativeImg()).into(holder.binding.chooseNativeImage);
        Glide.with(context).load(ApiWebServices.base_url + "own_ads_images/" + ownAdsModels.get(position).getInterstitialImg()).into(holder.binding.chooseInterstitialImage);
        holder.binding.chooseBannerImage.setOnClickListener(v -> ownAdsListner.ownAdsClicked(ownAdsModels.get(position), "ban"));
        holder.binding.chooseNativeImage.setOnClickListener(v -> ownAdsListner.ownAdsClicked(ownAdsModels.get(position), "nat"));
        holder.binding.chooseInterstitialImage.setOnClickListener(v -> ownAdsListner.ownAdsClicked(ownAdsModels.get(position), "inter"));

    }

    @Override
    public int getItemCount() {
        return ownAdsModels.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateOwnAdsList(List<OwnAdsModel> ownAdsModelList) {
        ownAdsModels.clear();
        ownAdsModels.addAll(ownAdsModelList);
        notifyDataSetChanged();
    }

    public interface OwnAdsListener {
        void ownAdsClicked(OwnAdsModel ownAdsModel, String id);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        OwnAdsLayoutBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OwnAdsLayoutBinding.bind(itemView);
        }
    }
}
