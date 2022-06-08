package com.atoz.atoznewsadmin.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.atoz.atoznewsadmin.R;

public class Utils {


    public static Dialog loadingDialog(Context context) {
        Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading);
        loadingDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.item_bg));
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

}
