package dailynews.localandglobalnews.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

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

public class SubCategoryActivity extends AppCompatActivity implements NewsCategoryAdapter.CategoryListener {
    ActivitySubCategoryBinding binding;
    NewsCategoryAdapter newsCategoryAdapter;
    Dialog loading;
    List<CatModel> catModelList = new ArrayList<>();
    ApiInterface apiInterface;
    CatViewModel catViewModel;
    String id, title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loading = CommonMethods.getLoadingDialog(this);
        loading.show();
        apiInterface = ApiWebServices.getApiInterface();
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");

        catViewModel = new ViewModelProvider(SubCategoryActivity.this,
                new CatViewModelFactory(this.getApplication(), id)).get(CatViewModel.class);
        binding.activityTitle.setText(HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_LEGACY));
        binding.backIcon.setOnClickListener(v -> onBackPressed());

        StaggeredGridLayoutManager layoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.subCatRecyclerView.setLayoutManager(layoutManager);
        newsCategoryAdapter = new NewsCategoryAdapter(SubCategoryActivity.this,this);
        binding.subCatRecyclerView.setAdapter(newsCategoryAdapter);

        setSubCategories();

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
            Intent intent = new Intent(SubCategoryActivity.this, SubCategoryActivity.class);
            intent.putExtra("id", catModel.getId());
            intent.putExtra("title", catModel.getTitle());
            startActivity(intent);
        } else if (catModel.getNews().equals("true")) {
            Intent intent = new Intent(SubCategoryActivity.this, ShowAllItemsActivity.class);
            Log.d("checkKey",catModel.getId());
            intent.putExtra("key", "news");
            intent.putExtra("id", catModel.getId());
            startActivity(intent);
        } else {
            Toast.makeText(SubCategoryActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
        }
    }
}