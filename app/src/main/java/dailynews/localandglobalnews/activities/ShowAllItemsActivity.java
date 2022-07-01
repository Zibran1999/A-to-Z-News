package dailynews.localandglobalnews.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.adapters.NewsAdapter;
import dailynews.localandglobalnews.adapters.OtherNewsAdapter;
import dailynews.localandglobalnews.databinding.ActivityShowAllItemsBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.BreakingNews.NewsModelFactory;
import dailynews.localandglobalnews.models.BreakingNews.NewsViewModel;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemModelFactory;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemViewModel;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.ShowAds;

public class ShowAllItemsActivity extends AppCompatActivity implements OtherNewsAdapter.OtherNewsInterface, NewsAdapter.NewsInterface {
    ActivityShowAllItemsBinding binding;
    List<NewsModel> catItemNewsModelList = new ArrayList<>();
    List<NewsModel> breakingNewsModelList = new ArrayList<>();

    OtherNewsAdapter otherNewsAdapter;
    NewsAdapter newsAdapter;
    NewsViewModel newsViewModel;
    CatNewsItemViewModel catNewsItemViewModel;
    RecyclerView showAllItemRecyclerView;
    String key, id;
    Dialog loading;
    ShowAds showAds = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowAllItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showAllItemRecyclerView = binding.showAllItemsRecyclerView;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        binding.backIcon.setOnClickListener(v -> onBackPressed());
        loading = CommonMethods.getLoadingDialog(ShowAllItemsActivity.this);
        loading.show();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        id = getIntent().getStringExtra("id");
        key = getIntent().getStringExtra("key");
        binding.lottieMail.setOnClickListener(v -> {
            CommonMethods.contactUs(this);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Contact With Gmail");
            mFirebaseAnalytics.logEvent("Clicked_On_content_category_gmail_icon", bundle);

        });
        getLifecycle().addObserver(showAds);
        showAds.showInterstitialAds(this);
        switch (key) {
            case "news":
                binding.activityTitle.setText(HtmlCompat.fromHtml(key, HtmlCompat.FROM_HTML_MODE_LEGACY));
                catNewsItemViewModel = new ViewModelProvider(this,
                        new CatNewsItemModelFactory(this.getApplication(), id)).get(CatNewsItemViewModel.class);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                showAllItemRecyclerView.setLayoutManager(layoutManager);
                otherNewsAdapter = new OtherNewsAdapter(this, this);
                showAllItemRecyclerView.setAdapter(otherNewsAdapter);
                fetCatNewItems();
                break;

            case "breakingNews":

                newsViewModel = new ViewModelProvider(ShowAllItemsActivity.this, new NewsModelFactory(
                        this.getApplication(), "breaking_news")).get(NewsViewModel.class);
                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                showAllItemRecyclerView.setLayoutManager(staggeredGridLayoutManager);
                newsAdapter = new NewsAdapter(this, this, true);
                showAllItemRecyclerView.setAdapter(newsAdapter);
                fetchBreakingNews();

                break;

        }
    }

    private void fetCatNewItems() {
        catNewsItemViewModel.getCatNewsItems().observe(this, newsModels -> {
            if (!newsModels.isEmpty()) {
                catItemNewsModelList.clear();
                catItemNewsModelList.addAll(newsModels);
                otherNewsAdapter.updateList(catItemNewsModelList);
                loading.dismiss();
            }
        });
    }

    private void fetchBreakingNews() {
        newsViewModel.getAllNews().observe(ShowAllItemsActivity.this, newsModels -> {
            if (!newsModels.isEmpty()) {
                breakingNewsModelList.clear();
                breakingNewsModelList.addAll(newsModels);
                newsAdapter.updateList(breakingNewsModelList);
                loading.dismiss();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        showAds.showTopBanner(this, binding.adViewTop);
        showAds.showBottomBanner(this, binding.adViewBottom);
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        showAds.destroyBanner();
        IronSource.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (preferences.getString("action", "").equals("")) {
            super.onBackPressed();
            showAds.destroyBanner();
        } else {
            showAds.destroyBanner();
            preferences.edit().clear().apply();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);

        }
    }

    @Override
    public void OnOtherNewsClicked(NewsModel newsModel) {
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Category News");
        mFirebaseAnalytics.logEvent("Clicked_On_Category_news", bundle);

        Intent intent = new Intent(ShowAllItemsActivity.this, NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void newsOnClicked(NewsModel newsModel) {
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Breaking News");
        mFirebaseAnalytics.logEvent("Clicked_On_breaking_news", bundle);

        Intent intent = new Intent(ShowAllItemsActivity.this, NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}