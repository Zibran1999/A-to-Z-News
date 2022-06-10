package dailynews.localandglobalnews.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;

import dailynews.localandglobalnews.databinding.ActivityStartBinding;
import dailynews.localandglobalnews.models.tabTextAndUrls.UrlOrTAbTextModel;
import dailynews.localandglobalnews.utils.ApiInterface;
import dailynews.localandglobalnews.utils.ApiWebServices;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {
    public static String tabText;
    ActivityStartBinding binding;
    FirebaseAnalytics firebaseAnalytics;
    ApiInterface apiInterface;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        apiInterface = ApiWebServices.getApiInterface();
        fetchTabText();
        binding.buttonStart.setOnClickListener(v -> {
            startActivity(new Intent(StartActivity.this, HomeActivity.class));
        });

    }

    private void fetchTabText() {
        Call<UrlOrTAbTextModel> call = apiInterface.fetchURLOrTABText("tab_text");
        call.enqueue(new Callback<UrlOrTAbTextModel>() {
            @Override
            public void onResponse(@NonNull Call<UrlOrTAbTextModel> call, @NonNull Response<UrlOrTAbTextModel> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    tabText = response.body().getUrl();
                    Log.d("tabText", tabText);
                }
            }

            @Override
            public void onFailure(@NonNull Call<UrlOrTAbTextModel> call, @NonNull Throwable t) {
                Log.d("contentError", t.getMessage());
            }
        });
    }
}