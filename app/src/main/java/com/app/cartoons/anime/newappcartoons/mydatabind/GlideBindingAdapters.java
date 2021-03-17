package com.app.cartoons.anime.newappcartoons.mydatabind;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import android.widget.ImageView;

import com.app.cartoons.anime.newappcartoons.R;
import com.bumptech.glide.Glide;


public class GlideBindingAdapters {

    @BindingAdapter("imgUrl")
    public static void setImage(ImageView imageView, String imgUrl){
        Context context = imageView.getContext();
        if(imgUrl != null && !imgUrl.equals("")){
            Glide.with(context)
                    .load(imgUrl)
                    .into(imageView);
        }else{
            Glide.with(context)
                    .load(R.drawable.img_placeholder)
                    .into(imageView);
        }
    }
}
