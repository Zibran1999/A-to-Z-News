package dailynews.localandglobalnews.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dailynews.localandglobalnews.BuildConfig;
import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.databinding.ActivityNewsDetailsBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.ShowAds;

public class NewsDetailsActivity extends AppCompatActivity {
    ActivityNewsDetailsBinding binding;
    NewsModel newsModel;
    String hindiDesc, englishDesc, plainText;
    Spanned spanned;
    char[] chars;
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    ShowAds showAds = new ShowAds();
    String hindiTitle;
    FirebaseAnalytics mFirebaseAnalytics;
    Bundle bundle = new Bundle();
    SharedPreferences preferences;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        binding.backIcon.setOnClickListener(v -> {
            onBackPressed();
        });
        Bundle bundle = getIntent().getExtras();
        newsModel = (NewsModel) bundle.getSerializable("news");
        Glide.with(NewsDetailsActivity.this)
                .load(ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg())
                .into(binding.newsDetailsImageView);

        showAds.showTopBanner(this, binding.adViewTop);
        showAds.showBottomBanner(this, binding.adViewBottom);
        getLifecycle().addObserver(showAds);
        showAds.showInterstitialAds(this);

        if (getIntent().getStringExtra("key") != null) {
            if (getIntent().getStringExtra("key").equals("gadgets")) {
                binding.newDetailsTitle.setText(HtmlCompat.fromHtml(newsModel.getEngTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                englishDesc = newsModel.getEngDesc();
                spanned = HtmlCompat.fromHtml(englishDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
                chars = new char[spanned.length()];
                TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
                plainText = new String(chars);
                binding.newDetailsDesc.loadData(String.valueOf(HtmlCompat.fromHtml(englishDesc, HtmlCompat.FROM_HTML_MODE_LEGACY)), "text/html", "UTF-8");
                binding.materialButtonToggleGroup.check(R.id.englishPreview);
                binding.readMoreBtn.setText(R.string.read_more);
                binding.whatsappShare.setOnClickListener(v -> {
                    shareData(String.valueOf(HtmlCompat.fromHtml(newsModel.getEngTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                });

            }
        } else {
            binding.whatsappShare.setOnClickListener(v -> {
                shareData(String.valueOf(HtmlCompat.fromHtml(newsModel.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
            });
            binding.newDetailsTitle.setText(HtmlCompat.fromHtml(newsModel.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
            hindiDesc = newsModel.getHinDesc();
            spanned = HtmlCompat.fromHtml(hindiDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
            chars = new char[spanned.length()];
            TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
            plainText = new String(chars);
            binding.newDetailsDesc.loadData(plainText, "text/html", "UTF-8");
            binding.materialButtonToggleGroup.check(R.id.hindiPreview);
            binding.readMoreBtn.setText(R.string.read_more_hindi);
        }


        binding.materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                switch (checkedId) {
                    case R.id.hindiPreview:
                        binding.whatsappShare.setOnClickListener(v -> {
                            shareData(String.valueOf(HtmlCompat.fromHtml(newsModel.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        });
                        binding.newDetailsTitle.setText(HtmlCompat.fromHtml(newsModel.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        showAds.showTopBanner(this, binding.adViewTop);
                        showAds.showBottomBanner(this, binding.adViewBottom);
                        binding.readMoreBtn.setText(R.string.read_more_hindi);
                        hindiDesc = newsModel.getHinDesc().replaceAll("<.*?>", "");
                        spanned = HtmlCompat.fromHtml(hindiDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        chars = new char[spanned.length()];
                        TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
                        plainText = new String(chars);
                        binding.newDetailsDesc.loadData(plainText, "text/html", "UTF-8");

                        break;
                    case R.id.englishPreview:
                        binding.whatsappShare.setOnClickListener(v -> {
                            shareData(String.valueOf(HtmlCompat.fromHtml(newsModel.getEngTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        });
                        binding.newDetailsTitle.setText(HtmlCompat.fromHtml(newsModel.getEngTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                        showAds.showTopBanner(this, binding.adViewTop);
                        showAds.showBottomBanner(this, binding.adViewBottom);
                        binding.readMoreBtn.setText(R.string.read_more);
                        englishDesc = newsModel.getEngDesc().replaceAll("<.*?>", "");
                        spanned = HtmlCompat.fromHtml(englishDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        chars = new char[spanned.length()];
                        TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
                        plainText = new String(chars);
                        binding.newDetailsDesc.loadData(plainText, "text/html", "UTF-8");

                        break;
                    default:
                }
            }
        });
        binding.lottieMail.setOnClickListener(v -> {
            CommonMethods.contactUs(NewsDetailsActivity.this);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Contact With Gmail");
            mFirebaseAnalytics.logEvent("Clicked_On_content_view_gmail_icon", bundle);

        });



        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(newsModel.getUrl());//replace with string to compare
        if(m.find()) {
            binding.readMoreBtn.setVisibility(View.VISIBLE);
            binding.readMoreBtn.setOnClickListener(v -> {
                openWebPage(newsModel.getUrl(), NewsDetailsActivity.this);
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Read More");
                mFirebaseAnalytics.logEvent("Clicked_On_read_more", bundle);
            });
        }else {
            binding.readMoreBtn.setVisibility(View.GONE);
        }

        binding.newsDetailsImageView.setOnClickListener(v -> {
            openWebPage(newsModel.getUrl(), this);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg());
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, newsModel.getTitle());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Content Image");
            mFirebaseAnalytics.logEvent("Clicked_On_content_image", bundle);

        });
    }

    private void shareData(String title) {
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Share on whatsapp");
        mFirebaseAnalytics.logEvent("Clicked_On_share_on_whatsapp", bundle);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File file = new File(this.getExternalCacheDir(), File.separator + "/" + "A To Z News" + ".jpeg");
        BitmapDrawable bitmapDrawable = (BitmapDrawable) binding.newsDetailsImageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setType("image/*");
            i.setPackage("com.whatsapp");
            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String shareMessage = title + "\n\n" + "That's Awesome...\uD83D\uDC40 \n\n Install Now!☺☺ \n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            i.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(i, "Share News from " + this.getString(R.string.app_name)));

        } catch (Exception e) {
            Log.e("ContentValue", e.getMessage());

            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setType("image/*");
                i.setPackage("com.whatsapp.w4b");
                i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String shareMessage = newsModel.getTitle() + "\n\n" + "That's Awesome...\uD83D\uDC40 \n\n Install Now!☺☺ \n\n";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
                i.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(i, "Share News from " + this.getString(R.string.app_name)));

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

    }


    @SuppressLint("QueryPermissionsNeeded")
    public void openWebPage(String url, Context context) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (preferences.getString("action", "").equals("")) {
            super.onBackPressed();
            finish();
            showAds.destroyBanner();
        } else {
            showAds.destroyBanner();
            preferences.edit().clear().apply();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
            overridePendingTransition(0, 0);

        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        showAds.showTopBanner(this, binding.adViewTop);
        showAds.showBottomBanner(this, binding.adViewBottom);
        IronSource.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        showAds.destroyBanner();
        IronSource.onPause(this);
    }

}