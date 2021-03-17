package com.app.cartoons.anime.newappcartoons.activities;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.app.cartoons.anime.newappcartoons.Database.SQLiteDatabaseManager;
import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.app.Config;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityEpisodesBinding;
import com.app.cartoons.anime.newappcartoons.model.Cartoon;
import com.app.cartoons.anime.newappcartoons.model.Episode;
import com.app.cartoons.anime.newappcartoons.model.Playlist;
import com.app.cartoons.anime.newappcartoons.network.ApiClient;
import com.app.cartoons.anime.newappcartoons.network.ApiService;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
//import com.htetznaing.xgetter.Model.XModel;
//import com.htetznaing.xgetter.XGetter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.app.cartoons.anime.newappcartoons.app.Config.admob;

public class EpisodesActivity extends AppCompatActivity {

    ActivityEpisodesBinding mBinding;

    List<Episode> episodeList = new ArrayList<>();

    Cartoon cartoon;
    Playlist playlist;

    private final int VIDEO_REQUEST_CODE = 1;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    int pageNumber = 1;

    SQLiteDatabaseManager sqLiteDatabaseManager;
    private android.widget.SearchView searchView;

    public boolean searchCase = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_episodes);

        initDatabase();
        getIntentData();
        createBannerAd();
        initToolbar();
        initProgressBar();
        initRecyclerview();
        initRetrofit();
        getEpisodes();
    }

    private void initDatabase(){
        sqLiteDatabaseManager = new SQLiteDatabaseManager(EpisodesActivity.this);
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(this)
                .load(playlist.getThumb())
                .into(mBinding.toolbarImage);
        mBinding.collapsingToolbar.setTitle(playlist.getTitle());
    }

    private void initProgressBar(){
        Circle circle = new Circle();
        mBinding.progress.setIndeterminateDrawable(circle);
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void getIntentData(){
        cartoon = (Cartoon) getIntent().getSerializableExtra("cartoon");
        playlist = (Playlist) getIntent().getSerializableExtra("playlist");
    }

    private void initRecyclerview(){
        mBinding.setEpisodeList(episodeList);
    }

    public void getEpisodes(){
        disposable.add(
                apiService
                        .getEpisodes(playlist.getId(), pageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Episode>>() {
                            @Override
                            public void onSuccess(List<Episode> retrievedEpisodeList) {
                                int oldSize = episodeList.size();
                                episodeList.addAll(retrievedEpisodeList);
                                Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyItemRangeInserted(oldSize, episodeList.size());
                                mBinding.progressBarLayout.setVisibility(View.GONE);

                                pageNumber++;
                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
                            }
                        })
        );
    }

    public void progressBar(View view) {
        Toast.makeText(this, "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
    }

    public String getThumb(){
        return playlist.getThumb();
    }
    public String getPlaylistTitle(){
        return playlist.getTitle();
    }
    public String getCartoonTitle(){
        return cartoon.getTitle();
    }

    public void startVideoActivity(int position, Episode episode, String episodeTitle, String thumb,
                                   String playlistTitle, String cartoonTitle){

        createInterstitialAd1(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
    }

    private void createInterstitialAd1(final int position, final Episode episode, final String episodeTitle, final String thumb,
                                       final String playlistTitle, final String cartoonTitle){

        if(admob != null && admob.getInterstitial() != null){
            mBinding.progressBarLayout.setVisibility(View.VISIBLE);

            final InterstitialAd mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(admob.getInterstitial());
            mInterstitialAd.loadAd(new AdRequest.Builder().build());

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    mInterstitialAd.show();
                    mBinding.progressBarLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                    mBinding.progressBarLayout.setVisibility(View.GONE);
                    checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
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
                    mBinding.progressBarLayout.setVisibility(View.GONE);
                    checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
                }
            });
        }else{

            checkServers(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
        }
    }

    private void checkServers(final int position, final Episode episode, final String episodeTitle, final String thumb,
                              final String playlistTitle, final String cartoonTitle){
        if(episode.getVideo1().isEmpty() &&
                episode.getVideo2().isEmpty() &&
                episode.getVideo3().isEmpty() &&
                episode.getVideo4().isEmpty()){

//            startVideoPlayer(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
            Config.optionsDialog(this, episode.getVideo(), episode, playlistTitle, cartoonTitle);
        }else{
            openServersActivity(position, episode, episodeTitle, thumb, playlistTitle, cartoonTitle);
        }
    }

    private void startVideoPlayer(int position, Episode episode, String episodeTitle, String thumb, String playlistTitle, String cartoonTitle) {
        Intent intent = new Intent(EpisodesActivity.this, ExoplayerActivity.class);
        intent.putExtra("episode", episode);
        intent.putExtra("title", episodeTitle);
        intent.putExtra("thumb", thumb);
        intent.putExtra("playlistTitle", playlistTitle);
        intent.putExtra("cartoonTitle", cartoonTitle);
        intent.putExtra("videoUrl", episode.getVideo());
        startVideoPlayerActivity(position, episode, intent);

        //Check if needs xgetter
        /*if(episode.getxGetter() == 1){ //Needs extractions

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

                        mBinding.progressBarLayout.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(EpisodesActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("اختار جودة الحلقة");
                        builder.setItems(qualities, (dialog, which) -> {

                            intent.putExtra("videoUrl", urls[which]);
                            startVideoPlayerActivity(position, episode, intent);
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
                        startVideoPlayerActivity(position, episode, intent);
                    }
                }

                @Override
                public void onError() {
                    //Error
                    Toast.makeText(EpisodesActivity.this, "حصل خطأ ما الرجاء المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                }
            });

            xGetter.find(episode.getVideo());

        }else{
            intent.putExtra("videoUrl", episode.getVideo());
            startVideoPlayerActivity(position, episode, intent);
        }*/
    }

    private void startVideoPlayerActivity(int position, Episode episode, Intent intent) {
        startActivityForResult(intent, VIDEO_REQUEST_CODE);
        mBinding.progressBarLayout.setVisibility(View.GONE);

        if (!sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
            sqLiteDatabaseManager.insertSeenEpisode(episode.getId());
            mBinding.episodessRecyclerview.getAdapter().notifyItemChanged(position);
        }
    }

    private void openServersActivity(int position, Episode episode, String episodeTitle, String thumb, String playlistTitle, String cartoonTitle) {
        Intent intent = new Intent(EpisodesActivity.this, ServersActivity.class);
        intent.putExtra("episode", episode);
        intent.putExtra("title", episodeTitle);
        intent.putExtra("thumb", thumb);
        intent.putExtra("playlistTitle", playlistTitle);
        intent.putExtra("cartoonTitle", cartoonTitle);
        startVideoPlayerActivity(position, episode, intent);
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

    private void createBannerAd(){
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.BANNER);

        if(admob != null && admob.getBanner() != null){
            mAdView.setAdUnitId(admob.getBanner());

            if(mBinding.addContainer != null){
                ((LinearLayout)mBinding.addContainer).addView(mAdView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(
                        Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();

        if (focusedView != null) {
            try{
                assert inputManager != null;
                inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }catch(AssertionError e){
                e.printStackTrace();
            }
        }
    }

    private void searchEpisode(String query){

        disposable.add(
                apiService
                        .searchEpisodes(query, playlist.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Episode>>() {
                            @Override
                            public void onSuccess(List<Episode> retrievedEpisodeList) {

                                pageNumber = 0;
                                episodeList.clear();
                                episodeList.addAll(retrievedEpisodeList);
                                Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyDataSetChanged();

                            }

                            @Override
                            public void onError(Throwable e) {
                                mBinding.progressBarLayout.setVisibility(View.GONE);
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );

    }

    //-----------Override Methods---------------//

    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.favorite_menu, menu);

        //Favorite Function
        this.menu = menu;
        if(sqLiteDatabaseManager.isCartoonFavorite(cartoon.getId())){
            menu.findItem(R.id.menu_empty_star).setVisible(false);
            menu.findItem(R.id.menu_filled_star).setVisible(true);
        }

        //Search Function
        MenuItem search_item = menu.findItem(R.id.menusearch);
        searchView = (android.widget.SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s)){
                    searchCase = true;
                    searchEpisode(s);
                }else{
                    searchCase = false;
                    episodeList.clear();
                    pageNumber = 1;
                    Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyDataSetChanged();
                    getEpisodes();
                }
                hideSoftKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s)){
                    searchCase = true;
                    searchEpisode(s);
                }else{
                    searchCase = false;
                    episodeList.clear();
                    pageNumber = 1;
                    Objects.requireNonNull(mBinding.episodessRecyclerview.getAdapter()).notifyDataSetChanged();
                    getEpisodes();
                }
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;

            case R.id.share:
                Config.shareApp(EpisodesActivity.this);
                break;

            case R.id.menu_empty_star:{
                menu.findItem(R.id.menu_empty_star).setVisible(false);
                menu.findItem(R.id.menu_filled_star).setVisible(true);

                sqLiteDatabaseManager.insertFavoriteCartoon(cartoon);
                setResult(RESULT_OK);
            }
            break;

            case R.id.menu_filled_star:{
                menu.findItem(R.id.menu_filled_star).setVisible(false);
                menu.findItem(R.id.menu_empty_star).setVisible(true);

                sqLiteDatabaseManager.deleteFavoriteCartoon(cartoon.getId());
                setResult(RESULT_OK);
            }
            break;

            default: //No Action
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        /*if (requestCode == VIDEO_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                //show ad

            }
        }*/

        createInterstitialAd2();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if(searchView != null && !searchView.isIconified()){
            searchView.setIconified(true);
            hideSoftKeyboard();
        }
        else{
            EpisodesActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
