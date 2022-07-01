package com.atoz.atoznewsadmin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.adapters.CategoryAdapter;
import com.atoz.atoznewsadmin.databinding.ActivityShowSubCategoryBinding;
import com.atoz.atoznewsadmin.databinding.UploadNewsBinding;
import com.atoz.atoznewsadmin.models.ApiInterface;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.CatModel;
import com.atoz.atoznewsadmin.models.MessageModel;
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

public class ShowSubCategory extends AppCompatActivity implements CategoryAdapter.CategoryListener {
    ActivityShowSubCategoryBinding binding;
    List<CatModel> catModels;
    CategoryAdapter categoryAdapter;
    GridLayoutManager gridLayoutManager;
    ApiInterface apiInterface;
    UploadNewsBinding uploadNewsBinding;
    Dialog dialog, loadingDialog;
    ActivityResultLauncher<String> launcher;
    String encodedImg, formattedDate, selectTime;
    Map<String, String> map = new HashMap<>();
    Call<MessageModel> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowSubCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.catRV.setLayoutManager(gridLayoutManager);
        catModels = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, this);
        binding.catRV.setAdapter(categoryAdapter);
        apiInterface = ApiWebServices.getApiInterface();

        loadingDialog = Utils.loadingDialog(this);
        fetchCategory();

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

            if (result != null) {
                try {
                    final InputStream inputStream = getContentResolver().openInputStream(result);
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    uploadNewsBinding.choseNewsImg.setImageBitmap(bitmap);
                    if (bitmap != null)
                        encodedImg = imageStore(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }


        });

    }

    private void fetchCategory() {
        loadingDialog.show();
        Call<List<CatModel>> call = apiInterface.fetchCategory(getIntent().getStringExtra("key"));
        call.enqueue(new Callback<List<CatModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CatModel>> call, @NonNull Response<List<CatModel>> response) {
                if (response.isSuccessful()) {
                    if (!Objects.requireNonNull(response.body()).isEmpty()) {
                        catModels.clear();
                        catModels.addAll(response.body());
                        categoryAdapter.updateList(catModels);
                        loadingDialog.dismiss();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CatModel>> call, @NonNull Throwable t) {
                loadingDialog.dismiss();
                Log.d("error", t.getMessage());
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void catOnClicked(CatModel catModel) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        if (catModel.getSubCat().equals("false") && catModel.getNews().equals("false")) {
            String[] items = new String[]{"Add Sub Category", "Add Item", "Update Category", "Delete Category"};
            builder.setTitle("Add Sub Category or Item").setCancelable(true).setItems(items, (dialogInterface, which) -> {
                switch (which) {
                    case 0:
                        setUploadNewsDialog("Upload Category", catModel, "Upload");
                        break;
                    case 1:
                        setUploadNewsDialog("upload News", catModel, "Upload");
                        break;
                    case 2:
                        setUploadNewsDialog("Upload Category", catModel, "update");
                        break;
                    case 3:
                        deleteCategory(catModel);
                }
            });
        } else if (catModel.getSubCat().equals("true")) {
            String[] items2 = new String[]{"Add a Subcategory", "Show Sub Category", "Update Category"};
            builder.setTitle("Add Subcategories").setCancelable(true).setItems(items2, (dialogInterface, which) -> {
                switch (which) {
                    case 0:
                        setUploadNewsDialog("Upload Category", catModel, "Upload");
                        break;
                    case 1:
                        Intent intent = new Intent(this, ShowSubCategory.class);
                        intent.putExtra("key", catModel.getId());
                        startActivity(intent);
                        break;
                    case 2:
                        setUploadNewsDialog("Upload Category", catModel, "update");
                        break;
                }
            });
        } else if (catModel.getNews().equals("true")) {
            String[] items3 = new String[]{"Add an Item", "Show Images", "Update Category"};
            builder.setTitle("Add Item").setCancelable(true).setItems(items3, (dialogInterface, which) -> {
                switch (which) {
                    case 0:
                        setUploadNewsDialog("upload News", catModel, "Upload");

                        break;
                    case 1:
                        Intent intent = new Intent(this, ShowNewsActivity.class);
                        intent.putExtra("key", catModel.getId());
                        startActivity(intent);
                        break;
                    case 2:
                        setUploadNewsDialog("Upload Category", catModel, "update");
                        break;
                }
            });
        }

        builder.show();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setUploadNewsDialog(String id, CatModel catModel, String upload) {
        dialog = new Dialog(this);
        uploadNewsBinding = UploadNewsBinding.inflate(getLayoutInflater());
        dialog.setContentView(uploadNewsBinding.getRoot());
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        dialog.setCancelable(false);
        dialog.show();
        uploadNewsBinding.cancel.setOnClickListener(view -> dialog.dismiss());

        if (id.equals("Upload Category")) {
            uploadNewsBinding.newsTV.setText(id);
            uploadNewsBinding.radioGroup.setVisibility(View.GONE);
            uploadNewsBinding.textInputLayout.setVisibility(View.GONE);
            uploadNewsBinding.textInput.setVisibility(View.GONE);
            uploadNewsBinding.textInputLayout2.setVisibility(View.GONE);
            uploadNewsBinding.textInputLayout3.setVisibility(View.GONE);

            if (upload.equals("update")) {
                encodedImg = catModel.getBanner();
                uploadNewsBinding.titleTv.setText(HtmlCompat.fromHtml(catModel.getTitle(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                Glide.with(this).load(ApiWebServices.base_url + "all_categories_images/" + catModel.getBanner()).into(uploadNewsBinding.choseNewsImg);
            }
        } else
            uploadNewsBinding.newsTV.setText(id);

        uploadNewsBinding.choseNewsImg.setOnClickListener(view -> launcher.launch("image/*"));

        uploadNewsBinding.radioGroup.setVisibility(View.GONE);
        uploadNewsBinding.upload.setOnClickListener(view -> {
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
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
            if (!id.equals("Upload Category")) {
                String engTitle = uploadNewsBinding.titleEngTv.getText().toString().trim();
                String url = uploadNewsBinding.url.getText().toString().trim();
                String desc = uploadNewsBinding.desc.getText().toString().trim();
                String engDesc = uploadNewsBinding.engDesc.getText().toString().trim();

                if (encodedImg == null) {
                    loadingDialog.dismiss();
                    Toast.makeText(ShowSubCategory.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(title)) {
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
                    map.put("img", encodedImg);
                    map.put("title", title);
                    map.put("engTitle", engTitle);
                    map.put("url", url);
                    map.put("desc", desc);
                    map.put("engDesc", engDesc);
                    map.put("date", formattedDate);
                    map.put("time", selectTime);
                    map.put("catId", catModel.getId());

                    call = apiInterface.uploadCatNews(map);
                    uploadData(call, dialog);

                }
            } else {
                if (upload.equals("Upload")) {
                    if (encodedImg == null) {
                        loadingDialog.dismiss();
                        Toast.makeText(ShowSubCategory.this, "Please Select an Image", Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(title)) {
                        uploadNewsBinding.titleTv.setError("title Required");
                        uploadNewsBinding.titleTv.requestFocus();
                        loadingDialog.dismiss();
                    } else {
                        map.put("img", encodedImg);
                        map.put("title", title);
                        map.put("date", formattedDate);
                        map.put("time", selectTime);
                        map.put("catId", catModel.getId());
                        call = apiInterface.uploadNewsSubCategory(map);
                        uploadData(call, dialog);
                    }
                } else if (upload.equals("update")) {
                    if (TextUtils.isEmpty(title)) {
                        uploadNewsBinding.titleTv.setError("title Required");
                        uploadNewsBinding.titleTv.requestFocus();
                        loadingDialog.dismiss();
                    } else {
                        if (encodedImg.length() < 100) {
                            map.put("img", encodedImg);
                            map.put("deleteImg", catModel.getBanner());
                            map.put("title", title);
                            map.put("date", formattedDate);
                            map.put("time", selectTime);
                            map.put("catId", catModel.getId());
                            map.put("key", "0");
                            call = apiInterface.updateNewsCategory(map);
                            uploadData(call, dialog);
                        } else {
                            map.put("img", encodedImg);
                            map.put("deleteImg", catModel.getBanner());
                            map.put("title", title);
                            map.put("date", formattedDate);
                            map.put("time", selectTime);
                            map.put("catId", catModel.getId());
                            map.put("key", "1");
                            call = apiInterface.updateNewsCategory(map);
                            uploadData(call, dialog);
                        }

                    }
                }
            }
        });

    }

    public String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);

        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void uploadData(Call<MessageModel> call, Dialog dialog) {
        loadingDialog.show();
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ShowSubCategory.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    fetchCategory();
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

    private void deleteCategory(CatModel catModel) {
        loadingDialog.show();
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Delete Banner")
                .setMessage("Would you like to delete this banner?")
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                })
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    map.put("id", catModel.getId());
                    map.put("img", catModel.getBanner());
                    map.put("title", catModel.getTitle());
                    call = apiInterface.deleteCategory(map);
                    call.enqueue(new Callback<MessageModel>() {
                        @Override
                        public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ShowSubCategory.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                                fetchCategory();
                            } else {
                                Toast.makeText(ShowSubCategory.this, Objects.requireNonNull(response.body()).getError(), Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                            loadingDialog.dismiss();

                        }
                    });
                }).show();
    }
}