package com.app.cartoons.anime.newappcartoons.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.cartoons.anime.newappcartoons.Database.SQLiteDatabaseManager;
import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.app.Config;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityExoplayerBinding;
import com.app.cartoons.anime.newappcartoons.model.Episode;
import com.app.cartoons.anime.newappcartoons.model.Favorite;
import com.app.cartoons.anime.newappcartoons.model.Report;
import com.app.cartoons.anime.newappcartoons.network.ApiClient;
import com.app.cartoons.anime.newappcartoons.network.ApiService;
import com.app.cartoons.anime.newappcartoons.viewmodels.VideoViewModel;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tonyodev.fetch2.Fetch;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

public class ExoplayerActivity extends AppCompatActivity {

    private final int PERMISSIONS_REQUEST_STORAGE = 1;
    private final String TAG = ExoplayerActivity.class.getSimpleName();
    ActivityExoplayerBinding mBinding;

    SimpleExoPlayer player;

    Episode episode;
    String title;
    String thumb;
    String playlistTitle;
    String cartoonTitle;
    String videoUrl;

    SQLiteDatabaseManager sqliteManager;
    IntentFilter intentFilter;
    ExtractorMediaSource mediaSource;
    boolean isBroadcastRegistered = false;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    private VideoViewModel videoViewModel;
    ImaAdsLoader adsLoader;
    private Fetch fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exoplayer);

        initSqliteDatabase();
        getIntentData();
        initFullScreenButton();
        checkIfEpisodeIsFavorite(episode.getId());
        initConnectivityIntentFilter();
        initRetrofit();
        registerReceiver(connectStateReceiver, intentFilter);
        fullScreen(null);
    }

    private void initVideoViewModel(){
        videoViewModel = ViewModelProviders.of(this).get(VideoViewModel.class);

        videoUrl = getIntent().getStringExtra("videoUrl");
        initPlayer(videoUrl);

        /*int needsXGetter = 0;

        switch (getIntent().getIntExtra("chosenServer", 1)){

            case 1: {
                videoUrl = episode.getVideo();
                needsXGetter = episode.getxGetter();
            }
            break;

            case 2: {
                videoUrl = episode.getVideo1();
                needsXGetter = episode.getxGetter1();
            }
                break;

            case 3: {
                videoUrl = episode.getVideo2();
                needsXGetter = episode.getxGetter2();
            }
                break;

            case 4: {
                videoUrl = episode.getVideo3();
                needsXGetter = episode.getxGetter3();
            }
                break;

            case 5: {
                videoUrl = episode.getVideo4();
                needsXGetter = episode.getxGetter4();
            }
                break;

        }

        if(needsXGetter == 1){
            XGetter xGetter = new XGetter(this);
            xGetter.onFinish(new XGetter.OnTaskCompleted() {

                @Override
                public void onTaskCompleted(ArrayList<XModel> vidURL, boolean multiple_quality) {
                    if (multiple_quality){
                        //This video you can choose qualities
                        for (XModel model : vidURL){
                            String url = model.getUrl();
                            initPlayer(url);

                            return;
                        }
                    }else {
                        //If single
                        String url = vidURL.get(0).getUrl();
                        initPlayer(url);
                    }
                }

                @Override
                public void onError() {
                    //Error
                }
            });

            xGetter.find(videoUrl);
        }else{
            initPlayer(videoUrl);
        }*/
    }

    private void initPlayer(String videoUrl) {
        player = videoViewModel.getPlayerWithAds(createMediaSourceWithAds(videoUrl));
        mBinding.videoView.setPlayer(player);
        adsLoader.setPlayer(player);

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
                setResult(RESULT_OK);
                finish();
                Toast.makeText(ExoplayerActivity.this, "السيرفر توقف عن العمل جرب سيرفر اخر", Toast.LENGTH_LONG).show();
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

    private void initRetrofit(){
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void initConnectivityIntentFilter(){
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    }

    private void getIntentData(){
        episode = (Episode) getIntent().getSerializableExtra("episode");
        title = getIntent().getStringExtra("title");
        thumb = getIntent().getStringExtra("thumb");
        playlistTitle = getIntent().getStringExtra("playlistTitle");
        cartoonTitle = getIntent().getStringExtra("cartoonTitle");


        Log.e(TAG, episode.getVideo());

        //Set Fake data
        /*episode = new Episode(801, "episode3", "", "https://www.googleapis.com/drive/v3/files/1ECjxMKDQe179roBefpqFn6cmji-s9O7C?alt=media&key=AIzaSyBpGu8j3PJI_wNuohCIodyFV-T0-VBEh0U&name=.mp4",
                20);
        title = "";
        thumb = "";
        playlistTitle = "";
        cartoonTitle = "Conan";*/
    }

    private void initSqliteDatabase(){
        sqliteManager = new SQLiteDatabaseManager(ExoplayerActivity.this);
    }

    private void initializePlayer() {

        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

        mBinding.videoView.setPlayer(player);

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayer"));

        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(episode.getVideo()));
        player.prepare(mediaSource);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        player.setPlayWhenReady(true);

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

    public void share(View view) {
        Config.shareApp(ExoplayerActivity.this);
    }

    private void checkIfEpisodeIsFavorite(int episodeId){
        if(sqliteManager.isFavorite(episodeId)){
            mBinding.filledStar.setVisibility(View.VISIBLE);
            mBinding.emptyStar.setVisibility(View.GONE);
        }
        else{
            mBinding.filledStar.setVisibility(View.GONE);
            mBinding.emptyStar.setVisibility(View.VISIBLE);
        }
    }

    public void removeFavorite(View view) {
        sqliteManager.deleteFavorite(episode.getId());
        mBinding.filledStar.setVisibility(View.GONE);
        mBinding.emptyStar.setVisibility(View.VISIBLE);
    }

    public void addFavorite(View view) {
        if(sqliteManager
                .insertFavorite
                        (new Favorite(episode.getId(), title, playlistTitle,cartoonTitle, thumb, episode.getVideo()))
                != -1){
            mBinding.filledStar.setVisibility(View.VISIBLE);
            mBinding.emptyStar.setVisibility(View.GONE);
        }
    }

    public void report(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExoplayerActivity.this);

        builder.setMessage("هل هناك مشكلة في الفيديو وتريد الابلاغ عنها؟");
        builder.setCancelable(true);
        builder.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitReport();
            }
        });

        builder.setNegativeButton("لا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void submitReport(){
        Report report = new Report(episode.getId(), title, episode.getVideo(), playlistTitle, cartoonTitle);
        disposable.add(
                apiService
                        .createReport(report)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<String>() {
                            @Override
                            public void onSuccess(String response) {
                                showThanksDialog();
                            }

                            @Override
                            public void onError(Throwable e) {
                                showThanksDialog();
                            }
                        })
        );
    }

    private void performBackAction(){
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void fullScreen(View view) {
        if(getResources().getConfiguration().orientation == ORIENTATION_PORTRAIT){ //Portrait
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mBinding.ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.exit_full_screen));
        }else{ //Landscape
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mBinding.ivFullScreen.setImageDrawable(getResources().getDrawable(R.drawable.open_full_screen));
        }
    }

    private void showThanksDialog(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExoplayerActivity.this);

        builder.setMessage("شكرا لك! تم الابلاغ عن الحلقة سيتم حل المشكلة قريبا! يمكنك مشاهدة باقي الحلقات حتي يتم حل المشكلة");
        builder.setCancelable(true);
        builder.setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                ExoplayerActivity.this.finish();
            }
        });


        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private AdsMediaSource createMediaSourceWithAds(String videoUrl) {

//        adsLoader = new ImaAdsLoader(this, Uri.parse("https://pubads.g.doubleclick.net/gampad/live/ads?iu=/21914104527/54a4a5a5a5a5a5&description_url=http%3A%2F%2Fmoslslatcrrtoon.com&tfcd=1&npa=1&sz=400x300%7C640x480&ciu_szs=168x42%2C120x30&min_ad_duration=5000&max_ad_duration=30000&cmsid=[placeholder]&vid=[placeholder]&gdfp_req=1&output=vast&unviewed_position_start=1&env=vp&impl=s&correlator="));
        adsLoader = new ImaAdsLoader(this, Uri.parse("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator="));
//        adsLoader = new ImaAdsLoader(this, Uri.parse(Config.admob.getImaAd()));

        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)));

        ProgressiveMediaSource.Factory mediaSourceFactory =
                new ProgressiveMediaSource.Factory(dataSourceFactory);

        // Create the MediaSource for the content you wish to play.
        MediaSource mediaSource =
                mediaSourceFactory.createMediaSource(Uri.parse(videoUrl));

        // Create the AdsMediaSource using the AdsLoader and the MediaSource.
        AdsMediaSource adsMediaSource =
                new AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, mBinding.videoView);

        return adsMediaSource;
    }

    //--------------Override Methods-------------//

    @Override
    protected void onStart() {
        super.onStart();
        if(player != null){
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(player != null){
            player.setPlayWhenReady(false);
        }
        /*mBinding.videoView.setPlayer(null);
        if(player != null)
            player.release();
        player = null;*/
    }

    @Override
    public void onBackPressed() {
        performBackAction();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initFullScreenButton();
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        unregisterReceiver(connectStateReceiver);
        super.onDestroy();
    }

    public void download(View view) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExoplayerActivity.this);

        builder.setMessage("تحميل الحلقة عن طريق");
        builder.setCancelable(true);
        builder.setPositiveButton("تنزيل عادي", (dialog, which) -> {
            dialog.cancel();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                        , PERMISSIONS_REQUEST_STORAGE);

            }else {
                startDownloadingEpisode();
            }
        });

        builder.setNegativeButton("ADM", (dialog, which) -> {
            downloadWithADM();
        });


        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void startDownloadingEpisode(){
        Uri episodeUri = Uri.parse(videoUrl.trim());

        DownloadManager.Request req=new DownloadManager.Request(episodeUri);

        String episodeUniqueName = UUID.randomUUID().toString();

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(episode.getTitle())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        episodeUniqueName + ".mp4")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);;

        /*.setDestinationInExternalPublicDir(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/AnimeCartoons/" + cartoonTitle + "/" + playlistTitle,
                episode.getTitle() + ".mp4")*/

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(req);

        Toast.makeText(this, "يتم تحميل الحلقة الان...", Toast.LENGTH_SHORT).show();

        //------Save download to the database -----//
        /*StringBuilder filePathBuilder = new StringBuilder();
        filePathBuilder.append("/AnimeCartoons/");
        filePathBuilder.append(cartoonTitle, filePathBuilder.length() - 1, cartoonTitle.length());
        filePathBuilder.append("/");
        filePathBuilder.append(episode.getTitle(), filePathBuilder.length() - 1, cartoonTitle.length());
        filePathBuilder.append(".mp4", filePathBuilder.length(), cartoonTitle.length());*/

