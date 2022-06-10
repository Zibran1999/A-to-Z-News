package dailynews.localandglobalnews.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.activities.NewsDetailsActivity;
import dailynews.localandglobalnews.adapters.OtherNewsAdapter;
import dailynews.localandglobalnews.databinding.FragmentGadgetsBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.models.others.OtherNewsModelFactory;
import dailynews.localandglobalnews.models.others.OtherNewsViewModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;

public class GadgetsFragment extends Fragment implements OtherNewsAdapter.OtherNewsInterface {
    FragmentGadgetsBinding binding;
    OtherNewsViewModel otherNewsViewModel;
    OtherNewsAdapter otherNewsAdapter;
    ApiInterface apiInterface;
    List<NewsModel> otherNewsModelList = new ArrayList<>();

    public GadgetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGadgetsBinding.inflate(inflater, container, false);
        apiInterface = ApiWebServices.getApiInterface();

        otherNewsViewModel = new ViewModelProvider(requireActivity(),
                new OtherNewsModelFactory(requireActivity().getApplication(),
                        "gadget_news")).get(OtherNewsViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.otherNewsRecyclerView.setLayoutManager(layoutManager);
        otherNewsAdapter = new OtherNewsAdapter(requireActivity(),this);
        binding.otherNewsRecyclerView.setAdapter(otherNewsAdapter);
        fetchAllOtherNews();


        return binding.getRoot();
    }

    private void fetchAllOtherNews() {
        otherNewsViewModel.getAllOtherNews().observe(requireActivity(), newsModels -> {
            if (!newsModels.isEmpty()){
                otherNewsModelList.clear();
                otherNewsModelList.addAll(newsModels);
                otherNewsAdapter.updateList(otherNewsModelList);
            }
        });
    }

    @Override
    public void OnOtherNewsClicked(NewsModel newsModel) {
        Intent intent = new Intent(requireActivity(), NewsDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("news",newsModel);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}