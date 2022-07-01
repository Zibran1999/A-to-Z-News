package com.atoz.atoznewsadmin;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.atoz.atoznewsadmin.databinding.ActivityShowQuizBinding;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowQuizActivity extends AppCompatActivity implements CategoryAdapter.CategoryListener {
    ActivityShowQuizBinding binding;
    List<CatModel> catModels;
    CategoryAdapter categoryAdapter;
    GridLayoutManager gridLayoutManager;
    ApiInterface apiInterface;
    UploadNewsBinding uploadNewsBinding;
    Dialog dialog, loadingDialog, addQuizDialog;
    ActivityResultLauncher<String> launcher;
    String encodedImg, formattedDate, selectTime;
    Map<String, String> map = new HashMap<>();
    Call<MessageModel> call;
    Bitmap bitmap;
    Button uploadQuizQuestionBtn;
    TextView question, op1, op2, op3, op4, ans;
    ImageView quizImg;
    private Button uploadBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_quiz);
        binding = ActivityShowQuizBinding.inflate(getLayoutInflater());
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
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    if (uploadNewsBinding != null)
                        uploadNewsBinding.choseNewsImg.setImageBitmap(bitmap);
                    if (bitmap != null)
                        encodedImg = imageStore(bitmap);
                    if (quizImg != null)
                        quizImg.setImageBitmap(bitmap);


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
        Call<List<CatModel>> call = apiInterface.fetchQuizCategory();
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
        String[] items = new String[]{"Add Quizzes", "Update Category", "Delete Category", "Show Quizzes"};
        builder.setTitle("Add Sub Category or Item").setCancelable(true).setItems(items, (dialogInterface, which) -> {
            switch (which) {
                case 0:
                    showUploadQuizQuestionDialog(catModel.getId());

                    break;
                case 1:
                    setUploadNewsDialog("Upload Category", catModel, "update");
                    break;
                case 2:
                    deleteCategory(catModel);
                case 3:
                    Intent intent = new Intent(this, ShowItemActivity.class);
                    intent.putExtra("catId", catModel.getId());
                    startActivity(intent);
            }
        });

        builder.show();

    }

    private void setUploadNewsDialog(String id, CatModel catModel, String upload) {
        dialog = new Dialog(this);
        uploadNewsBinding = UploadNewsBinding.inflate(getLayoutInflater());
        dialog.setContentView(uploadNewsBinding.getRoot());
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        dialog.setCancelable(false);
        dialog.show();
        uploadNewsBinding.cancel.setOnClickListener(view -> dialog.dismiss());

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


        uploadNewsBinding.newsTV.setText(id);

        uploadNewsBinding.choseNewsImg.setOnClickListener(view -> launcher.launch("image/*"));

        uploadNewsBinding.radioGroup.setVisibility(View.GONE);
        uploadNewsBinding.upload.setOnClickListener(view -> {
            String title = uploadNewsBinding.titleTv.getText().toString().trim();
            if (upload.equals("update")) {
                if (TextUtils.isEmpty(title)) {
                    uploadNewsBinding.titleTv.setError("title Required");
                    uploadNewsBinding.titleTv.requestFocus();
                    loadingDialog.dismiss();
                } else {
                    if (encodedImg.length() < 100) {
                        map.put("img", encodedImg);
                        map.put("deleteImg", catModel.getBanner());
                        map.put("title", title);
                        map.put("catId", catModel.getId());
                        map.put("key", "0");
                        call = apiInterface.updateQuizCategory(map);
                        uploadData(call, dialog);
                    } else {
                        map.put("img", encodedImg);
                        map.put("deleteImg", catModel.getBanner());
                        map.put("title", title);
                        map.put("catId", catModel.getId());
                        map.put("key", "1");
                        call = apiInterface.updateQuizCategory(map);
                        uploadData(call, dialog);
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
                    Toast.makeText(ShowQuizActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
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
                    call = apiInterface.deleteQuizCategory(map);
                    call.enqueue(new Callback<MessageModel>() {
                        @Override
                        public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ShowQuizActivity.this, Objects.requireNonNull(response.body()).getMessage(), Toast.LENGTH_SHORT).show();
                                fetchCategory();
                            } else {
                                Toast.makeText(ShowQuizActivity.this, Objects.requireNonNull(response.body()).getError(), Toast.LENGTH_SHORT).show();
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

    private void showUploadQuizQuestionDialog(String id) {

        addQuizDialog = new Dialog(this);
        addQuizDialog.setContentView(R.layout.quiz_layout);
        addQuizDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        addQuizDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(ShowQuizActivity.this, R.drawable.item_bg));
        addQuizDialog.setCancelable(false);
        addQuizDialog.show();


        question = addQuizDialog.findViewById(R.id.question);
        op1 = addQuizDialog.findViewById(R.id.option);
        op2 = addQuizDialog.findViewById(R.id.option2);
        op3 = addQuizDialog.findViewById(R.id.option3);
        op4 = addQuizDialog.findViewById(R.id.option4);
        ans = addQuizDialog.findViewById(R.id.answer);
        quizImg = addQuizDialog.findViewById(R.id.img);
        uploadQuizQuestionBtn = addQuizDialog.findViewById(R.id.upload_quiz);
        quizImg.setOnClickListener(view -> launcher.launch("image/*"));

        addQuizDialog.findViewById(R.id.cancel).setOnClickListener(v -> addQuizDialog.dismiss());
        uploadQuizQuestionBtn.setOnClickListener(v -> {

            String ques, opt1, opt2, opt3, opt4, answer;

            ques = question.getText().toString().trim();
            opt1 = op1.getText().toString().trim();
            opt2 = op2.getText().toString().trim();
            opt3 = op3.getText().toString().trim();
            opt4 = op4.getText().toString().trim();
            answer = ans.getText().toString().trim();
            if (TextUtils.isEmpty(ques)) {
                question.setError("field required");
            } else if (TextUtils.isEmpty(opt1)) {
                op1.setError("field required");
            } else if (TextUtils.isEmpty(opt2)) {
                op2.setError("field required");
            } else if (TextUtils.isEmpty(opt3)) {
                op3.setError("field required");
            } else if (TextUtils.isEmpty(opt4)) {
                op4.setError("field required");
            } else if (TextUtils.isEmpty(answer)) {
                ans.setError("field required");
            } else {

                if (encodedImg != null)
                    map.put("img", encodedImg);
                else
                    map.put("img", "null");

//                Log.d("contentValue", encodedImg != null ? encodedImg : "null");
                map.put("id", id);
                map.put("ques", ques);
                map.put("op1", opt1);
                map.put("op2", opt2);
                map.put("op3", opt3);
                map.put("op4", opt4);
                map.put("ans", answer);
                uploadQuiz(map);
            }
        });

    }

    private void uploadQuiz(Map<String, String> map) {
        loadingDialog.show();
        Call<MessageModel> call = apiInterface.uploadQuizQuestions(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {

                assert response.body() != null;
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    addQuizDialog.dismiss();

                } else {
                    Toast.makeText(getApplicationContext(), response.body().getError(), Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                Log.d("onResponse", t.getMessage());


            }
        });

    }
}