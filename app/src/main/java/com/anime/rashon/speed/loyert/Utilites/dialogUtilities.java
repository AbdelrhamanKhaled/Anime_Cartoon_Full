package com.anime.rashon.speed.loyert.Utilites;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.anime.rashon.speed.loyert.R;

public class dialogUtilities {
    private ProgressDialog progressDialog ;

    public void ShowDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        try {
            progressDialog.show();
        }catch (Exception exception) {
            Log.d("ab_do" , exception.getMessage());
            return;
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void dismissDialog() {
        try {
            progressDialog.dismiss();
        }catch (Exception exception) {
            Log.d("ab_do" , exception.getMessage());
        }
    }
}
