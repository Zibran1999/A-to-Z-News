package dailynews.localandglobalnews.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.adapters.NewsCategoryAdapter;
import dailynews.localandglobalnews.databinding.ActivitySubCategoryBinding;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.category.CatViewModel;
import dailynews.localandglobalnews.models.category.CatViewModelFactory;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.ShowAds;

public class SubCategoryActivity extends AppCompatActivity implements NewsCategoryAdapter.CategoryListener {
    ActivitySubCategoryBinding binding;
    Dialog loading;
    NewsCategoryAdapter newsCategoryAdapter;
    List<CatModel> catModelList = new ArrayList<>();
    ApiInterface apiInterface;
    CatViewModel catViewModel;
    String id, title;
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();

    SharedPreferences preferences;

    ShowAds showAds = new ShowAds();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading = CommonMethods.getLoadingDialog(this);
        loading.show();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        apiInterface = ApiWebServices.getApiInterface();
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        binding.lottieMail.setOnClickListener(v -> {
            CommonMethods.contactUs(this);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Contact With Gmail");
            mFirebaseAnalytics.logEvent("Clicked_On_sub_category_gmail_icon", bundle);

        });
        catViewModel = new ViewModelProvider(SubCategoryActivity.this,
                new CatViewModelFactory(this.getApplication(), id)).get(CatViewModel.class);
        binding.activityTitle.setText(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY));
        binding.backIcon.setOnClickListener(v -> onBackPressed());

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.subCatRecyclerView.setLayoutManager(layoutManager);
        newsCategoryAdapter = new NewsCategoryAdapter(SubCategoryActivity.this, this);
        binding.subCatRecyclerView.setAdapter(newsCategoryAdapter);

        setSubCategories();
        getLifecycle().addObserver(showAds);
        showAds.showInterstitialAds(this);

    }

    private void setSubCategories() {
        catViewModel.getCategories().observe(SubCategoryActivity.this, catModels -> {
            if (!catModels.isEmpty()) {
                catModelList.clear();
                catModelList.addAll(catModels);
                newsCategoryAdapter.updateList(catModelList);
                loading.dismiss();
            }
        });
    }

    @Override
    public void catOnClicked(CatModel catModel) {

        if (catModel.getSubCat().equals("true")) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_categories_images/" + catModel.getBanner());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, catModel.getTitle());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Sub Category");
            mFirebaseAnalytics.logEvent("Clicked_On_sub_category", bundle);

            Intent intent = new Intent(SubCategoryActivity.this, SubCategoryActivity.class);
            intent.putExtra("id", catModel.getId());
            intent.putExtra("title", catModel.getTitle());
            startActivity(intent);
        } else if (catModel.getNews().equals("true")) {
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_categories_images/" + catModel.getBanner());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, catModel.getTitle());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Sub Category");
            mFirebaseAnalytics.logEvent("Clicked_On_sub_category", bundle);
            Intent intent = new Intent(SubCategoryActivity.this, ShowAllItemsActivity.class);
            Log.d("checkKey", catModel.getId());
            intent.putExtra("key", "news");
            intent.putExtra("id", catModel.getId());
            startActivity(intent);
        } else {
            Toast.makeText(SubCategoryActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
        }
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
}