package com.atoz.atoznewsadmin.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.atoz.atoznewsadmin.R;
import com.atoz.atoznewsadmin.models.ApiWebServices;
import com.atoz.atoznewsadmin.models.QuizModel;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    List<QuizModel> quizModelList = new ArrayList<>();
    QuizInterface quizInterface;

    public QuizAdapter(QuizInterface quizInterface) {
        this.quizInterface = quizInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String id, ques, op1, op2, op3, op4, ans;

        if (!Objects.equals(quizModelList.get(position).getImg(), "null"))
            Glide.with(holder.itemView.getContext()).load(ApiWebServices.base_url + "quiz_images/" + quizModelList.get(position).getImg()).into(holder.img);
        else
            holder.img.setVisibility(View.GONE);
        id = quizModelList.get(position).getId();
        ques = quizModelList.get(position).getQues();
        op1 = quizModelList.get(position).getOp1();
        op2 = quizModelList.get(position).getOp2();
        op3 = quizModelList.get(position).getOp3();
        op4 = quizModelList.get(position).getOp4();
        ans = quizModelList.get(position).getAns();
        holder.setData(id, ques, op1, op2, op3, op4, ans);
        holder.constraintLayout.setOnClickListener(v -> quizInterface.onItemClicked(quizModelList.get(position)));


    }

    @Override
    public int getItemCount() {
        return quizModelList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateQuizQuestions(List<QuizModel> quizModels) {
        quizModelList.clear();
        quizModelList.addAll(quizModels);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView question;
        MaterialButton opt1, opt2, opt3, opt4, ans;
        ConstraintLayout constraintLayout;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            question = itemView.findViewById(R.id.question);
            opt1 = itemView.findViewById(R.id.opt_one_card);
            opt2 = itemView.findViewById(R.id.opt_two_card);
            opt3 = itemView.findViewById(R.id.opt_three_card);
            opt4 = itemView.findViewById(R.id.opt_four_card);
            ans = itemView.findViewById(R.id.answer);
            constraintLayout = itemView.findViewById(R.id.constraint);

        }

        @SuppressLint("SetTextI18n")
        public void setData(String id, String ques, String op1, String op2, String op3, String op4, String ans) {
            question.setText(ques);
            opt1.setText("A: " + op1);
            opt2.setText("B: " + op2);
            opt3.setText("C: " + op3);
            opt4.setText("D: " + op4);
            this.ans.setText("Ans: " + ans);
        }
    }
}