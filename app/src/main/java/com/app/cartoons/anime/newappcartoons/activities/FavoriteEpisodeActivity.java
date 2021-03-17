package com.app.cartoons.anime.newappcartoons.activities;

import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.app.cartoons.anime.newappcartoons.Database.SQLiteDatabaseManager;
import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityFavoriteEpisodesBinding;
import com.app.cartoons.anime.newappcartoons.model.Favorite;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;
import java.util.Objects;

import static com.app.cartoons.anime.newappcartoons.app.Config.admob;

public class FavoriteEpisodeActivity extends AppCompatActivity {

    ActivityFavoriteEpisodesBinding mBinding;
    SQLiteDatabaseManager sqliteManager;
    List<Favorite> favoriteList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_episodes);

        initToolbar();
        createBannerAd();
        setFavoriteData();
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("الحلقات المفضلة");
    }

    private void setFavoriteData(){
        sqliteManager = new SQLiteDatabaseManager(FavoriteEpisodeActivity.this);
        favoriteList = sqliteManager.getFavoriteData();
        mBinding.setFavoriteList(favoriteList);
    }

    private void createBannerAd(){
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);

        if(admob != null && admob.getBanner() != null){
            mAdView.setAdUnitId(admob.getBanner());

            if(mBinding.addContainer != null){
                ((LinearLayout)mBinding.addContainer).addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    //-----------Override Methods------------//

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;


            default: //No Action
        }

        return super.onOptionsItemSelected(item);
    }
}
