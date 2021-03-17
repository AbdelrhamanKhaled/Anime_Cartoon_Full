package com.app.cartoons.anime.newappcartoons.activities;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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
import com.app.cartoons.anime.newappcartoons.databinding.ActivityPlayListsBinding;
import com.app.cartoons.anime.newappcartoons.model.Cartoon;
import com.app.cartoons.anime.newappcartoons.model.Playlist;
import com.app.cartoons.anime.newappcartoons.network.ApiClient;
import com.app.cartoons.anime.newappcartoons.network.ApiService;
import com.bumptech.glide.Glide;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.app.cartoons.anime.newappcartoons.app.Config.admob;

public class PlayListsActivity extends AppCompatActivity {

    private final String TAG = PlayListsActivity.class.getSimpleName();

    ActivityPlayListsBinding mBinding;

    List<Playlist> playlistList = new ArrayList<>();
    List<Playlist> playlistListBackup = new ArrayList<>();

    Cartoon cartoon;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;
    SQLiteDatabaseManager sqliteManager;
    private android.widget.SearchView searchView;
    private final int openEpisodesRequestCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_play_lists);

        createBannerAd();
        getIntentData();
        initToolbar();
        initSqliteDatabase();
        initProgressBar();
        initRecyclerview();
        initRetrofit();
        getPlaylists();
    }

    private void initSqliteDatabase(){
        sqliteManager = new SQLiteDatabaseManager(PlayListsActivity.this);
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Glide.with(this)
                .load(cartoon.getThumb())
                .into(mBinding.toolbarImage);
        mBinding.collapsingToolbar.setTitle(cartoon.getTitle());
    }

    private void initProgressBar(){
        Circle circle = new Circle();
        mBinding.progress.setIndeterminateDrawable(circle);
    }

    private void getIntentData(){
        cartoon = (Cartoon) getIntent().getSerializableExtra("cartoon");
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void initRecyclerview(){
        mBinding.setPlaylistList(playlistList);
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

    private void getPlaylists(){
        disposable.add(
                apiService
                        .getPlaylists(cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Playlist>>() {
                            @Override
                            public void onSuccess(List<Playlist> playlistList) {
                                PlayListsActivity.this.playlistList.addAll(playlistList);
                                PlayListsActivity.this.playlistListBackup.addAll(playlistList);
                                if(playlistList.size() == 1){
                                    Intent intent = new Intent(PlayListsActivity.this, EpisodesActivity.class);
                                    intent.putExtra("playlist", playlistList.get(0));
                                    intent.putExtra("cartoon", cartoon);
                                    PlayListsActivity.this.startActivity(intent);
                                    PlayListsActivity.this.finish();
                                }else{
                                    mBinding.playlistsRecyclerview.getAdapter().notifyDataSetChanged();
                                    mBinding.progressBarLayout.setVisibility(View.GONE);
                                }
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

    public String getCartoonTitle(){
        return cartoon.getTitle();
    }

    public void openEpisodesActivity(Playlist playlist) {
        hideSoftKeyboard();
        Intent intent = new Intent(this, EpisodesActivity.class);
        intent.putExtra("cartoon", cartoon);
        intent.putExtra("playlist", playlist);
        startActivityForResult(intent, openEpisodesRequestCode);
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

    private void searchPlaylist(String query){

        playlistList.clear();
        for(Playlist playlist : playlistListBackup){
            if(playlist.getTitle().toLowerCase().contains(query.toLowerCase()))
                playlistList.add(playlist);
        }

        Objects.requireNonNull(mBinding.playlistsRecyclerview.getAdapter()).notifyDataSetChanged();
    }

    //-----------Override Methods---------------//


    Menu menu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        this.menu = menu;
        if(sqliteManager.isCartoonFavorite(cartoon.getId())){
            menu.findItem(R.id.menu_empty_star).setVisible(false);
            menu.findItem(R.id.menu_filled_star).setVisible(true);
        }

        //Search Function
        MenuItem search_item = menu.findItem(R.id.menusearch);
        searchView = (android.widget.SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchPlaylist(s);
                hideSoftKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchPlaylist(s);
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
                Config.shareApp(PlayListsActivity.this);
                break;

            case R.id.menu_empty_star:{
                menu.findItem(R.id.menu_empty_star).setVisible(false);
                menu.findItem(R.id.menu_filled_star).setVisible(true);

                sqliteManager.insertFavoriteCartoon(cartoon);
            }
                break;

            case R.id.menu_filled_star:{
                menu.findItem(R.id.menu_filled_star).setVisible(false);
                menu.findItem(R.id.menu_empty_star).setVisible(true);

                sqliteManager.deleteFavoriteCartoon(cartoon.getId());
            }
                break;


            default:
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == openEpisodesRequestCode){
                if(sqliteManager.isCartoonFavorite(cartoon.getId())){
                    menu.findItem(R.id.menu_empty_star).setVisible(false);
                    menu.findItem(R.id.menu_filled_star).setVisible(true);
                }else{
                    menu.findItem(R.id.menu_filled_star).setVisible(false);
                    menu.findItem(R.id.menu_empty_star).setVisible(true);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
