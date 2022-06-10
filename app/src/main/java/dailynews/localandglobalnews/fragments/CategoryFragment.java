package dailynews.localandglobalnews.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.activities.ShowAllItemsActivity;
import dailynews.localandglobalnews.activities.SubCategoryActivity;
import dailynews.localandglobalnews.adapters.NewsCategoryAdapter;
import dailynews.localandglobalnews.databinding.FragmentCategoryBinding;
import dailynews.localandglobalnews.models.category.CatModel;
import dailynews.localandglobalnews.models.category.CatViewModel;
import dailynews.localandglobalnews.models.category.CatViewModelFactory;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;

public class CategoryFragment extends Fragment implements NewsCategoryAdapter.CategoryListener {
    FragmentCategoryBinding binding;
    NewsCategoryAdapter newsCategoryAdapter;
    CatViewModel catViewModel;
    ApiInterface apiInterface;
    List<CatModel> catModelList = new ArrayList<>();

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        apiInterface = ApiWebServices.getApiInterface();
//        CommonMethods.getLoadingDialog(requireActivity()).show();

        catViewModel = new ViewModelProvider(requireActivity(),
                new CatViewModelFactory(requireActivity().getApplication(), "0")).get(CatViewModel.class);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        binding.categoryRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        newsCategoryAdapter = new NewsCategoryAdapter(requireActivity(), this);
        binding.categoryRecyclerView.setAdapter(newsCategoryAdapter);
        fetchCategories();


        return binding.getRoot();
    }

    private void fetchCategories() {
        catViewModel.getCategories().observe(requireActivity(), catModels -> {
            if (!catModels.isEmpty()) {
                catModelList.clear();
                catModelList.addAll(catModels);
                newsCategoryAdapter.updateList(catModelList);
//                CommonMethods.getLoadingDialog(requireActivity()).dismiss();
            }
        });
    }

    @Override
    public void catOnClicked(CatModel catModel) {
        if (catModel.getSubCat().equals("true")) {

            Intent intent = new Intent(requireActivity(), SubCategoryActivity.class);
            intent.putExtra("id", catModel.getId());
            intent.putExtra("title", catModel.getTitle());
            startActivity(intent);
        } else if (catModel.getNews().equals("true")) {
            Intent intent = new Intent(requireActivity(), ShowAllItemsActivity.class);
            intent.putExtra("key", "news");
            intent.putExtra("id", catModel.getId());
            startActivity(intent);
        } else {
            Toast.makeText(requireActivity(), "No Data Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchCategories();
    }
}