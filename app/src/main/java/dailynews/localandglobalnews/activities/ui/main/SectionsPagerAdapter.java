package dailynews.localandglobalnews.activities.ui.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import dailynews.localandglobalnews.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();


    public SectionsPagerAdapter(FragmentManager fm, int behavior) {
        super(fm,behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public void addFragments(Fragment fragment,String title){
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return fragmentList.size();
    }
}