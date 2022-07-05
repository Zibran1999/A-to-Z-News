package dailynews.localandglobalnews.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;
import com.jackandphantom.carouselrecyclerview.CarouselRecyclerview;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dailynews.localandglobalnews.activities.NewsDetailsActivity;
import dailynews.localandglobalnews.activities.ShowAllItemsActivity;
import dailynews.localandglobalnews.adapters.CategoryAdapter;
import dailynews.localandglobalnews.adapters.NewsAdapter;
import dailynews.localandglobalnews.adapters.TrendingNewsAdapter;
import dailynews.localandglobalnews.databinding.FragmentHomeBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemModelFactory;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemViewModel;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.category.CatViewModel;
import dailynews.localandglobalnews.models.category.CatViewModelFactory;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements NewsAdapter.NewsInterface, CategoryAdapter.CategoryInterface, TrendingNewsAdapter.TrendingNewsInterface {
    FragmentHomeBinding binding;
    ApiInterface apiInterface;
    NewsAdapter newsAdapter;
    Dialog loading;
    List<NewsModel> breakingNewsModelList = new ArrayList<>();
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();
    LinearLayoutManager layoutManager1, layoutManager, layoutManager2;
    CatViewModel catViewModel;
    boolean callOnce = true;

    // Carousel Recyclerview
    CategoryAdapter categoryAdapter;
    List<CatModel> catModelList = new ArrayList<>();
    CarouselRecyclerview carouselRecyclerview;

    // Carousel Items
    CatNewsItemViewModel catNewsItemViewModel;
    List<NewsModel> catItemNewsModelList = new ArrayList<>();
    TrendingNewsAdapter otherNewsAdapter;

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

        // setting data in carousel recyclerview
        carouselRecyclerview = binding.carouselItemRecyclerView;
        categoryAdapter = new CategoryAdapter(requireActivity(), this);

        carouselRecyclerview.setAdapter(categoryAdapter);
        carouselRecyclerview.set3DItem(true);
        carouselRecyclerview.setFlat(false);
        carouselRecyclerview.setInfinite(false);
        carouselRecyclerview.setAlpha(true);


        // Setting Carousel Items
        layoutManager2 = new LinearLayoutManager(requireActivity());
        binding.CRRecyclerview.setLayoutManager(layoutManager2);
        otherNewsAdapter = new TrendingNewsAdapter(requireActivity(), "home", this);
        binding.CRRecyclerview.setAdapter(otherNewsAdapter);


        fetchCategories();
        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            fetchBreakingNews();
            fetchCategories();
        });

        carouselRecyclerview.setItemSelectListener(i -> LoadData(catModelList.get(i).getId()));
        return binding.getRoot();
    }

    private void fetchCategories() {
        catViewModel.getCategories().observe(requireActivity(), catModels -> {
            if (!catModels.isEmpty()) {
                catModelList.clear();
                catModelList.addAll(catModels);
                categoryAdapter.updateCatList(catModelList);

                if (callOnce) {
                    LoadData(catModelList.get(0).getId());
                }

            }
        });
    }

    private void LoadData(String cId) {
        callOnce = false;
        Call<List<NewsModel>> call = apiInterface.fetchCatNewsItem(cId);
        call.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    catItemNewsModelList.clear();
                    catItemNewsModelList.addAll(Objects.requireNonNull(response.body()));
                    otherNewsAdapter.updateList(catItemNewsModelList);
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {
                loading.dismiss();
            }
        });
    }

    private void fetchBreakingNews() {
        catNewsItemViewModel = new ViewModelProvider(this,
                new CatNewsItemModelFactory(requireActivity().getApplication(), "breaking_news")).get(CatNewsItemViewModel.class);

        newsAdapter = new NewsAdapter(requireActivity(), this, false);

        binding.breakingNewsRV.setLayoutManager(layoutManager);
        binding.breakingNewsRV.setAdapter(newsAdapter);

        catNewsItemViewModel.getCatNewsItems().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()) {
                binding.breakingNewsText.setVisibility(View.VISIBLE);
                breakingNewsModelList.clear();
                breakingNewsModelList.addAll(newsModels);
                newsAdapter.updateList(breakingNewsModelList);
                binding.breakingNewsContainer.setVisibility(View.VISIBLE);
            } else {
                binding.breakingNewsText.setVisibility(View.GONE);
            }
            loading.dismiss();
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
    public void onResume() {
        super.onResume();
        fetchBreakingNews();
        IronSource.onResume(requireActivity());
        if (Objects.equals(Paper.book().read(Prevalent.bannerTopNetworkName), "IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

        } else if (Objects.equals(Paper.book().read(Prevalent.bannerBottomNetworkName), "IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);
//            ads.showTopBanner(requireActivity(), binding.adViewTop);

        } else {
//            ads.showTopBanner(requireActivity(), binding.adViewTop);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        IronSource.onPause(requireActivity());
    }

    @Override
    public void OnCatClicked(CatModel catModel) {
        Intent intent = new Intent(requireActivity(), ShowAllItemsActivity.class);
        intent.putExtra("key", "news");
        intent.putExtra("id", catModel.getId());
        intent.putExtra("title", catModel.getTitle());
        startActivity(intent);
    }


    @Override
    public void OnTrendingNewsClicked(NewsModel newsModel) {
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Category News");
        mFirebaseAnalytics.logEvent("Clicked_On_Category_news", bundle);

        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}



