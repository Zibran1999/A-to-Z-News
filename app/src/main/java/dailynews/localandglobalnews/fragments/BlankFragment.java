package dailynews.localandglobalnews.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.adapters.OtherNewsAdapter;
import dailynews.localandglobalnews.databinding.FragmentBlankBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemModelFactory;
import dailynews.localandglobalnews.models.catNewsItems.CatNewsItemViewModel;

public class BlankFragment extends Fragment implements OtherNewsAdapter.OtherNewsInterface {

    FragmentBlankBinding binding;
    LinearLayoutManager layoutManager;
    String cid;
    CatNewsItemViewModel catNewsItemViewModel;
    List<NewsModel> catItemNewsModelList = new ArrayList<>();
    OtherNewsAdapter otherNewsAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBlankBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            cid = getArguments().getString("cid");
        }
        catNewsItemViewModel = new ViewModelProvider(this,
                new CatNewsItemModelFactory(requireActivity().getApplication(), cid)).get(CatNewsItemViewModel.class);
        layoutManager = new LinearLayoutManager(requireActivity());
        binding.trendingRV.setLayoutManager(layoutManager);
        otherNewsAdapter = new OtherNewsAdapter(requireActivity(), this);
        binding.trendingRV.setAdapter(otherNewsAdapter);

//        binding.trendingRV.setNestedScrollingEnabled(false);

        LoadData();

        return binding.getRoot();
    }

    private void LoadData() {
        catNewsItemViewModel.getCatNewsItems().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()) {
                catItemNewsModelList.clear();
                catItemNewsModelList.addAll(newsModels);
                otherNewsAdapter.updateList(catItemNewsModelList);
                for (NewsModel n : catItemNewsModelList) {

                    Log.d("contentValueItem", n.getEngTitle());
                }
            }
        });


    }

    @Override
    public void OnOtherNewsClicked(NewsModel newsModel) {

    }
}