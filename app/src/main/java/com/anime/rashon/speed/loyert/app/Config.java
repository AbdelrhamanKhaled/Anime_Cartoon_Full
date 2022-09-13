package com.anime.rashon.speed.loyert.app;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.activities.InformationActivity;
import com.anime.rashon.speed.loyert.model.Admob;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.util.LogicUtils;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.EnqueueAction;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class Config {

    private static FetchListener fetchListener;
    private static ProgressDialog progressDialog ;
//    public static final String BASE_URL = "http://dxd-player.com/animelivev/API/";
    static BroadcastReceiver receiver ;
    public static final String BASE_URL = "https://dxd-downloader.com/Abdulrahman/API/";

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

    private static void ShowDialog(Context context) {
        //setting up progress dialog
        progressDialog = new ProgressDialog(context);
        try {
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }catch (Exception exception) {

        }

    }

    public static void optionsDialog(Activity activity, String url, Episode episode,
                                     String playlistTitle, String cartoonTitle)
    {

        if (episode.isError()) {
            Toast.makeText(activity.getApplicationContext() , "حدث خطأ ما يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        if (url.startsWith("https://vudeo.net/") || url.startsWith("https://vudeo.io/") || url.startsWith("https://m3.vudeo.io/")) {
            Toast.makeText(activity.getApplicationContext() , "حدث خطأ ما يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        if (episode.getVideo().startsWith("https://vudeo.net/") || episode.getVideo().startsWith("https://vudeo.io/") || episode.getVideo().startsWith("https://m3.vudeo.io/")) {
            Toast.makeText(activity.getApplicationContext() , "حدث خطأ ما يرجي تجربة سيرفر أخر" , Toast.LENGTH_LONG).show();
            return;
        }

        String[] optionsArr = new String[]{"مشاهدة الحلقة", "تحميل الحلقة"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("");
        builder.setItems(optionsArr, (dialog, which) -> {
            if(which == 0){
                openExoPlayerApp(activity, url, episode);
            }
            else if(which == 1){
                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}
                            , 1);

                }else {
                    startDownloadingEpisode(activity, url, episode, playlistTitle, cartoonTitle);
                }
            }
        });

        builder.show();

    }



    public static void startDownloadingEpisode(Activity activity, String url, Episode episode,
                                               String playlistTitle, String cartoonTitle) {
        ShowDialog(activity);
          if (!isPackageInstalled ( "com.mojfhr.plasjre" , activity.getPackageManager())) {
              showDownloadDownloaderAppDialog(activity);
              progressDialog.dismiss();
              return;
           }
          // check if can download with fetch :
           Fetch fetch = Fetch.Impl.getInstance(new FetchConfiguration.Builder(activity).build());
           String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" +
                   episode.getTitle() + UUID.randomUUID().toString() + ".mp4" ;
           fetchListener = new FetchListener() {
               @Override
               public void onAdded(@NonNull Download download) {

               }

               @Override
               public void onQueued(@NonNull Download download, boolean b) {

               }

               @Override
               public void onWaitingNetwork(@NonNull Download download) {

               }

               @Override
               public void onCompleted(@NonNull Download download) {

               }

               @Override
               public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
                           if (error.getValue() == Error.REQUEST_NOT_SUCCESSFUL.getValue()) {
                               Log.i("ab_do" , "anime REQUEST_NOT_SUCCESSFUL");
                               endFetch(download, fetch);
                               startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
                           }
               }

               @Override
               public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

               }

               @Override
               public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {
                   Log.i("ab_do" , "start with anime");
                   // the download can be downloaded with fetch
                   endFetch(download, fetch);
                   openDownloaderApp(activity, playlistTitle , url, cartoonTitle, episode);
               }

               @Override
               public void onProgress(@NonNull Download download, long l, long l1) {

               }

               @Override
               public void onPaused(@NonNull Download download) {

               }

               @Override
               public void onResumed(@NonNull Download download) {

               }

               @Override
               public void onCancelled(@NonNull Download download) {

               }

               @Override
               public void onRemoved(@NonNull Download download) {

               }

               @Override
               public void onDeleted(@NonNull Download download) {

               }
           };
           fetch.addListener(fetchListener);
           Request request = new Request(url, path);
           request.setEnqueueAction(EnqueueAction.INCREMENT_FILE_NAME);
           fetch.enqueue(request,updatedRequest -> {
               //Request was successfully enqueued for download.
               Log.i("ab_do" , "enqueued ");

           }, error -> {
               Log.i("ab_do" , "error on enqueee " + error.toString());
               endFetch(null , fetch);
               try {
                   File file = new File(request.getFile());
                   file.delete();
               }
               catch (Exception exception) {}
               startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
           });
