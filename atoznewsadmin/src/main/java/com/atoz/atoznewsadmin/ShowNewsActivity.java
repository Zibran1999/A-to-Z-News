package com.atoz.atoznewsadmin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.adapters.NewsAdapter;
import com.atoz.atoznewsadmin.databinding.ActivityShowNewsBinding;
import com.atoz.atoznewsadmin.databinding.UploadNewsBinding;
import com.atoz.atoznewsadmin.models.ApiInterface;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.MessageModel;
import com.atoz.atoznewsadmin.models.NewsModel;
import com.atoz.atoznewsadmin.utils.Utils;
import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowNewsActivity extends AppCompatActivity implements NewsAdapter.NewsInterface {
    ActivityShowNewsBinding binding;
    List<NewsModel> newsModels;
    NewsAdapter newsAdapter;
    LinearLayoutManager layoutManager;
    ApiInterface apiInterface;
    UploadNewsBinding uploadNewsBinding;
    Dialog dialog, loadingDialog;
    ActivityResultLauncher<String> launcher;
    String encodedImg, formattedDate, selectTime;
    Map<String, String> map = new HashMap<>();
    Call<MessageModel> call;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = Utils.loadingDialog(this);
        apiInterface = ApiWebServices.getApiInterface();
        newsModels = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.newsRV.setLayoutManager(layoutManager);
        binding.newsRV.setAdapter(newsAdapter);
        key = getIntent().getStringExtra("key");
        fetchNews(key);
        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

            if (result != null) {
                try {
                    final InputStream inputStream = getContentResolver().openInputStream(result);
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null)
                        uploadNewsBinding.choseNewsImg.setImageBitmap(bitmap);
                    encodedImg = imageStore(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void fetchNews(String key) {
        newsModels.clear();
        Call<List<NewsModel>> listCall = apiInterface.fetchNews(key);
        listCall.enqueue(new Callback<List<NewsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<NewsModel>> call, @NonNull Response<List<NewsModel>> response) {
                if (response.isSuccessful()) {
                    if (!Objects.requireNonNull(response.body()).isEmpty()) {
                        newsModels.addAll(response.body());
                        newsAdapter.updateList(newsModels);
                        loadingDialog.dismiss();
                        Log.d("contentValue", newsModels.get(0).getTitle());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<NewsModel>> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Log.d("contentValue", t.getMessage());
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void newsOnClicked(NewsModel newsModel) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        String[] items = new String[]{"Update Product", "Delete Product"};
        builder.setTitle("Update OR Delete Product").setCancelable(true).setItems(items, (dialogInterface, which) -> {
            switch (which) {
                case 0:
                    setUploadNewsDialog(newsModel);
                    break;
                case 1:
                    deleteProducts(newsModel, "deleteN");
                    break;

            }
        });

        builder.show();
    }

    private void deleteProducts(NewsModel newsModel, String deleteS) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        if (deleteS.equals("deleteS")) {
            deleteCall(newsModel);
        } else if (deleteS.equals("deleteN")) {
            builder.setTitle("Delete Product")
                    .setMessage("Would you like to delete this banner?")
                    .setNegativeButton("Cancel", (dialogInterface, i) -> {
                    })
                    .setPositiveButton("Ok", (dialogInterface, i) -> {
                        deleteCall(newsModel);

                    }).show();
        }
    }

    private void deleteCall(NewsModel newsModel) {
        loadingDialog.show();
        map.put("catId", newsModel.getCatId());
        map.put("id", newsModel.getId());
        map.put("img", newsModel.getNewsImg());
        call = apiInterface.deleteCatNews(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShowNewsActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    fetchNews(newsModel.getCatId());
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Log.d("contentValue", t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUploadNewsDialog(NewsModel newsModel) {
        dialog = new Dialog(this);
        uploadNewsBinding = UploadNewsBinding.inflate(getLayoutInflater());
        dialog.setContentView(uploadNewsBinding.getRoot());
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        dialog.setCancelable(false);
        dialog.show();
        uploadNewsBinding.cancel.setOnClickListener(view -> dialog.dismiss());

        Glide.with(this).load(ApiWebServices.base_url + "all_news_images/" + newsModel.getNewsImg()).into(uploadNewsBinding.choseNewsImg);

        uploadNewsBinding.choseNewsImg.setOnClickListener(view -> launcher.launch("image/*"));
        uploadNewsBinding.titleTv.setText(HtmlCompat.fromHtml(newsModel.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        uploadNewsBinding.titleEngTv.setText(HtmlCompat.fromHtml(newsModel.getEngTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        uploadNewsBinding.url.setText(newsModel.getUrl());
        uploadNewsBinding.desc.setText(HtmlCompat.fromHtml(newsModel.getDesc(), HtmlCompat.FROM_HTML_MODE_LEGACY));
        uploadNewsBinding.engDesc.setText(HtmlCompat.fromHtml(newsModel.getEngDesc(), HtmlCompat.FROM_HTML_MODE_LEGACY));
//        uploadNewsBinding.radioGroup.setVisibility(View.GONE);
        uploadNewsBinding.trendingNews.setChecked(Boolean.parseBoolean(newsModel.getTrending()));
        uploadNewsBinding.breakingNews.setChecked(Boolean.parseBoolean(newsModel.getBreaking()));
        uploadNewsBinding.gadgets.setChecked(Boolean.parseBoolean(newsModel.getGadgets()));



        encodedImg = newsModel.getNewsImg();
        uploadNewsBinding.upload.setOnClickListener(view -> {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            formattedDate = df.format(c);
            SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String stime = mdformat.format(c);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTimeFormate = new SimpleDateFormat("HH:mm");
            Date currentDate = null;
            try {
                currentDate = currentTimeFormate.parse(stime);
            } catch (ParseException e) {

                e.printStackTrace();
            }
            @SuppressLint("SimpleDateFormat") SimpleDateFormat CurrentfmtOut = new SimpleDateFormat("hh:mm aa");

            assert currentDate != null;
            selectTime = CurrentfmtOut.format(currentDate);
            String title = uploadNewsBinding.titleTv.getText().toString().trim();
            String engTitle = uploadNewsBinding.titleEngTv.getText().toString().trim();
            String url = uploadNewsBinding.url.getText().toString().trim();
            String desc = uploadNewsBinding.desc.getText().toString().trim();
            String engDesc = uploadNewsBinding.engDesc.getText().toString().trim();
            boolean trending;
            boolean breaking;
            boolean gadgets;
            trending = uploadNewsBinding.trendingNews.isChecked();
            breaking = uploadNewsBinding.breakingNews.isChecked();
            gadgets = uploadNewsBinding.gadgets.isChecked();

            if (TextUtils.isEmpty(title)) {
                uploadNewsBinding.titleTv.setError("title Required");
                uploadNewsBinding.titleTv.requestFocus();
                loadingDialog.dismiss();
            } else if (TextUtils.isEmpty(engTitle)) {
                uploadNewsBinding.titleEngTv.setError("title Required");
                uploadNewsBinding.titleEngTv.requestFocus();
                loadingDialog.dismiss();
            } else if (TextUtils.isEmpty(url)) {
                uploadNewsBinding.url.setError("Url Required");
                uploadNewsBinding.url.requestFocus();
                loadingDialog.dismiss();
            } else if (TextUtils.isEmpty(desc)) {
                uploadNewsBinding.desc.setError("desc Required");
                uploadNewsBinding.desc.requestFocus();
                loadingDialog.dismiss();
            } else if (TextUtils.isEmpty(engDesc)) {
                uploadNewsBinding.engDesc.setError("desc Required");
                uploadNewsBinding.engDesc.requestFocus();
                loadingDialog.dismiss();
            } else {
                if (encodedImg.length() < 100) {
                    map.put("img", encodedImg);
                    map.put("deleteImg", newsModel.getNewsImg());
                    map.put("title", title);
                    map.put("engTitle", engTitle);
                    map.put("url", url);
                    map.put("desc", desc);
                    map.put("engDesc", engDesc);
                    map.put("date", formattedDate);
                    map.put("time", selectTime);
                    map.put("id", newsModel.getId());
                    map.put("trending", String.valueOf(trending));
                    map.put("breaking", String.valueOf(breaking));
                    map.put("gadgets", String.valueOf(gadgets));

                    map.put("key", "0");
                    call = apiInterface.updateCatNews(map);
                } else {
                    map.put("img", encodedImg);
                    map.put("deleteImg", newsModel.getNewsImg());
                    map.put("title", title);
                    map.put("engTitle", engTitle);
                    map.put("url", url);
                    map.put("desc", desc);
                    map.put("engDesc", engDesc);
                    map.put("date", formattedDate);
                    map.put("time", selectTime);
                    map.put("id", newsModel.getId());
                    map.put("key", "1");
                    map.put("trending", String.valueOf(trending));
                    map.put("breaking", String.valueOf(breaking));
                    map.put("gadgets", String.valueOf(gadgets));

                    call = apiInterface.updateCatNews(map);
                }

                Log.d("contentValue", String.valueOf(breaking));
                uploadData(call, dialog);

            }

        });

    }

    private void uploadData(Call<MessageModel> call, Dialog dialog) {
        loadingDialog.show();
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShowNewsActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fetchNews(key);
                }

                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {

                Log.d("responseError", t.getMessage());
                loadingDialog.dismiss();
            }
        });
    }

    public String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);

        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

}