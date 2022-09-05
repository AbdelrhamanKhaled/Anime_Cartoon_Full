package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.databinding.ActivityFavoriteCartoonsBinding;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

import static com.anime.rashon.speed.loyert.app.Config.admob;

public class FavoriteCartoonsActivity extends AppCompatActivity {

    ActivityFavoriteCartoonsBinding mBinding;

    SQLiteDatabaseManager sqliteManager;
    List<Cartoon> cartoonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorite_cartoons);

        initToolbar();
//        Config.showFacebookBannerAd(this, mBinding.addContainer);
        createBannerAd();
        setFavoriteData();
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("المسلسلات المفضلة");
    }

    private void setFavoriteData(){
        sqliteManager = new SQLiteDatabaseManager(FavoriteCartoonsActivity.this);
        cartoonList = sqliteManager.getCartoonsFavoriteData();
        mBinding.setCartoonList(cartoonList);
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
