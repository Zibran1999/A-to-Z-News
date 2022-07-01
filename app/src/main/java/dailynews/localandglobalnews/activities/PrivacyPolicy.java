package dailynews.localandglobalnews.activities;

import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.ironsource.mediationsdk.IronSource;

import dailynews.localandglobalnews.databinding.ActivityPrivacyPolicyBinding;
import dailynews.localandglobalnews.utils.ShowAds;

public class PrivacyPolicy extends AppCompatActivity {
    WebView webView;
    ShowAds ads = new ShowAds();
    ActivityPrivacyPolicyBinding binding;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        webView = binding.policy;

        key = getIntent().getStringExtra("key");
        if (key.equals("terms")) {
            webView.loadUrl("file:///android_asset/terms_and_conditions.html");
        } else if (key.equals("policy")) {
            webView.loadUrl("file:///android_asset/privacy_policy.html");
        }

        getLifecycle().addObserver(ads);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ads.destroyBanner();

    }
    @Override
    protected void onResume() {
        super.onResume();
        ads.showTopBanner(this, binding.adViewTop);
        ads.showBottomBanner(this, binding.adViewBottom);
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ads.destroyBanner();
        IronSource.onPause(this);
    }
}