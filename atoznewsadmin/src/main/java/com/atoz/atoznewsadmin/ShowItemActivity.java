package com.atoz.atoznewsadmin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.atoz.atoznewsadmin.adapters.QuizAdapter;
import com.atoz.atoznewsadmin.adapters.QuizInterface;
import com.atoz.atoznewsadmin.databinding.ActivityShowItemBinding;
import com.atoz.atoznewsadmin.models.ApiInterface;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.MessageModel;
import com.atoz.atoznewsadmin.models.QuizModel;
import com.atoz.atoznewsadmin.models.QuizModelList;
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


public class ShowItemActivity extends AppCompatActivity implements QuizInterface {

    SwipeRefreshLayout swipeRefreshLayout;
    List<QuizModel> quizModelList = new ArrayList<>();
    RecyclerView recyclerView;
    ItemTouchHelper.SimpleCallback simpleCallback;
    MaterialAlertDialogBuilder builder;

    Dialog loadingDialog, addQuizDialog;
    ActivityShowItemBinding binding;
    String itemId, catId;
    ApiInterface apiInterface;
    Map<String, String> map = new HashMap<>();
    QuizAdapter quizAdapter;
    Button uploadQuizQuestionBtn;
    TextView question, op1, op2, op3, op4, ans;
    ImageView img;
    ActivityResultLauncher<String> launcher;
    Bitmap bitmap;
    String encodedImg, deleteImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        apiInterface = ApiWebServices.getApiInterface();

        catId = getIntent().getStringExtra("catId");
        builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Edit your Item")
                .setMessage("Edit")
                .setNeutralButton("CANCEL", (dialog1, which) -> {

                });

        launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {

            if (result != null) {
                try {
                    final InputStream inputStream = getContentResolver().openInputStream(result);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    if (bitmap != null)
                        encodedImg = imageStore(bitmap);
                    if (img != null)
                        img.setImageBitmap(bitmap);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }


        });

        //****Loading Dialog****/
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        loadingDialog.setCancelable(false);
        //**Loading Dialog****/
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        recyclerView = findViewById(R.id.edit_delete_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                itemId = quizModelList.get(viewHolder.getAdapterPosition()).getId();
                quizModelList.remove(viewHolder.getAdapterPosition());
                quizAdapter.updateQuizQuestions(quizModelList);
                deleteImg = quizModelList.get(viewHolder.getAdapterPosition()).getImg();
                map.put("id", itemId);
                map.put("img", deleteImg);
                deleteQuizItem(map);

            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);


        fetchQuiz(catId);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchQuiz(catId);
            swipeRefreshLayout.setRefreshing(false);
        });


    }

    public String imageStore(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] imageBytes = stream.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void deleteQuizItem(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.deleteQuizItems(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), response.body().getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageModel> call, @NonNull Throwable t) {
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
                Log.d("onResponse", t.getMessage());
            }
        });
    }

    private void fetchQuiz(String catid) {
        loadingDialog.show();
        quizAdapter = new QuizAdapter(this);
        Call<QuizModelList> call = apiInterface.fetchQuizQuestions(catid);
        call.enqueue(new Callback<QuizModelList>() {
            @Override
            public void onResponse(@NonNull Call<QuizModelList> call, @NonNull Response<QuizModelList> response) {
                if (response.isSuccessful()) {
                    quizModelList.clear();
                    if (!Objects.requireNonNull(response.body()).getData().isEmpty()) {
                        quizModelList.addAll(response.body().getData());
                    }
                    loadingDialog.dismiss();
                    quizAdapter.updateQuizQuestions(quizModelList);
                    recyclerView.setAdapter(quizAdapter);
                } else {
                    Log.d("onResponse", response.message());

                }
            }

            @Override
            public void onFailure(@NonNull Call<QuizModelList> call, @NonNull Throwable t) {
                Log.d("onResponse error", t.getMessage());

            }
        });

    }

    @Override
    public void onItemClicked(QuizModel quizModel) {

        builder.setPositiveButton("Edit", (dialog, which) -> {
            showUploadQuizQuestionDialog(quizModel);
        });
        builder.show();
    }

    @SuppressLint("SetTextI18n")
    private void showUploadQuizQuestionDialog(QuizModel quizModel) {

        addQuizDialog = new Dialog(this);
        addQuizDialog.setContentView(R.layout.quiz_layout);
        addQuizDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        addQuizDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.item_bg));
        addQuizDialog.setCancelable(false);
        addQuizDialog.show();
        TextView textView = addQuizDialog.findViewById(R.id.textView);
        textView.setText("Update Quiz");

        question = addQuizDialog.findViewById(R.id.question);
        op1 = addQuizDialog.findViewById(R.id.option);
        op2 = addQuizDialog.findViewById(R.id.option2);
        op3 = addQuizDialog.findViewById(R.id.option3);
        op4 = addQuizDialog.findViewById(R.id.option4);
        ans = addQuizDialog.findViewById(R.id.answer);
        img = addQuizDialog.findViewById(R.id.img);
        uploadQuizQuestionBtn = addQuizDialog.findViewById(R.id.upload_quiz);
        if (!Objects.equals(quizModel.getImg(), "null"))
            Glide.with(this).load(ApiWebServices.base_url + "quiz_images/" + quizModel.getImg()).into(img);
        else
            img.setOnClickListener(view -> launcher.launch("image/*"));
        encodedImg = quizModel.getImg();
        deleteImg = quizModel.getImg();
        addQuizDialog.findViewById(R.id.cancel).setOnClickListener(v -> addQuizDialog.dismiss());

        question.setText(quizModel.getQues());
        op1.setText(quizModel.getOp1());
        op2.setText(quizModel.getOp2());
        op3.setText(quizModel.getOp3());
        op4.setText(quizModel.getOp4());
        ans.setText(quizModel.getAns());

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
                if (encodedImg.length() < 100) {
                    map.put("id", quizModel.getId());
                    map.put("img", encodedImg);
                    map.put("delImg", deleteImg);
                    map.put("key", "0");
                    map.put("ques", ques);
                    map.put("op1", opt1);
                    map.put("op2", opt2);
                    map.put("op3", opt3);
                    map.put("op4", opt4);
                    map.put("ans", answer);
                    updateQuiz(map);
                } else {
                    map.put("id", quizModel.getId());
                    map.put("img", encodedImg);
                    map.put("delImg", deleteImg);
                    map.put("key", "1");
                    map.put("ques", ques);
                    map.put("op1", opt1);
                    map.put("op2", opt2);
                    map.put("op3", opt3);
                    map.put("op4", opt4);
                    map.put("ans", answer);
                    updateQuiz(map);
                }
            }
        });

    }

    private void updateQuiz(Map<String, String> map) {
        Call<MessageModel> call = apiInterface.updateQuiz(map);
        call.enqueue(new Callback<MessageModel>() {
            @Override
            public void onResponse(@NonNull Call<MessageModel> call, @NonNull Response<MessageModel> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    loadingDialog.dismiss();
                    fetchQuiz(catId);
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