package dailynews.localandglobalnews.activities;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

import dailynews.localandglobalnews.R;
import dailynews.localandglobalnews.databinding.ActivityPlayQuizBinding;
import dailynews.localandglobalnews.models.QuizModel;
import dailynews.localandglobalnews.models.category.CatViewModel;
import dailynews.localandglobalnews.models.category.CatViewModelFactory;
import dailynews.localandglobalnews.utils.ApiWebServices;
import dailynews.localandglobalnews.utils.Prevalent;
import dailynews.localandglobalnews.utils.ShowAds;
import io.paperdb.Paper;

public class PlayQuizActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityPlayQuizBinding quizBinding;
    MaterialButton op1, op2, op3, op4;
    TextView question, questionNo;
    int currentPos, questionAttempted = 1, currentScore = 0;
    NestedScrollView quiz;
    MaterialCardView score;
    LottieAnimationView lottieAnimationView;
    List<QuizModel> quizModelList = new ArrayList<>();
    ShowAds showAds = new ShowAds();
    Random random;
    CatViewModel viewModel;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quizBinding = ActivityPlayQuizBinding.inflate(getLayoutInflater());
        setContentView(quizBinding.getRoot());
        key = getIntent().getStringExtra("id");
        setQuizLayout();
    }

    private void setQuizLayout() {
        question = quizBinding.question;
        op1 = quizBinding.optOneCard;
        op2 = quizBinding.optTwoCard;
        op3 = quizBinding.optThreeCard;
        op4 = quizBinding.optFourCard;
        questionNo = quizBinding.questionNo;
        quiz = quizBinding.quiz;
        score = quizBinding.score;
        getLifecycle().addObserver(showAds);

        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            quizBinding.adViewQuiz.setVisibility(View.GONE);
            showAds.showBottomBanner(this, quizBinding.adViewBottom);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            quizBinding.adViewBottom.setVisibility(View.GONE);
            showAds.showTopBanner(this, quizBinding.adViewQuiz);

        } else {
            showAds.showTopBanner(this, quizBinding.adViewQuiz);
            showAds.showBottomBanner(this, quizBinding.adViewBottom);
        }

        viewModel = new ViewModelProvider(this, new CatViewModelFactory(this.getApplication(), key)).get(CatViewModel.class);
        viewModel.getQuizQuestions().observe(this, quizModelList1 -> {
            quizModelList.clear();
            if (!quizModelList1.getData().isEmpty()) {
                quiz.setVisibility(View.VISIBLE);
                quizModelList.addAll(quizModelList1.getData());
                random = new Random();
                currentPos = random.nextInt(quizModelList.size());
                setDataToViews(currentPos);
            } else {
                quiz.setVisibility(View.GONE);
            }
        });

        op1.setOnClickListener(this);
        op2.setOnClickListener(this);
        op3.setOnClickListener(this);
        op4.setOnClickListener(this);


    }

    @SuppressLint("SetTextI18n")
    private void setDataToViews(int currentPos) {
//        lottieAnimationView.setVisibility(View.GONE);
        if (questionAttempted <= 15) {
            questionNo.setText(questionAttempted + "/15");
            if (!Objects.equals(quizModelList.get(currentPos).getImg(), "null"))
                Glide.with(this).load(ApiWebServices.base_url + "quiz_images/" + quizModelList.get(currentPos).getImg()).into(quizBinding.img);
            else
                quizBinding.img.setVisibility(View.GONE);
            question.setText(quizModelList.get(currentPos).getQues());
            op1.setText(quizModelList.get(currentPos).getOp1());
            op2.setText(quizModelList.get(currentPos).getOp2());
            op3.setText(quizModelList.get(currentPos).getOp3());
            op4.setText(quizModelList.get(currentPos).getOp4());
            if (questionAttempted % 5 == 0) {
                showAds.showInterstitialAds(this);
                Log.d("ContentValue", String.valueOf(questionAttempted));
            }
        } else {

            quiz.setVisibility(View.GONE);
            showScore();
        }

    }

    @SuppressLint("SetTextI18n")
    private void showScore() {
        score.setVisibility(View.VISIBLE);
        MaterialButton nextBtn;
        TextView scoreResult, cong;
        scoreResult = quizBinding.scoreResult;
        nextBtn = quizBinding.nextBtn;
        cong = quizBinding.cong;
        if (currentScore < 7) {
            cong.setText("Try again!");
            nextBtn.setText("Play Again");
        } else if (currentScore > 7 && currentScore < 12) {
            cong.setText("Good!");

        } else if (currentScore > 12 && currentScore <= 15) {
            cong.setText("Great!");
        }
        scoreResult.setText(currentScore + "/15");
        nextBtn.setOnClickListener(v -> {
            quiz.setVisibility(View.VISIBLE);
            score.setVisibility(View.GONE);
            currentPos = random.nextInt(quizModelList.size());
            setDataToViews(currentPos);
            questionAttempted = 1;
            currentScore = 0;
        });


    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.opt_one_card:
            case R.id.opt_two_card:
            case R.id.opt_three_card:
            case R.id.opt_four_card:

                MaterialButton button = (MaterialButton) v;
                checkAns(button);
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void checkAns(MaterialButton button) {
        if (quizModelList.get(currentPos).getAns().trim().toLowerCase(Locale.ROOT).equals(button.getText().toString().trim().toLowerCase(Locale.ROOT))) {
            currentScore++;
            button.setBackgroundColor(getResources().getColor(R.color.purple_700));
            button.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));

            switch (button.getId()) {
                case R.id.opt_one_card:
                    quizBinding.lottieWhatsapp.setAnimation(R.raw.right);
                    quizBinding.lottieWhatsapp.playAnimation();
                    quizBinding.lottieWhatsapp.setVisibility(View.VISIBLE);

                    break;
                case R.id.opt_two_card:
                    quizBinding.lottieWhatsapp2.setAnimation(R.raw.right);
                    quizBinding.lottieWhatsapp2.playAnimation();
                    quizBinding.lottieWhatsapp2.setVisibility(View.VISIBLE);
                    break;
                case R.id.opt_three_card:
                    quizBinding.lottieWhatsapp3.setAnimation(R.raw.right);
                    quizBinding.lottieWhatsapp3.playAnimation();
                    quizBinding.lottieWhatsapp3.setVisibility(View.VISIBLE);
                    break;
                case R.id.opt_four_card:
                    quizBinding.lottieWhatsapp4.setAnimation(R.raw.right);
                    quizBinding.lottieWhatsapp4.playAnimation();
                    quizBinding.lottieWhatsapp4.setVisibility(View.VISIBLE);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + button.getId());
            }

            new Handler().postDelayed(() -> {
                currentPos = random.nextInt(quizModelList.size());
                resetButtonColor();
                setDataToViews(currentPos);
            }, 3000);
        } else {
            showAnswer();
            new Handler().postDelayed(() -> {
                currentPos = random.nextInt(quizModelList.size());
                resetButtonColor();
                setDataToViews(currentPos);
            }, 2000);
            button.setBackgroundColor(Color.parseColor("#ff0000"));
            switch (button.getId()) {
                case R.id.opt_one_card:
                    quizBinding.lottieWhatsapp.setAnimation(R.raw.wrongcross);
                    quizBinding.lottieWhatsapp.playAnimation();
                    quizBinding.lottieWhatsapp.setVisibility(View.VISIBLE);

                    break;
                case R.id.opt_two_card:
                    quizBinding.lottieWhatsapp2.setAnimation(R.raw.wrongcross);
                    quizBinding.lottieWhatsapp2.playAnimation();
                    quizBinding.lottieWhatsapp2.setVisibility(View.VISIBLE);
                    break;
                case R.id.opt_three_card:
                    quizBinding.lottieWhatsapp3.setAnimation(R.raw.wrongcross);
                    quizBinding.lottieWhatsapp3.playAnimation();
                    quizBinding.lottieWhatsapp3.setVisibility(View.VISIBLE);
                    break;
                case R.id.opt_four_card:
                    quizBinding.lottieWhatsapp4.setAnimation(R.raw.wrongcross);
                    quizBinding.lottieWhatsapp4.playAnimation();
                    quizBinding.lottieWhatsapp4.setVisibility(View.VISIBLE);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + button.getId());
            }

        }
        button.setTextColor(Color.parseColor("#ffffff"));
        questionAttempted++;
    }

    private void showAnswer() {
        op1.setClickable(false);
        op2.setClickable(false);
        op3.setClickable(false);
        op4.setClickable(false);
        if (quizModelList.get(currentPos).getAns().trim().toLowerCase(Locale.ROOT).equals(op1.getText().toString().trim().toLowerCase(Locale.ROOT))) {

            op1.setBackgroundColor(getResources().getColor(R.color.purple_700));
            op1.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            op1.setTextColor(Color.parseColor("#ffffff"));

        } else if (quizModelList.get(currentPos).getAns().trim().toLowerCase(Locale.ROOT).equals(op2.getText().toString().trim().toLowerCase(Locale.ROOT))) {

            op2.setBackgroundColor(getResources().getColor(R.color.purple_700));
            op2.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            op2.setTextColor(Color.parseColor("#ffffff"));


        } else if (quizModelList.get(currentPos).getAns().trim().toLowerCase(Locale.ROOT).equals(op3.getText().toString().trim().toLowerCase(Locale.ROOT))) {

            op3.setBackgroundColor(getResources().getColor(R.color.purple_700));
            op3.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            op3.setTextColor(Color.parseColor("#ffffff"));


        } else if (quizModelList.get(currentPos).getAns().trim().toLowerCase(Locale.ROOT).equals(op4.getText().toString().trim().toLowerCase(Locale.ROOT))) {
            op4.setBackgroundColor(getResources().getColor(R.color.purple_700));
            op4.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            op4.setTextColor(Color.parseColor("#ffffff"));

        }

    }

    private void resetButtonColor() {
        if (Paper.book().read(Prevalent.bannerTopNetworkName).equals("IronSourceWithMeta")) {
            quizBinding.adViewQuiz.setVisibility(View.GONE);
            showAds.showBottomBanner(this, quizBinding.adViewBottom);

        } else if (Paper.book().read(Prevalent.bannerBottomNetworkName).equals("IronSourceWithMeta")) {
            quizBinding.adViewBottom.setVisibility(View.GONE);
            showAds.showTopBanner(this, quizBinding.adViewQuiz);

        } else {
            showAds.showTopBanner(this, quizBinding.adViewQuiz);
            showAds.showBottomBanner(this, quizBinding.adViewBottom);
        }
        op1.setClickable(true);
        op2.setClickable(true);
        op3.setClickable(true);
        op4.setClickable(true);

        op1.setBackgroundColor(Color.parseColor("#ffffff"));
        op2.setBackgroundColor(Color.parseColor("#ffffff"));
        op3.setBackgroundColor(Color.parseColor("#ffffff"));
        op4.setBackgroundColor(Color.parseColor("#ffffff"));
        op1.setTextColor(Color.parseColor("#000000"));
        op2.setTextColor(Color.parseColor("#000000"));
        op3.setTextColor(Color.parseColor("#000000"));
        op4.setTextColor(Color.parseColor("#000000"));
        quizBinding.lottieWhatsapp.setVisibility(View.GONE);
        quizBinding.lottieWhatsapp2.setVisibility(View.GONE);
        quizBinding.lottieWhatsapp3.setVisibility(View.GONE);
        quizBinding.lottieWhatsapp4.setVisibility(View.GONE);
        quizBinding.optOneCard.setBackgroundColor(getResources().getColor(R.color.white));
        quizBinding.optOneCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
        quizBinding.optTwoCard.setBackgroundColor(getResources().getColor(R.color.white));
        quizBinding.optTwoCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
        quizBinding.optThreeCard.setBackgroundColor(getResources().getColor(R.color.white));
        quizBinding.optThreeCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));
        quizBinding.optFourCard.setBackgroundColor(getResources().getColor(R.color.white));
        quizBinding.optFourCard.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.purple_700)));


    }
}