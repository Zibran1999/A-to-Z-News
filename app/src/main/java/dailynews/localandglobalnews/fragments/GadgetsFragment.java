package dailynews.localandglobalnews.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dailynews.localandglobalnews.activities.NewsDetailsActivity;
import dailynews.localandglobalnews.adapters.OtherNewsAdapter;
import dailynews.localandglobalnews.databinding.FragmentGadgetsBinding;
import dailynews.localandglobalnews.models.BannerModel;
import dailynews.localandglobalnews.models.BannerModelList;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.others.OtherNewsModelFactory;
import dailynews.localandglobalnews.models.others.OtherNewsViewModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GadgetsFragment extends Fragment implements OtherNewsAdapter.OtherNewsInterface {
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    FragmentGadgetsBinding binding;
    OtherNewsViewModel otherNewsViewModel;
    OtherNewsAdapter otherNewsAdapter;
    ApiInterface apiInterface;
    List<NewsModel> otherNewsModelList = new ArrayList<>();
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();
    Map<String, String> map = new HashMap<>();
    String banUrl;
    Dialog loadingDialog;

    public GadgetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGadgetsBinding.inflate(inflater, container, false);
        apiInterface = ApiWebServices.getApiInterface();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        map.put("title", "news_banner");
        loadingDialog = CommonMethods.getLoadingDialog(requireContext());

        fetchBannerImages();

        otherNewsViewModel = new ViewModelProvider(requireActivity(),
                new OtherNewsModelFactory(requireActivity().getApplication(),
                        "gadget_news")).get(OtherNewsViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.otherNewsRecyclerView.setLayoutManager(layoutManager);
        otherNewsAdapter = new OtherNewsAdapter(requireActivity(), this);
        binding.otherNewsRecyclerView.setAdapter(otherNewsAdapter);
//        binding.otherNewsRecyclerView.setNestedScrollingEnabled(false);

        fetchAllOtherNews();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            fetchAllOtherNews();
            fetchBannerImages();
            binding.swipeRefresh.setRefreshing(false);
        });
        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);
            ads.showTopBanner(requireActivity(), binding.adViewTop);

        } else {
            ads.showTopBanner(requireActivity(), binding.adViewTop);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }
        return binding.getRoot();
    }

    public void fetchBannerImages() {
        Call<BannerModelList> call = apiInterface.fetchBanner(map);
        call.enqueue(new Callback<BannerModelList>() {
            @Override
            public void onResponse(@NonNull Call<BannerModelList> call, @NonNull Response<BannerModelList> response) {

                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (response.body().getData() != null) {

                        for (BannerModel ban : response.body().getData()) {

                            Pattern p = Pattern.compile(URL_REGEX);
                            Matcher m = p.matcher(ban.getUrl());//replace with string to compare
                            if (m.find()) {
                                Glide.with(requireActivity()).load(ApiWebServices.base_url + "strip_banner_images/"
                                        + ban.getImage()).into(binding.tipsBannerImageView);
                            } else {
                                binding.tipsBanner.setVisibility(View.GONE);
                            }

                            banUrl = ban.getUrl();
                            loadingDialog.dismiss();
                        }
                        binding.tipsBannerImageView.setOnClickListener(v -> {
                            openWebPage(banUrl);
                        });
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BannerModelList> call, @NonNull Throwable t) {

            }
        });
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void fetchAllOtherNews() {
        otherNewsViewModel.getAllOtherNews().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()) {
                otherNewsModelList.clear();
                otherNewsModelList.addAll(newsModels);
                otherNewsAdapter.updateList(otherNewsModelList);
            }
        });
    }

    @Override
    public void OnOtherNewsClicked(NewsModel newsModel) {
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Gadgets News");
        mFirebaseAnalytics.logEvent("Clicked_On_gadgets", bundle);

        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        intent.putExtra("key", "gadgets");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        IronSource.onResume(requireActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        IronSource.onPause(requireActivity());
    }
}