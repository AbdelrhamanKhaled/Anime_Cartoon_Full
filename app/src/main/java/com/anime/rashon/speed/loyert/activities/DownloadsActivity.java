package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.adapters.DownloadsAdapter;
import com.anime.rashon.speed.loyert.databinding.ActivityDownloadsBinding;
import com.anime.rashon.speed.loyert.model.Download;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.List;

import static com.anime.rashon.speed.loyert.app.Config.admob;

public class DownloadsActivity extends AppCompatActivity {

    SQLiteDatabaseManager sqLiteDatabaseManager;
    private List<Download> downloadList;

    ActivityDownloadsBinding mBinding;
//
    DownloadsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_downloads);

        initToolbar();
        initDatabase();
        initRecyclerview();
//        Config.showFacebookBannerAd(this, mBinding.addContainer);
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
    }

    private void initRecyclerview(){
        adapter = new DownloadsAdapter(DownloadsActivity.this, downloadList);

        mBinding.downloadsRecyclerview.setLayoutManager(new LinearLayoutManager(DownloadsActivity.this));
        mBinding.downloadsRecyclerview.setAdapter(adapter);
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
