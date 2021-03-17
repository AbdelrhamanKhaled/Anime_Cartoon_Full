package com.app.cartoons.anime.newappcartoons.app;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.app.cartoons.anime.newappcartoons.Database.SQLiteDatabaseManager;
import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.activities.ExoplayerActivity;
import com.app.cartoons.anime.newappcartoons.model.Admob;
import com.app.cartoons.anime.newappcartoons.model.Episode;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.UUID;

public class Config {

    public static final String BASE_URL = "http://tokviews.com/newanimecartoons/API/";
//    public static final String BASE_URL = "http://mohannadapp.com/worldcartoons/API/";
//    public static final String BASE_URL = "http://192.168.1.10:80/AnimeCartoons/API/";

    public static int numOfItemsBetweenAds = 30;

    public static void shareApp(Context context){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "حمل التطبيق من هنا" +
                "\n\n" +
                "https://play.google.com/store/apps/details?id=" + context.getPackageName();
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "مشاركة من خلال"));
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void optionsDialog(Activity activity, String url, Episode episode,
                                     String playlistTitle, String cartoonTitle){

        String[] optionsArr = new String[]{"مشاهدة الحلقة", "تحميل الحلقة"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setItems(optionsArr, (dialog, which) -> {
            if(which == 0){
                openExoPlayerApp(activity, url);
            }else if(which == 1){
                startDownloadingEpisode(activity, url, episode, playlistTitle, cartoonTitle);
            }
        });

        builder.show();

    }

    private static void startDownloadingEpisode(Activity activity, String url, Episode episode,
                                         String playlistTitle, String cartoonTitle){
        Uri episodeUri = Uri.parse(url);

        DownloadManager.Request req=new DownloadManager.Request(episodeUri);

        String episodeUniqueName = UUID.randomUUID().toString();

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)
                .setTitle(episode.getTitle())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                        episodeUniqueName + ".mp4")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);;

        /*.setDestinationInExternalPublicDir(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/AnimeCartoons/" + cartoonTitle + "/" + playlistTitle,
                episode.getTitle() + ".mp4")*/

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(req);

        Toast.makeText(activity, "يتم تحميل الحلقة الان...", Toast.LENGTH_SHORT).show();

        SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(activity);
        sqliteManager.insertDownload(cartoonTitle + " - " + playlistTitle + " - " + episode.getTitle()
                ,  episodeUniqueName + ".mp4");
    }

    public static void openExoPlayerApp(Context activity, String url){
        PackageManager packageManager = activity.getPackageManager();

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setAction("exoplayer.launch.me");
        intent.putExtra("url", url);

        if (intent.resolveActivity(packageManager) != null) {
            activity.startActivity(intent);
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme);

            builder.setMessage("لمشاهدة الحلقة يجب تثبيت تطبيق MQ Player");
            builder.setCancelable(true);
            builder.setPositiveButton("تحميل", (dialog, which) -> openExoPlayerOnPlayStore(activity));

            builder.setNegativeButton("لاحقا", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private static void openExoPlayerOnPlayStore(Context activity){
        final String appPackageName = "com.mq.player.mqplayer";
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void loadNativeAd(Context context, TemplateView nativeAd){
        AdLoader.Builder builder = new AdLoader.Builder(context, admob.getNativeAd());
        builder.forUnifiedNativeAd(nativeAd::setNativeAd);

        AdLoader adLoader = builder.build();
        AdRequest adRequest = new AdRequest.Builder().build();
        adLoader.loadAd(adRequest);
    }


    public static final int ALL = 0;
    public static final int ACTION = 1;
    public static final int GIRLSANIME = 2;
    public static final int ADVENTURE = 3;
    public static final int OTHER = 4;
    public static final int TRANSLATED_ANIME = 5;
    public static final int CHILD_ANIME = 6;
    public static final int SPORT_ANIME = 7;
    public static final int NEW_ANIME = 8;
    public static final String CHANNEL_ID = "animeCartoonNotification";

    public static Admob admob;

}
