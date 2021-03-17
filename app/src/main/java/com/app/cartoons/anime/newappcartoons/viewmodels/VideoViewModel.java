package com.app.cartoons.anime.newappcartoons.viewmodels;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.app.cartoons.anime.newappcartoons.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoViewModel extends AndroidViewModel {

    Application application;

    private final String TAG = VideoViewModel.class.getSimpleName();

    SimpleExoPlayer player;

    ExtractorMediaSource mediaSource;


    public VideoViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    public SimpleExoPlayer getPlayer(String videoUrl) {

        player = new SimpleExoPlayer.Builder(application).build();

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(application, Util.getUserAgent(application, application.getString(R.string.app_name)));

        ProgressiveMediaSource.Factory mediaSourceFactory =
                new ProgressiveMediaSource.Factory(dataSourceFactory);
        MediaSource mediaSource =
                mediaSourceFactory.createMediaSource(Uri.parse(videoUrl));
        player.prepare(mediaSource);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.setPlayWhenReady(true);


        return player;
    }

    public SimpleExoPlayer getPlayerWithAds(AdsMediaSource mediaSource) {

        player = new SimpleExoPlayer.Builder(application).build();
        player.prepare(mediaSource);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.setPlayWhenReady(true);


        return player;
    }

    public void preparePlayer(){
        if(player.getPlaybackState() != Player.STATE_READY){
            player.prepare(mediaSource, false, false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(player != null)
            player.release();
    }
}
