package dailynews.localandglobalnews.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.bumptech.glide.Glide;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.databinding.ActivityNewsDetailsBinding;
import dailynews.localandglobalnews.models.BreakingNews.NewsModel;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;

public class NewsDetailsActivity extends AppCompatActivity {
    ActivityNewsDetailsBinding binding;
    NewsModel newsModel;
    String hindiDesc,englishDesc,plainText;
    Spanned spanned;
    char[] chars;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backIcon.setOnClickListener(v -> onBackPressed());
        binding.lottieMail.setOnClickListener(v -> CommonMethods.contactUs(NewsDetailsActivity.this));

        Bundle bundle = getIntent().getExtras();
        newsModel = (NewsModel) bundle.getSerializable("news");

        binding.readMoreBtn.setOnClickListener(v -> openWebPage(newsModel.getUrl(), NewsDetailsActivity.this));
        binding.newsDetailsImageView.setOnClickListener(v -> openWebPage(newsModel.getUrl(),this));
        Glide.with(NewsDetailsActivity.this)
                .load(ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg())
                .into(binding.newsDetailsImageView);
        binding.newDetailsTitle.setText(newsModel.getTitle());

        hindiDesc = newsModel.getHinDesc();
        spanned = HtmlCompat.fromHtml(hindiDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
        chars = new char[spanned.length()];
        TextUtils.getChars(spanned,0,spanned.length(),chars,0);
        plainText = new String(chars);

        binding.newDetailsDesc.loadData(plainText, "text/html", "UTF-8");

        Log.d("hindiDesc",newsModel.getHinDesc());
        Log.d("englishDesc",newsModel.getEngDesc());

        binding.materialButtonToggleGroup.check(R.id.hindiPreview);
        binding.materialButtonToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked){
                switch (checkedId){
                    case R.id.hindiPreview:
                        hindiDesc = newsModel.getHinDesc().replaceAll("<.*?>", "");
                        spanned = HtmlCompat.fromHtml(hindiDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        chars = new char[spanned.length()];
                        TextUtils.getChars(spanned,0,spanned.length(),chars,0);
                        plainText = new String(chars);
                        binding.newDetailsDesc.loadData(plainText, "text/html", "UTF-8");


                        break;
                    case R.id.englishPreview:

                        englishDesc = newsModel.getEngDesc().replaceAll("<.*?>", "");
                        spanned = HtmlCompat.fromHtml(englishDesc, HtmlCompat.FROM_HTML_MODE_LEGACY);
                        chars = new char[spanned.length()];
                        TextUtils.getChars(spanned,0,spanned.length(),chars,0);
                        plainText = new String(chars);
                        binding.newDetailsDesc.loadData(plainText, "text/html", "UTF-8");

                        break;
                    default:
                }
            }
        });

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
        super.onBackPressed();
        finish();
    }
}