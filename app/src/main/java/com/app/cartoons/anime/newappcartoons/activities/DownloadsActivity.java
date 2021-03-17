package com.app.cartoons.anime.newappcartoons.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.app.cartoons.anime.newappcartoons.Database.SQLiteDatabaseManager;
import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityDownloadsBinding;
import com.app.cartoons.anime.newappcartoons.model.Download;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

import static com.app.cartoons.anime.newappcartoons.app.Config.admob;

public class DownloadsActivity extends AppCompatActivity {

    SQLiteDatabaseManager sqLiteDatabaseManager;
    private List<Download> downloadList;

    ActivityDownloadsBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_downloads);

        initToolbar();
        initDatabase();
        createBannerAd();
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("التنزيلات");
    }

    private void initDatabase(){
        sqLiteDatabaseManager = new SQLiteDatabaseManager(this);
        downloadList = sqLiteDatabaseManager.getDownloads();
        mBinding.setDownloadList(downloadList);
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
