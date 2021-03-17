package com.app.cartoons.anime.newappcartoons.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.app.Config;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityServersBinding;
import com.app.cartoons.anime.newappcartoons.model.Episode;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
//import com.htetznaing.xgetter.Model.XModel;
//import com.htetznaing.xgetter.XGetter;

import java.util.Objects;

import static com.app.cartoons.anime.newappcartoons.app.Config.admob;
import static com.app.cartoons.anime.newappcartoons.app.Config.isNetworkConnected;

public class ServersActivity extends AppCompatActivity {

    ActivityServersBinding mBinding;
    Episode episode;

    boolean server1 = true;
    boolean server2 = true;
    boolean server3 = true;
    boolean server4 = true;
    boolean server5 = true;
    boolean server6 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_servers);

        getIntentData();
        initToolbar();
        checkSeversAvailability();
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(episode.getTitle());
    }

    private void getIntentData(){
        episode = (Episode) getIntent().getSerializableExtra("episode");
    }

    private void checkSeversAvailability(){
        if(episode.getVideo() == null || episode.getVideo().isEmpty()) {
            mBinding.llServer1.setVisibility(View.GONE);
            server1 = false;
        }

        if(episode.getVideo1() == null || episode.getVideo1().isEmpty()){
            mBinding.llServer2.setVisibility(View.GONE);
            server2 = false;
        }

        if(episode.getVideo2() == null || episode.getVideo2().isEmpty()){
            mBinding.llServer3.setVisibility(View.GONE);
            server3 = false;
        }

        if(episode.getVideo3() == null || episode.getVideo3().isEmpty()){
            mBinding.llServer4.setVisibility(View.GONE);
            server4 = false;
        }

        if(episode.getVideo4() == null || episode.getVideo4().isEmpty()){
            mBinding.llServer5.setVisibility(View.GONE);
            server5 = false;
        }

        if(episode.getVideo5() == null || episode.getVideo5().isEmpty()){
            mBinding.llServer6.setVisibility(View.GONE);
            server6 = false;
        }
    }

    public void openServer1(View view) {
        openVideoPlayer(1, episode.getVideo(), episode.getxGetter());
    }

    public void openServer2(View view) {
        if(episode.getVideo1().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            openVideoPlayer(2, episode.getVideo1(), episode.getxGetter1());
    }

    public void openServer3(View view) {
        if(episode.getVideo2().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            openVideoPlayer(3, episode.getVideo2(), episode.getxGetter2());
    }

    public void openServer4(View view) {
        if(episode.getVideo3().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            openVideoPlayer(4, episode.getVideo3(), episode.getxGetter3());

    }

    public void openServer5(View view) {

        if(episode.getVideo4().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            openVideoPlayer(5, episode.getVideo4(), episode.getxGetter4());

        /*if(!server1 && !server2 && !server3 &&
                !server4){
            if(episode.getVideo4().isEmpty())
                Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
            else
                openVideoPlayer(5, episode.getVideo4(), episode.getxGetter4());
        }else{
            Toast.makeText(ServersActivity.this, "سيتم تفعيل السيرفر في حال فشلت باقي السيرفرات", Toast.LENGTH_SHORT).show();
        }*/
    }

    public void openServer6(View view) {
        if(episode.getVideo4().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            openVideoPlayer(5, episode.getVideo4(), episode.getxGetter3());

    }

    private void openVideoPlayer(int serverNumber, String videoUrl, int needsXGetter){

        Config.optionsDialog(this, videoUrl, episode,
                getIntent().getStringExtra("playlistTitle"),
                getIntent().getStringExtra("cartoonTitle"));

        /*if(!isNetworkConnected(ServersActivity.this)){
            Toast.makeText(ServersActivity.this, "من فضلك تأكد من اتصالك بالانترنت", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(ServersActivity.this, ExoplayerActivity.class);
            intent.putExtra("episode", episode);
            intent.putExtra("title", getIntent().getStringExtra("title"));
            intent.putExtra("thumb", getIntent().getStringExtra("thumb"));
            intent.putExtra("playlistTitle", getIntent().getStringExtra("playlistTitle"));
            intent.putExtra("cartoonTitle", getIntent().getStringExtra("cartoonTitle"));
            intent.putExtra("chosenServer", serverNumber);
            intent.putExtra("videoUrl", videoUrl);
            startActivityForResult(intent, serverNumber);

            //Check if needs xgetter
            *//*if(needsXGetter == 1){ //Needs extractions

                XGetter xGetter = new XGetter(this);
                xGetter.onFinish(new XGetter.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                        if (multiple_quality){
                            //This video you can choose qualities
                            CharSequence[] qualities = new CharSequence[vidURL.size()];
                            CharSequence[] urls = new CharSequence[vidURL.size()];

                            for (int i=0; i<vidURL.size(); i++){
//                            String url = model.getUrl();
                                qualities[i] = vidURL.get(i).getQuality();
                                urls[i] = vidURL.get(i).getUrl();
                            }

                            AlertDialog.Builder builder = new AlertDialog.Builder(ServersActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("اختار جودة الحلقة");
                            builder.setItems(qualities, (dialog, which) -> {

                                intent.putExtra("videoUrl", urls[which]);
                                startActivityForResult(intent, serverNumber);
                            });

                            AlertDialog dialog = builder.create();

                            dialog.setOnShowListener(dlg -> {

                                Objects.requireNonNull(dialog.getWindow()).getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // set title and message direction to RTL
                            });

                            dialog.show();

                        }else {
                            //If single
                            String url = vidURL.get(0).getUrl();

                            intent.putExtra("videoUrl", url);
                            startActivityForResult(intent, serverNumber);
                        }
                    }

                    @Override
                    public void onError() {
                        //Error
                        Toast.makeText(ServersActivity.this, "حدث خطأ ما الرجاء المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                    }
                });

                xGetter.find(videoUrl);

            }else{
                intent.putExtra("videoUrl", videoUrl);
                startActivityForResult(intent, serverNumber);
            }*//*
//        mBinding.progressBarLayout.setVisibility(View.GONE);
        }*/
    }

    private void createInterstitialAd2(){

        if(admob != null && admob.getInterstitial2() != null){
            final InterstitialAd mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(admob.getInterstitial2());
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mInterstitialAd.show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                    // Code to be executed when when the interstitial ad is closed.
                }
            });
        }

    }

    //=======================Override Methods=================//

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){

            //No Action

            /*switch (requestCode){
                case 1:
                    server1 = false;
                    break;
                case 2:
                    server2 = false;
                    break;
                case 3:
                    server3 = false;
                    break;
                case 4:
                    server4 = false;
                    break;

            }


            if(!server1 && !server2 && !server3 &&
                !server4){
                mBinding.btnBackupServer.setBackground(getResources().getDrawable(R.drawable.blue_btn_bk));
            }*/
        }else{
            //show ad
            createInterstitialAd2();
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
