package com.app.cartoons.anime.newappcartoons.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityOfflineExoplayerBinding;
import com.app.cartoons.anime.newappcartoons.model.Download;
import com.app.cartoons.anime.newappcartoons.viewmodels.VideoViewModel;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;

import java.io.File;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class OfflineExoplayerActivity extends AppCompatActivity {

    private final String TAG = OfflineExoplayerActivity.class.getSimpleName();
    ActivityOfflineExoplayerBinding mBinding;

    SimpleExoPlayer player;
    private VideoViewModel videoViewModel;

    Download download;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offline_exoplayer);

        getIntentData();
        initFullScreenButton();
    }

    private void getIntentData(){
        download = (Download) getIntent().getSerializableExtra("download");
        initVideoViewModel();

        /*Intent intent = new Intent(Intent.ACTION_VIEW);

        String path = Environment.getExternalStoragePublicDirectory(download.getPath()).getAbsolutePath();

        intent.setDataAndType(Uri.parse(path), "video/*");

        startActivity(Intent.createChooser(intent, "Complete action using"));*/
    }

    private void initVideoViewModel(){
        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);
//        player = videoViewModel.getPlayer(Environment.getExternalStoragePublicDirectory(
//                download.getPath()).getAbsolutePath());


        String videoUrl = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + File.separator
                + download.getPath();

        player = videoViewModel.getPlayer(videoUrl);

        mBinding.videoView.setPlayer(player);

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
                Log.d(TAG, "onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.d(TAG, "onTracksChanged");
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.d(TAG, "onLoadingChanged");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d(TAG, "onPlayerStateChanged");

                if (playWhenReady && playbackState == Player.STATE_READY) {
                    // media actually playing
                    mBinding.progressBar.setVisibility(View.GONE);
                } else if (playWhenReady) {
                    // might be idle (plays after prepare()),
                    // buffering (plays when data available)
                    // or ended (plays when seek away from end)
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                } else {
                    // player paused in any state
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.d(TAG, "onRepeatModeChanged");
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Log.d(TAG, "onShuffleModeEnabledChanged");
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.d(TAG, "onPlayerError");
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.d(TAG, "onPositionDiscontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.d(TAG, "onPlaybackParametersChanged");
            }

            @Override
            public void onSeekProcessed() {
                Log.d(TAG, "onSeekProcessed");
            }
        });
    }
    private void initFullScreenButton(){
        if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT){ //Portrait
            mBinding.ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.open_full_screen));
        }else{ //Landscape
            mBinding.ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.exit_full_screen));
        }
    }

    public void fullScreen(View view) {
        if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT){ //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mBinding.ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.exit_full_screen));
        }else{ //Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mBinding.ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.open_full_screen));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initFullScreenButton();
    }
}
