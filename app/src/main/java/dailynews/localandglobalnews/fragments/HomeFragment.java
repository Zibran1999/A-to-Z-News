package dailynews.localandglobalnews.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.activities.NewsDetailsActivity;
import dailynews.localandglobalnews.adapters.NewsAdapter;
import dailynews.localandglobalnews.adapters.TrendingNewsAdapter;
import dailynews.localandglobalnews.databinding.FragmentHomeBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.BreakingNews.NewsModelFactory;
import dailynews.localandglobalnews.models.BreakingNews.NewsViewModel;
import dailynews.localandglobalnews.models.TrendingNews.TrendingNewsModelFactory;
import dailynews.localandglobalnews.models.TrendingNews.TrendingNewsViewModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;


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



    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.getLoadingDialog(requireActivity());
        loading.show();
        binding.breakingNewsContainer.setVisibility(View.GONE);
        binding.trendingNewsContainer.setVisibility(View.GONE);

        newsViewModel = new ViewModelProvider(requireActivity(), new NewsModelFactory(
                requireActivity().getApplication(),"breaking_news")).get(NewsViewModel.class);

        trendingNewsViewModel = new ViewModelProvider(requireActivity(),new TrendingNewsModelFactory(
                requireActivity().getApplication(),"trending_news")).get(TrendingNewsViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(requireActivity());

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager1.setOrientation(LinearLayoutManager.VERTICAL);

        binding.breakingNewsRV.setLayoutManager(layoutManager);
        binding.trendingNewsRecyclerview.setLayoutManager(layoutManager1);

        newsAdapter = new NewsAdapter(requireActivity(),this,false);
        trendingNewsAdapter = new TrendingNewsAdapter(requireActivity(),this);

        binding.breakingNewsRV.setAdapter(newsAdapter);
        binding.trendingNewsRecyclerview.setAdapter(trendingNewsAdapter);
        fetchBreakingNews();
        fetchTrendingNews();




        return binding.getRoot();
    }

    private void fetchTrendingNews() {
        trendingNewsViewModel.getAllNews().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()){
                trendingNewsModelList.clear();
                trendingNewsModelList.addAll(newsModels);
                trendingNewsAdapter.updateList(trendingNewsModelList);
                binding.trendingNewsContainer.setVisibility(View.VISIBLE);
                loading.dismiss();
            }
        });
    }

    private void fetchBreakingNews() {
        newsViewModel.getAllNews().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()){
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
        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news",newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void OnTrendingNewsClicked(NewsModel newsModel) {
        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news",newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}