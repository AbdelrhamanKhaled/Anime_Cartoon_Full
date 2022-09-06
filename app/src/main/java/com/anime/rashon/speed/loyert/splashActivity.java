package com.anime.rashon.speed.loyert;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArraySet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.anime.rashon.speed.loyert.activities.MainActivity;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.bumptech.glide.Glide;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class splashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        Glide.with(this)
                .asGif()
                .load(R.raw.wave_2)
                .centerCrop()
                .into((ImageView) findViewById(R.id.splash_img));
        init();
    }
    public void  init () {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        disposable.add(
                apiService
                        .latestEpisodesWithInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<EpisodeWithInfo>>() {
                            @Override
                            public void onSuccess(List<EpisodeWithInfo> retrivedEpisodeList) {
                                Log.i("splash_abdo" , "onSuccess ");
                                for (int i=0; i < retrivedEpisodeList.size(); i++) {
                                    if ((i+1) % 10 == 0){
                                        Log.i("ab_doa" , "Ad"  + i);
                                        // add Ad :)
                                        retrivedEpisodeList.add(i, new EpisodeWithInfo());
                                    }
                                }
                                List<EpisodeWithInfo> episodeList = new ArrayList<>(retrivedEpisodeList);
                                Intent intent = new Intent(getBaseContext() , MainActivity.class);
                                intent.putExtra("list" , (Serializable) episodeList);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Throwable e) {
                               Log.i("splash_abdo" , "error " + e.getMessage());
                            }
                        })
        );
    }
}