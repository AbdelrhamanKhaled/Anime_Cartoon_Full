package com.anime.rashon.speed.loyert.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.ActivityRedirectBinding;
import com.anime.rashon.speed.loyert.model.Redirect;

public class RedirectActivity extends AppCompatActivity {

    ActivityRedirectBinding mBinding;

    Redirect redirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_redirect);

        getIntentData();
    }

    private void getIntentData(){
        redirect = (Redirect) getIntent().getSerializableExtra("redirect");

        mBinding.tvMessage.setText(redirect.getMessage());
    }

    public void openNewApp(View view) {

        if(redirect.getRedirect_type().equals("package_name")){

            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + redirect.getPackage_name())));

        }else if(redirect.getRedirect_type().equals("url")){

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect.getUrl()));
            startActivity(browserIntent);

        }
    }
}
