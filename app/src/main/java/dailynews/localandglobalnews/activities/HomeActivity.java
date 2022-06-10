package dailynews.localandglobalnews.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import static dailynews.localandglobalnews.activities.StartActivity.tabText;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.activities.ui.main.SectionsPagerAdapter;
import dailynews.localandglobalnews.databinding.ActivityHomeBinding;
import dailynews.localandglobalnews.fragments.CategoryFragment;
import dailynews.localandglobalnews.fragments.GadgetsFragment;
import dailynews.localandglobalnews.fragments.HomeFragment;
import dailynews.localandglobalnews.models.tabTextAndUrls.UrlOrTAbTextModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.MyReceiver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String BroadCastStringForAction = "checkingInternet";
    private static final float END_SCALE = 0.7f;
    int count = 1;
    ImageView navMenu;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ConstraintLayout categoryContainer;
    ActivityHomeBinding binding;
    Dialog loading;
    String siteUrl;
    ApiInterface apiInterface;
    FirebaseAnalytics mFirebaseAnalytics;
    private IntentFilter intentFilter;
    private SectionsPagerAdapter sectionsPagerAdapter;

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(BroadCastStringForAction)) {
                if (intent.getStringExtra("online_status").equals("true")) {

                    Set_Visibility_ON();
                    count++;
                } else {
                    Set_Visibility_OFF();
                }
            }
        }
    };
    private Bundle bundle;

    private void Set_Visibility_ON() {
        binding.tvNotConnected.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.VISIBLE);
        binding.tabs.setVisibility(View.VISIBLE);
        enableNavItems();

//        Toast.makeText(this, "hello "+ count, Toast.LENGTH_SHORT).show();
//        if (count == 1) {
//
//        }
    }

    private void Set_Visibility_OFF() {
        binding.tvNotConnected.setVisibility(View.VISIBLE);
        disableNavItems();
        loading.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiWebServices.getApiInterface();
        loading = CommonMethods.getLoadingDialog(HomeActivity.this);
        navigationView = binding.navigation;
        navMenu = binding.navMenu;
        drawerLayout = binding.drawerLayout;
        bundle = new Bundle();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        fetchUrl();

        // Setting Version Code
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            binding.versionCode.setText(getString(R.string.version, version));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //Internet Checking Condition
        intentFilter = new IntentFilter();
        intentFilter.addAction(BroadCastStringForAction);
        Intent serviceIntent = new Intent(this, MyReceiver.class);
        startService(serviceIntent);
        if (isOnline(HomeActivity.this)) {
            Set_Visibility_ON();
        } else {
            Set_Visibility_OFF();
        }

        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        sectionsPagerAdapter.addFragments(new HomeFragment(), "Home");
        sectionsPagerAdapter.addFragments(new CategoryFragment(), "Category");
        sectionsPagerAdapter.addFragments(new GadgetsFragment(), tabText);

        navigationDrawer();
        binding.viewPager.setAdapter(sectionsPagerAdapter);
        binding.tabs.setupWithViewPager(binding.viewPager);
    }

    private void fetchUrl() {
        Call<UrlOrTAbTextModel> call = apiInterface.fetchURLOrTABText("site_url");
        call.enqueue(new Callback<UrlOrTAbTextModel>() {
            @Override
            public void onResponse(@NonNull Call<UrlOrTAbTextModel> call, @NonNull Response<UrlOrTAbTextModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    siteUrl = response.body().getUrl();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UrlOrTAbTextModel> call, @NonNull Throwable t) {
                Log.d("contentError", t.getMessage());
            }
        });
    }


    public void navigationDrawer() {
        navigationView = findViewById(R.id.navigation);
        navigationView.bringToFront();
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                HomeActivity.this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(HomeActivity.this);
        navigationView.setCheckedItem(R.id.nav_home);
        categoryContainer = findViewById(R.id.container_layout);

        navMenu.setOnClickListener(view -> {
            if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawerLayout.setScrimColor(getColor(R.color.teal_200));
        }
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                categoryContainer.setScaleX(offsetScale);
                categoryContainer.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = categoryContainer.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                categoryContainer.setTranslationX(xTranslation);
            }
        });
    }

    public boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public void disableNavItems() {
        Menu navMenu = navigationView.getMenu();

        MenuItem nav_home = navMenu.findItem(R.id.nav_home);
        nav_home.setEnabled(false);

        MenuItem nav_visit = navMenu.findItem(R.id.nav_visit);
        nav_visit.setEnabled(false);

        MenuItem nav_share = navMenu.findItem(R.id.nav_share);
        nav_share.setEnabled(false);

        MenuItem nav_rate = navMenu.findItem(R.id.nav_rate);
        nav_rate.setEnabled(false);

        MenuItem nav_policy = navMenu.findItem(R.id.nav_privacy);
        nav_policy.setEnabled(false);

        MenuItem nav_disclaimer = navMenu.findItem(R.id.nav_disclaimer);
        nav_disclaimer.setEnabled(false);

    }

    public void enableNavItems() {
        Menu navMenu = navigationView.getMenu();

        MenuItem nav_home = navMenu.findItem(R.id.nav_home);
        nav_home.setEnabled(true);

        MenuItem nav_visit = navMenu.findItem(R.id.nav_visit);
        nav_visit.setEnabled(true);

        MenuItem nav_share = navMenu.findItem(R.id.nav_share);
        nav_share.setEnabled(true);

        MenuItem nav_rate = navMenu.findItem(R.id.nav_rate);
        nav_rate.setEnabled(true);

        MenuItem nav_policy = navMenu.findItem(R.id.nav_privacy);
        nav_policy.setEnabled(true);

        MenuItem nav_disclaimer = navMenu.findItem(R.id.nav_disclaimer);
        nav_disclaimer.setEnabled(true);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Home Menu");
                mFirebaseAnalytics.logEvent("Clicked_On_Home_Menu", bundle);
                break;
            case R.id.nav_visit:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Visit Site");
                mFirebaseAnalytics.logEvent("Clicked_Visit_Site", bundle);
                openWebPage(siteUrl,HomeActivity.this);
                break;
            case R.id.nav_share:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Share Menu");
                mFirebaseAnalytics.logEvent("Clicked_On_ShareMenu", bundle);
                CommonMethods.shareApp(HomeActivity.this, "shareText");
                break;
            case R.id.nav_rate:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Rate Menu");
                mFirebaseAnalytics.logEvent("Clicked_On_Rate_Menu", bundle);
                CommonMethods.rateApp(HomeActivity.this);
                break;
            case R.id.nav_privacy:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Privacy Menu");
                mFirebaseAnalytics.logEvent("Clicked_On_Privacy_Menu", bundle);
                Intent intent = new Intent(HomeActivity.this, PrivacyPolicy.class);
                intent.putExtra("key", "policy");
                startActivity(intent);
                break;
            case R.id.nav_disclaimer:
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Disclaimer Menu");
                mFirebaseAnalytics.logEvent("Clicked_On_Disclaimer_Menu", bundle);
                disclaimerDialog();
                break;
            default:
        }
        return true;
    }

    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url, Context context) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void disclaimerDialog() {
        Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.disclaimer_layout);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            startActivity(new Intent(HomeActivity.this, StartActivity.class));
        }
    }
}