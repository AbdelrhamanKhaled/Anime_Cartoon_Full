package com.anime.rashon.speed.loyert.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.adapters.DownloadsAdapter;
import com.anime.rashon.speed.loyert.adapters.EpisodesAdapter;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.ActivityEpisodeDownloadsBinding;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class EpisodeDownloadsActivity extends AppCompatActivity implements DownloadsAdapter.OnRemoveDownloadedEpisode {
    ActivityEpisodeDownloadsBinding binding;
    DownloadsAdapter adapter;
    List<Episode> downloadList = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    int user_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.updateTheme(this);
        binding = ActivityEpisodeDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private  void init () {
        apiService = ApiClient.getClient(this).create(ApiService.class);
        LoginUtil loginUtil = new LoginUtil(this);
        user_id = loginUtil.getCurrentUser().getId() ;
        initToolbar();
        initRecyclerview();
        getEpisodeDownloads();
    }

    private void getEpisodeDownloads() {
        disposable.add(
                apiService
                        .getEpisodeDownloads(user_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Episode>>() {
                            @Override
                            public void onSuccess(List<Episode> episodeList) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                downloadList.clear();
                                downloadList.addAll(episodeList);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(EpisodeDownloadsActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void initRecyclerview(){
        adapter = new DownloadsAdapter(this, downloadList);
        binding.downloadedEpisodesRecyclerview.setLayoutManager(new LinearLayoutManager(EpisodeDownloadsActivity.this));
        binding.downloadedEpisodesRecyclerview.setAdapter(adapter);
    }

    private void initToolbar(){
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("التحميلات");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRemove(int pos) {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        disposable.add(
                apiService
                        .deleteDownloadEpisode(user_id , downloadList.get(pos).getId() , downloadList.get(pos).getVideo_url())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                downloadList.remove(pos);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(EpisodeDownloadsActivity.this, "حدث خطا ما !", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }
}