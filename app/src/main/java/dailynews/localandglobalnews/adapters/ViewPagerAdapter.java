package dailynews.localandglobalnews.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import dailynews.localandglobalnews.fragments.BlankFragment;
import dailynews.localandglobalnews.models.category.CatModel;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;
    List<CatModel> catModelList;

    public ViewPagerAdapter(@NonNull FragmentManager fm, List<CatModel> catModelList, Context context) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.catModelList = catModelList;
        Log.d("cid", catModelList.get(1).getTitle());

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("cid", catModelList.get(position).getId());
        BlankFragment blankFragment = new BlankFragment();
        blankFragment.setArguments(bundle);
        return blankFragment;
    }

    @Override
    public int getCount() {
        return catModelList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        Log.d("cid", catModelList.get(position).getTitle());
        return catModelList.get(position).getTitle();
    }

}
