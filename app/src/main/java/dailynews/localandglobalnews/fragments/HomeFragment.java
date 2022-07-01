package dailynews.localandglobalnews.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dailynews.localandglobalnews.activities.NewsDetailsActivity;
import dailynews.localandglobalnews.adapters.NewsAdapter;
import dailynews.localandglobalnews.adapters.NewsCategoryAdapter;
import dailynews.localandglobalnews.adapters.TrendingNewsAdapter;
import dailynews.localandglobalnews.adapters.ViewPagerAdapter;
import dailynews.localandglobalnews.databinding.FragmentHomeBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.BreakingNews.NewsModelFactory;
import dailynews.localandglobalnews.models.BreakingNews.NewsViewModel;
import dailynews.localandglobalnews.models.TrendingNews.TrendingNewsViewModel;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.category.CatViewModel;
import dailynews.localandglobalnews.models.category.CatViewModelFactory;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;


public class HomeFragment extends Fragment implements NewsAdapter.NewsInterface, TrendingNewsAdapter.TrendingNewsInterface {
    FragmentHomeBinding binding;
    ApiInterface apiInterface;
    NewsAdapter newsAdapter;
    TrendingNewsAdapter trendingNewsAdapter;
    NewsViewModel newsViewModel;
    TrendingNewsViewModel trendingNewsViewModel;
    Dialog loading;
    List<NewsModel> breakingNewsModelList = new ArrayList<>();
    List<NewsModel> trendingNewsModelList = new ArrayList<>();
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();
    int visibleItems, totalItems, scrolledItems;
    Boolean isScrolling = false;
    LinearLayoutManager layoutManager1, layoutManager;

    ViewPagerAdapter viewPagerAdapter;
    NewsCategoryAdapter newsCategoryAdapter;
    CatViewModel catViewModel;
    List<CatModel> catModelList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.getLoadingDialog(requireActivity());
        loading.show();
        binding.breakingNewsContainer.setVisibility(View.GONE);
//        binding.trendingNewsContainer.setVisibility(View.GONE);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());
        catViewModel = new ViewModelProvider(requireActivity(),
                new CatViewModelFactory(requireActivity().getApplication(), "0")).get(CatViewModel.class);

        layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager1 = new LinearLayoutManager(requireActivity());

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        fetchBreakingNews();
//        fetchTrendingNews();
        fetchCategories();
        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            fetchBreakingNews();
            fetchCategories();

//            fetchTrendingNews();

        });

        if (Objects.equals(Paper.book().read(Prevalent.bannerTopNetworkName), "IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

        } else if (Objects.equals(Paper.book().read(Prevalent.bannerBottomNetworkName), "IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);
            ads.showTopBanner(requireActivity(), binding.adViewTop);

        } else {
            ads.showTopBanner(requireActivity(), binding.adViewTop);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }
        return binding.getRoot();
    }


//    private void fetchTrendingNews() {
//        binding.trendingNewsRecyclerview.setLayoutManager(layoutManager1);
//        trendingNewsAdapter = new TrendingNewsAdapter(requireActivity(), this);
//        binding.trendingNewsRecyclerview.setAdapter(trendingNewsAdapter);
//
//        trendingNewsViewModel = new ViewModelProvider(requireActivity(), new TrendingNewsModelFactory(
//                requireActivity().getApplication(), "trending_news")).get(TrendingNewsViewModel.class);
//
//        trendingNewsViewModel.getAllNews().observe(requireActivity(), newsModels -> {
//            if (!newsModels.isEmpty()) {
//                trendingNewsModelList.clear();
//                trendingNewsModelList.addAll(newsModels);
//                trendingNewsAdapter.updateList(trendingNewsModelList);
//                binding.trendingNewsContainer.setVisibility(View.VISIBLE);
//                loading.dismiss();
//            }
//        });
//        binding.breakingNewsRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
//                    isScrolling = true;
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                visibleItems = layoutManager1.getChildCount();
//                totalItems = layoutManager1.getItemCount();
//                scrolledItems = layoutManager1.findFirstVisibleItemPosition();
//
//                if (isScrolling && (visibleItems + scrolledItems == totalItems)) {
//
//                    isScrolling = false;
//                }
//            }
//        });
//    }


    private void fetchCategories() {

        binding.trendingNewsTab.setupWithViewPager(binding.trendingNewsViewPager);
        catViewModel.getCategories().observe(requireActivity(), catModels -> {
            if (!catModels.isEmpty()) {
                viewPagerAdapter = new ViewPagerAdapter(requireActivity().getSupportFragmentManager(), catModels, requireActivity());
                binding.trendingNewsViewPager.setAdapter(viewPagerAdapter);
                for (CatModel cat : catModels) {
                    Log.d("contentValue", cat.getTitle());
                }

            }
        });
    }

    private void fetchBreakingNews() {

        newsAdapter = new NewsAdapter(requireActivity(), this, false);
        newsViewModel = new ViewModelProvider(requireActivity(), new NewsModelFactory(
                requireActivity().getApplication(), "breaking_news")).get(NewsViewModel.class);
        binding.breakingNewsRV.setLayoutManager(layoutManager);
        binding.breakingNewsRV.setAdapter(newsAdapter);

        newsViewModel.getAllNews().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()) {
                breakingNewsModelList.clear();
                breakingNewsModelList.addAll(newsModels);
                newsAdapter.updateList(breakingNewsModelList);
                binding.breakingNewsContainer.setVisibility(View.VISIBLE);
                loading.dismiss();
            }
        });
    }

    @Override
    public void newsOnClicked(NewsModel newsModel) {
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Breaking News");
        mFirebaseAnalytics.logEvent("Clicked_On_breaking_news", bundle);

        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    @Override
    public void OnTrendingNewsClicked(NewsModel newsModel) {
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "trending News");
        mFirebaseAnalytics.logEvent("Clicked_On_trending_news", bundle);

        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
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



