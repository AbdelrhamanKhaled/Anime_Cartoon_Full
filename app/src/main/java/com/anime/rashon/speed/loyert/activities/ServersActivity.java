package com.anime.rashon.speed.loyert.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.ActivityServersBinding;
import com.anime.rashon.speed.loyert.model.Episode;
import com.inside4ndroid.jresolver.Jresolver;
import com.inside4ndroid.jresolver.Model.Jmodel;

import java.util.ArrayList;
import java.util.Objects;

import static com.anime.rashon.speed.loyert.app.Config.isNetworkConnected;

public class ServersActivity extends AppCompatActivity {

    ActivityServersBinding mBinding;
    Episode episode;

    boolean server1 = true;
    boolean server2 = true;
    boolean server3 = true;
    boolean server4 = true;
    boolean server5 = true;
    boolean server6 = true;
    int WATCH_ACTION = 100 ;
    int DOWNLOAD_ACTION = 101 ;
    int Action = -1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_servers);
        getIntentData();
        initToolbar();
        checkSeversAvailability();
        setListeners();
    }

    private void setListeners() {
        mBinding.play1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION ;
                openServer1();
            }
        });
        mBinding.play2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION ;
                openServer2();
            }
        });
        mBinding.play3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION ;
                openServer3();
            }
        });
        mBinding.play4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION ;
                openServer4();
            }
        });
        mBinding.play5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION ;
                openServer5();
            }
        });
        mBinding.play6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = WATCH_ACTION ;
                openServer6();
            }
        });
        //------------------------------------------------------------------//
        mBinding.download1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION ;
                openServer1();
            }
        });
        mBinding.download2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION ;
                openServer2();
            }
        });
        mBinding.download3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION ;
                openServer3();
            }
        });
        mBinding.download4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION ;
                openServer4();
            }
        });
        mBinding.download5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION ;
                openServer5();
            }
        });
        mBinding.download6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Action = DOWNLOAD_ACTION ;
                openServer6();
            }
        });
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle(episode.getTitle());
        getSupportActionBar().setTitle("إختيار سيرفير");
    }

    private void getIntentData(){
        episode = (Episode) getIntent().getSerializableExtra("episode");
    }

    private void checkSeversAvailability(){
        if(episode.getVideo() == null || episode.getVideo().isEmpty()) {
            //mBinding.llServer1.setVisibility(View.GONE);
            mBinding.llServer1.setEnabled(false);
            mBinding.active1.setImageResource(R.drawable.not_active);
            server1 = false;
        }

        if(episode.getVideo1() == null || episode.getVideo1().isEmpty()){
            mBinding.llServer2.setEnabled(false);
            mBinding.active2.setImageResource(R.drawable.not_active);
            server2 = false;
        }

        if(episode.getVideo2() == null || episode.getVideo2().isEmpty()){
            mBinding.llServer3.setEnabled(false);
            mBinding.active3.setImageResource(R.drawable.not_active);
            server3 = false;
        }

        if(episode.getVideo3() == null || episode.getVideo3().isEmpty()){
            mBinding.llServer4.setEnabled(false);
            mBinding.active4.setImageResource(R.drawable.not_active);
            server4 = false;
        }

        if(episode.getVideo4() == null || episode.getVideo4().isEmpty()){
            mBinding.llServer5.setEnabled(false);
            mBinding.active5.setImageResource(R.drawable.not_active);
            server5 = false;
        }

        if(episode.getVideo5() == null || episode.getVideo5().isEmpty()){
            mBinding.llServer6.setEnabled(false);
            mBinding.active6.setImageResource(R.drawable.not_active);
            server6 = false;
        }
    }

    public void openServer1() {
        serverClicked(1, episode.getVideo(), episode.getjResolver());
    }

    public void openServer2() {
        if(episode.getVideo1().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(2, episode.getVideo1(), episode.getjResolver1());
    }

    public void openServer3() {
        if(episode.getVideo2().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(3, episode.getVideo2(), episode.getjResolver2());
    }

    public void openServer4() {
        if(episode.getVideo3().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(4, episode.getVideo3(), episode.getjResolver3());

    }

    public void openServer5() {

        if(episode.getVideo4().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(5, episode.getVideo4(), episode.getjResolver4());

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

    public void openServer6() {
        if(episode.getVideo4().isEmpty())
            Toast.makeText(ServersActivity.this, "غير متاح حاليا", Toast.LENGTH_SHORT).show();
        else
            serverClicked(5, episode.getVideo4(), episode.getjResolver3());

    }

    private void serverClicked (int serverNumber, String videoUrl, int needsXGetter){
        Log.i("ab_do" , "openVideoPlayer");
        episode.setError(false);
        if(!isNetworkConnected(ServersActivity.this)) {
            Toast.makeText(ServersActivity.this, "من فضلك تأكد من اتصالك بالانترنت", Toast.LENGTH_SHORT).show();
        }
        else {
            //Check if needs xgetter
            if(needsXGetter == 1){ //Needs extractions
                Log.i("ab_do" , "needsXGetter");
                Jresolver jresolver = new Jresolver(this);
                jresolver.onFinish(new Jresolver.OnTaskCompleted() {

                    @Override
                    public void onTaskCompleted(ArrayList<Jmodel> vidURL, boolean multiple_quality) {
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
                                handleAction(serverNumber , urls[which].toString() , Action);
                            });

                            AlertDialog dialog = builder.create();

                            dialog.setOnShowListener(dlg -> {

                                Objects.requireNonNull(dialog.getWindow()).getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL); // set title and message direction to RTL
                            });

                            dialog.show();

                        }

                        else {
                            //If single
                            String url = vidURL.get(0).getUrl();
                            handleAction(serverNumber, url , Action);
                        }
                    }

                    @Override
                    public void onError() {
                        //Error
                        episode.setError(true);
                        Toast.makeText(ServersActivity.this, "حدث خطأ ما يرجي تجربة سيرفر أخر", Toast.LENGTH_SHORT).show();
                        updateServerStatues(serverNumber);
                    }
                });

                jresolver.find(videoUrl);

            }
            else
                {
                    handleAction(serverNumber, videoUrl , Action);
                }
//        mBinding.progressBarLayout.setVisibility(View.GONE);
        }
    }

    private void updateServerStatues(int serverNumber) {
        switch (serverNumber) {
            case 1 :
                mBinding.active1.setImageResource(R.drawable.not_active);
                break;
            case 2 :
                mBinding.active2.setImageResource(R.drawable.not_active);
                break;
            case 3 :
                mBinding.active3.setImageResource(R.drawable.not_active);
                break;
            case 4 :
                mBinding.active4.setImageResource(R.drawable.not_active);
                break;
            case 5 :
                mBinding.active5.setImageResource(R.drawable.not_active);
                break;
            case 6 :
                mBinding.active6.setImageResource(R.drawable.not_active);
                break;
        }
    }

    private void handleAction(int serverNumber, String url, int Action) {
        if (episode.isError()) {
            updateServerStatues(serverNumber);
            Toast.makeText(getApplicationContext() , "حدث خطأ ما يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        if (url.startsWith("https://vudeo.net/") || url.startsWith("https://vudeo.io/") || url.startsWith("https://m3.vudeo.io/")) {
            updateServerStatues(serverNumber);
            Toast.makeText(getApplicationContext() , "حدث خطأ ما يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        if (episode.getVideo().startsWith("https://vudeo.net/") || episode.getVideo().startsWith("https://vudeo.io/") || episode.getVideo().startsWith("https://m3.vudeo.io/")) {
            updateServerStatues(serverNumber);
            Toast.makeText(getApplicationContext() , "حدث خطأ ما يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }


        if (Action == WATCH_ACTION)
         Config.openExoPlayerApp(this, url , episode);

        else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                            , 1);
                }
                else {
                    Config.startDownloadingEpisode(this, url, episode, getIntent().getStringExtra("playlistTitle"), getIntent().getStringExtra("cartoonTitle"));
                }
            }
    }

    //=======================Override Methods=================//


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Log.i("ab_do" ,"FromDownloader ");
                if (data == null) return;
                String path = data.getStringExtra("animePath");
                String name = data.getStringExtra("animeName");
                SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(this);
                sqliteManager.insertDownload(name, path);
            }
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
    }
}