//        Toast.makeText(this, filePathBuilder.toString(), Toast.LENGTH_SHORT).show();

        sqliteManager.insertDownload(cartoonTitle + " - " + playlistTitle + " - " + episode.getTitle()
                ,  episodeUniqueName + ".mp4");
    }

    //Example Download Google Drive Video with ADM
    public void downloadWithADM() {
        boolean appInstalledOrNot = appInstalledOrNot( "com.dv.adm");
        boolean appInstalledOrNot2 = appInstalledOrNot("com.dv.adm.pay");
        boolean appInstalledOrNot3 = appInstalledOrNot( "com.dv.adm.old");
        String str3;
        if (appInstalledOrNot || appInstalledOrNot2 || appInstalledOrNot3) {
            if (appInstalledOrNot2) {
                str3 = "com.dv.adm.pay";
            } else if (appInstalledOrNot) {
                str3 = "com.dv.adm";
            } else {
                str3 = "com.dv.adm.old";
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(videoUrl), "application/x-mpegURL");
                intent.setPackage(str3);
                /*if (xModel.getCookie()!=null) {
                    intent.putExtra("Cookie", xModel.getCookie());
                    intent.putExtra("Cookies", xModel.getCookie());
                    intent.putExtra("cookie", xModel.getCookie());
                    intent.putExtra("cookies", xModel.getCookie());
                }*/

                startActivity(intent);
                return;
            } catch (Exception e) {
                return;
            }
        }
        str3 = "com.dv.adm";
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+str3)));
        } catch (ActivityNotFoundException e2) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+str3)));
        }
    }

    public boolean appInstalledOrNot(String str) {
        try {
            getPackageManager().getPackageInfo(str, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    //----------Connectivity Broadcast Receiver --------//
    private BroadcastReceiver connectStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if(activeNetworkInfo != null && activeNetworkInfo.isConnected()){
                mBinding.noSignalIcon.setVisibility(View.GONE);
                if(player == null){
                    initVideoViewModel();
                }else{
                    videoViewModel.preparePlayer();
                }

            }else{ //Internet Connection Stopped
                mBinding.noSignalIcon.setVisibility(View.VISIBLE);
                Toast.makeText(context, "هناك مشكلة في الاتصال بالانترنت !", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case PERMISSIONS_REQUEST_STORAGE:

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, getString(R.string.error_permission_denied), Toast.LENGTH_SHORT).show();
                }
                else{
                    startDownloadingEpisode();
                }
                break;
        }
    }
}