//          String googleUserContentURl = "https://lh3.googleusercontent.com";
//        if (url.contains(googleUserContentURl)) {
//            // this url can`t be handled with downloader app
//            startDownloadViaDownloadManager(activity, url, episode, playlistTitle, cartoonTitle);
//        }
//        else
//            openDownloaderApp(activity, url, cartoonTitle, episode);

    }

    private static void endFetch(Download download, Fetch fetch) {
        fetch.removeListener(fetchListener);
        if (download != null) {
            fetch.remove(download.getId());
            fetch.remove(download.getRequest().getId());
            File file = new File(download.getRequest().getFile());
            file.delete();
        }
        fetch.cancelAll();
        fetch.removeAll();
        if (progressDialog!=null)
        progressDialog.dismiss();
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static void startDownloadViaDownloadManager(Activity activity, String url, Episode episode, String playlistTitle, String cartoonTitle) {
//        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
//        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", url);
//        clipboard.setPrimaryClip(clip);
        Uri episodeUri = Uri.parse(url);

        DownloadManager.Request req=new DownloadManager.Request(episodeUri);

        String full_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + "Downloads" + "/" + cartoonTitle + " " + episode.getTitle() + ".mp4";
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(true)
                .setAllowedOverMetered(true)
                .setTitle(cartoonTitle + " " + episode.getTitle())
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS , "Downloads" + "/" + cartoonTitle + " " + episode.getTitle() + ".mp4" )
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        ;

        /*.setDestinationInExternalPublicDir(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/AnimeCartoons/" + cartoonTitle + "/" + playlistTitle,
                episode.getTitle() + ".mp4")*/

        DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(req);
        SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(activity);
        String mainPath = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + File.separator ;
        sqliteManager.insertDownload(cartoonTitle + " - " + playlistTitle + " - " + episode.getTitle()
                , full_path);
        Toast.makeText(activity, "يتم تحميل الحلقة الان...", Toast.LENGTH_SHORT).show();
    }

    private static void openDownloaderApp(Activity activity, String playlistTitle , String url, String cartoonTitle, Episode episode) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("download.anime.action");
        intent.putExtra("anime_url", url);
        intent.putExtra("anime_file_name", activity.getString(R.string.format_mp4, cartoonTitle + " " + episode.getTitle()));
        intent.putExtra("animeName", cartoonTitle + " - " + playlistTitle + " - " + episode.getTitle());
        try {
            activity.startActivityForResult(intent, 101);
        } catch (ActivityNotFoundException e) {
            showDownloadDownloaderAppDialog(activity);
        }
        IntentFilter intentFilter = new IntentFilter("anime.saveData");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("ab_do" ,"FromDownloader ");
                String path = intent.getStringExtra("animePath");
                String name = intent.getStringExtra("animeName");
                SQLiteDatabaseManager sqliteManager = new SQLiteDatabaseManager(context);
                sqliteManager.insertDownload(name, path);
                activity.unregisterReceiver(receiver);
            }
            // register the receiver
        };
        activity.registerReceiver(receiver, intentFilter);
    }

    private static void showDownloadDownloaderAppDialog(Activity activity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme);
        builder.setMessage("يرجى تثبيت تطبيق تحميل الفيديو (Quick Downloader) لاكمال تحميل الحلقة");
        builder.setCancelable(true);
        builder.setPositiveButton("حسنا", (dialog, which) -> openDownloaderAppOnPlayStore(activity));
        builder.setNegativeButton("الغاء", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private static void openDownloaderAppOnPlayStore(Activity activity) {
        final String appPackageName = "com.mojfhr.plasjre";
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://loyert.page.link/FAST-DOWNLOADER" )));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void openExoPlayerApp(Activity activity, String url, Episode episode){
        ShowDialog(activity);
        PackageManager packageManager = activity.getPackageManager();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("quick.launch.me");
        intent.putExtra("url", url);
        if (intent.resolveActivity(packageManager) != null) {
             // insert seen episode and increment watched cartoons
            LoginUtil loginUtil = new LoginUtil(activity.getApplicationContext());
            if (loginUtil.userIsLoggedIN()) {
                insertSeenEpisode(intent , activity, episode, loginUtil);
            }
            else {
                dismissDialog(activity);
                startExoPlayer(activity, intent);
            }
        }


        }

    private static void insertSeenEpisode(Intent intent , Activity activity, Episode episode, LoginUtil loginUtil) {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(activity.getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .insertSeenEpisode(loginUtil.getCurrentUser().getId(), episode.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                     UserOptions.getUserOptions().getSeenEpisodesIds().add(episode.getId());
                                     incrementWatchedEpisodes(intent , activity, episode, loginUtil);
                                }
                                else {
                                    dismissDialog(activity);
                                    Toast.makeText(activity, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissDialog(activity);
                                Toast.makeText(activity, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private static void incrementWatchedEpisodes(Intent intent, Activity activity, Episode episode, LoginUtil loginUtil) {
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(activity.getApplicationContext()).create(ApiService.class);
        disposable.add(
                apiService
                        .incrementWatchedEpisodes(loginUtil.getCurrentUser().getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    dismissDialog(activity);
                                    startExoPlayer(activity , intent);
                                }
                                else {
                                    dismissDialog(activity);
                                    Toast.makeText(activity, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                dismissDialog(activity);
                                Toast.makeText(activity, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private static void dismissDialog(Activity activity) {
        try {
            progressDialog.dismiss();
        }catch (Exception exception) {

        }
    }

    private static void startExoPlayer(Activity activity, Intent intent) {
        try {
            activity.startActivity(intent);
        }

        catch (ActivityNotFoundException e) {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.AlertDialogTheme);

            builder.setMessage("يرجى تثبيت تطبيق المشغل السريع (Quick Player) لتشغيل الفيديو");
            builder.setCancelable(true);
            builder.setPositiveButton("حسنا", (dialog, which) -> openExoPlayerOnPlayStore(activity));

            builder.setNegativeButton("الغاء", (dialog, which) -> dialog.dismiss());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    private static void openExoPlayerOnPlayStore(Context activity){
        final String appPackageName = "com.mdax.player.liyhfkpk";
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://loyert.page.link/FAST-PLAYER" )));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void loadNativeAd(Context context, TemplateView templateView){
        /*AdLoader.Builder builder = new AdLoader.Builder(context, admob.getNativeAd());
        builder.forUnifiedNativeAd(nativeAd::setNativeAd);

        AdLoader adLoader = builder.build();
        AdRequest adRequest = new AdRequest.Builder().build();
        adLoader.loadAd(adRequest);*/
        if (admob!=null && admob.getNativeAd()!=null) {
            AdLoader adLoader = new AdLoader.Builder(context, admob.getNativeAd())
                    .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                        @Override
                        public void onNativeAdLoaded(@NotNull NativeAd nativeAd) {
                            templateView.setNativeAd(nativeAd);
                        }
                    })
                    .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }

    }

    public static final int Nav_LatestEpisode = 0;


    public static final int ALL = 0;
    public static final int ACTION = 1;
    public static final int GIRLSANIME = 2;
    public static final int ADVENTURE = 3;
    public static final int OTHER = 4;
    public static final int TRANSLATED_ANIME = 5;
    public static final int CHILD_ANIME = 6;
    public static final int SPORT_ANIME = 7;
    public static final int NEW_ANIME = 8;
    public static final int FILMS = 9;
    public static final String CHANNEL_ID = "animeCartoonNotification";

    public static Admob admob;

}
