package dailynews.localandglobalnews.fragments;

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
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dailynews.localandglobalnews.activities.PlayQuizActivity;
import dailynews.localandglobalnews.adapters.QuizCategoryAdapter;
import dailynews.localandglobalnews.databinding.FragmentCategoryBinding;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.category.CatViewModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;

public class CategoryFragment extends Fragment implements QuizCategoryAdapter.QuizListener {
    FragmentCategoryBinding binding;
    QuizCategoryAdapter quizCategoryAdapter;
    CatViewModel catViewModel;
    ApiInterface apiInterface;
    List<CatModel> catModelList = new ArrayList<>();
    ShowAds ads = new ShowAds();
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();


    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        apiInterface = ApiWebServices.getApiInterface();
//        CommonMethods.getLoadingDialog(requireActivity()).show();

        catViewModel = new ViewModelProvider(requireActivity()).get(CatViewModel.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireActivity());

        LinearLayoutManager staggeredGridLayoutManager = new LinearLayoutManager(requireActivity());
        staggeredGridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.categoryRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        quizCategoryAdapter = new QuizCategoryAdapter(requireActivity(), this);
        binding.categoryRecyclerView.setAdapter(quizCategoryAdapter);
        fetchCategories();
        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            fetchCategories();
        });

        if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerTopNetworkName)).equals("IronSourceWithMeta")) {
            binding.adViewTop.setVisibility(View.GONE);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);

        } else if (Objects.requireNonNull(Paper.book().read(Prevalent.bannerBottomNetworkName)).equals("IronSourceWithMeta")) {
            binding.adViewBottom.setVisibility(View.GONE);
            ads.showTopBanner(requireActivity(), binding.adViewTop);

        } else {
            ads.showTopBanner(requireActivity(), binding.adViewTop);
            ads.showBottomBanner(requireActivity(), binding.adViewBottom);
        }

        return binding.getRoot();
    }

    private void fetchCategories() {
        catViewModel.getQuizCategories().observe(requireActivity(), catModels -> {
            if (!catModels.isEmpty()) {
                catModelList.clear();
                catModelList.addAll(catModels);
                for (CatModel catModel : catModelList) {

                    Log.d("contentValue", catModel.getBanner());
                    Log.d("contentValue", catModel.getTitle());
                }
                quizCategoryAdapter.updateList(catModelList);
//                CommonMethods.getLoadingDialog(requireActivity()).dismiss();
            }
        });
    }

    @Override
    public void quizOnClicked(CatModel catModel) {

        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_categories_images/" + catModel.getBanner());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, catModel.getTitle());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Category Quiz");
        mFirebaseAnalytics.logEvent("Clicked_On_category", bundle);

        Intent intent = new Intent(requireActivity(), PlayQuizActivity.class);
        intent.putExtra("id", catModel.getId());
        startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        IronSource.onResume(requireActivity());
        fetchCategories();
    }

    @Override
    public void onPause() {
        super.onPause();
        IronSource.onPause(requireActivity());
    }
}