package dailynews.localandglobalnews.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.ironsource.mediationsdk.IronSource;

import java.util.Calendar;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.databinding.ActivityStartBinding;
import dailynews.localandglobalnews.models.tabTextAndUrls.UrlOrTAbTextModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.CommonMethods;
import dailynews.localandglobalnews.utils.ShowAds;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {
    ActivityStartBinding binding;
    FirebaseAnalytics firebaseAnalytics;
    ApiInterface apiInterface;
    ShowAds showAds = new ShowAds();
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiWebServices.getApiInterface();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        dialog = CommonMethods.getLoadingDialog(this);
        new Handler().postDelayed(() -> {
            showAds.showTopBanner(this, binding.adViewTop);
            showAds.showBottomBanner(this, binding.adViewBottom);
        }, 1000);
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        binding.buttonStart.setVisibility(View.VISIBLE);
        binding.buttonStart.startAnimation(myAnim);
        binding.buttonStart.setEnabled(false);

        new Handler().postDelayed(() -> binding.buttonStart.setEnabled(true), 3000);

        binding.buttonStart.setOnClickListener(v -> {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Start Activity");
            mFirebaseAnalytics.logEvent("Clicked_On_Start_Btn", bundle);

            dialog.show();
            new Handler().postDelayed(() -> {
                startActivity(new Intent(StartActivity.this, HomeActivity.class));
                dialog.dismiss();
            }, 2000);
        });
        setGreetings();

    }

    private void setGreetings() {
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours < 2) {
            binding.greating.setText("Good Mid-Night ☺");
        } else if (hours < 4) {
            binding.greating.setText("Good Night ☺");
        } else if (hours < 12) {
            binding.greating.setText("Good Morning ☺");

        } else if (hours < 16) {
            binding.greating.setText("Good Afternoon ☺");

        } else {
            binding.greating.setText("Good Evening ☺");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }

    @Override
    public void onBackPressed() {
        ShowExitDialog();

    }

    private void ShowExitDialog() {
        Dialog exitDialog = new Dialog(StartActivity.this);
        exitDialog.setContentView(R.layout.exit_dialog_layout);
        exitDialog.getWindow().setLayout(600, ViewGroup.LayoutParams.WRAP_CONTENT);
        exitDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        exitDialog.setCancelable(false);
        exitDialog.show();

        TextView rateNow = exitDialog.findViewById(R.id.rate_now);
        TextView okBtn = exitDialog.findViewById(R.id.ok);
        ImageView cancelBtn = exitDialog.findViewById(R.id.dismiss_btn);

        cancelBtn.setOnClickListener(v -> {
            exitDialog.dismiss();
        });
        okBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME | Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
            moveTaskToBack(true);
            System.exit(0);
        });

        rateNow.setOnClickListener(v -> {
            CommonMethods.rateApp(getApplicationContext());
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getStringExtra("key") != null) {
            if (getIntent().getStringExtra("key").equals("inter")) {
                showAds.showInterstitialAds(this);

            }
        }
    }
}
