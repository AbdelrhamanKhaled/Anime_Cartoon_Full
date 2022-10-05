package com.anime.rashon.speed.loyert.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anime.rashon.speed.loyert.adapters.EpisodesAdapter;
import com.anime.rashon.speed.loyert.databinding.ActivityEpisodeDownloadsBinding;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


public class EpisodeDownloadsActivity extends AppCompatActivity {
    ActivityEpisodeDownloadsBinding binding;
    private boolean grid;
    EpisodesAdapter adapter;
    List<Episode> episodeList = new ArrayList<>();
    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEpisodeDownloadsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

}