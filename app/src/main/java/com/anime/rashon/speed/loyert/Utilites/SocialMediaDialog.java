package com.anime.rashon.speed.loyert.Utilites;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.SocialMediaDialogBinding;
import com.anime.rashon.speed.loyert.model.Redirect;

public class SocialMediaDialog {
    private final Dialog dialog ;
    private final Activity context ;

    public SocialMediaDialog(Activity context) {
        dialog = new Dialog(context);
        this.context = context ;
        createDialog();
    }

    private void createDialog() {
        SocialMediaDialogBinding binding = SocialMediaDialogBinding.inflate(context.getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_back);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
        initListeners(binding);
    }

    private void initListeners(SocialMediaDialogBinding binding) {
        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        binding.telegramImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.telegram_url))));
            }
        });
        binding.facebookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                context.startActivity(Config.newFacebookIntent(context.getPackageManager() , context.getString(R.string.facebook_url)));
            }
        });
        binding.youtubeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.youtube_url))));
            }
        });
        binding.twitterImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.twitter_url))));
            }
        });
        binding.instgramImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.instagram_url))));
            }
        });
        binding.tiktokImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.tiktok_url))));
            }
        });
    }


    public void showDialog () {
        if (dialog.isShowing()) {
            dismissDialog();
        }
        try {
            dialog.show();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog show exception " + exception.getMessage());
        }
    }



    public void dismissDialog () {
        try {
            dialog.dismiss();
        } catch (Exception exception) {
            Log.i("ab_do", "dialog login exception " + exception.getMessage());
        }
    }


}
