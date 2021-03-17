package com.app.cartoons.anime.newappcartoons.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import androidx.annotation.NonNull;

import com.app.cartoons.anime.newappcartoons.model.Redirect;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.app.Config;
import com.app.cartoons.anime.newappcartoons.databinding.ActivityMainBinding;
import com.app.cartoons.anime.newappcartoons.fragments.CartoonFragment;
import com.app.cartoons.anime.newappcartoons.model.Admob;
import com.app.cartoons.anime.newappcartoons.network.ApiClient;
import com.app.cartoons.anime.newappcartoons.network.ApiService;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static com.app.cartoons.anime.newappcartoons.app.Config.ALL;
import static com.app.cartoons.anime.newappcartoons.app.Config.admob;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding mBinding;

    private android.widget.SearchView searchView;

    private InterstitialAd mInterstitialAd;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    public static boolean searchCase = false;

    private int selectedType = 0;

    RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initToolbar();
        initNavDrawer();
        initRetrofit();
        getRedirect(savedInstanceState);

        //Print onesignal token
        /*OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d("debug", "User:" + userId);
            if (registrationId != null)
                Log.d("debug", "registrationId:" + registrationId);

        });*/
    }

    public void getAdmobData(){
        disposable.add(
                apiService
                        .getAdmob()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Admob>() {
                            @Override
                            public void onSuccess(Admob admob) {
                                if(Config.admob == null){
                                    Config.admob = admob;
                                    Config.admob.setNativeAd("ca-app-pub-3940256099942544/2247696110");
                                    MobileAds.initialize(MainActivity.this, admob.getApp_id());
                                    createBannerAd();
                                    loadRewardedAd();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {

                            }
                        })
        );
    }

    private void initRetrofit(){
        apiService = ApiClient.getClient(this).create(ApiService.class);
    }

    private void initToolbar(){
        setSupportActionBar(mBinding.includedToolbar.toolbar);
    }

    private void initNavDrawer(){
        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mBinding.drawerLayout,
                mBinding.includedToolbar.toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                /*mBinding.mainContent.setTranslationX(slideOffset * drawerView.getWidth() * -1);
                mBinding.drawerLayout.bringChildToFront(drawerView);
                mBinding.drawerLayout.requestLayout();*/
            }
        };

        //        mDrawerLayout.setScrimColor(getResources().getColor(R.color.drawerBackgroundTint));
        mBinding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    public void getRedirect(Bundle savedInstanceState){
        disposable.add(
                apiService
                        .getRedirect()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Redirect>() {
                            @Override
                            public void onSuccess(Redirect redirect) {
                                if(redirect.getIs_active().equals("yes")){
                                    Intent intent = new Intent(MainActivity.this, RedirectActivity.class);
                                    intent.putExtra("redirect", redirect);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    getMessage();
                                    initNavDrawer();

                                    if(savedInstanceState == null){
                                        inflateCartoonsFragment();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getMessage();
                                initNavDrawer();

                                if(savedInstanceState == null){
                                    inflateCartoonsFragment();
                                }
                            }
                        })
        );
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

    private void createInterstitialAd(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(admob.getInterstitial());
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

    private void showDrawer(){
        mBinding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer(){
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void inflateCartoonsFragment(){
        getSupportActionBar().setTitle(getString(R.string.app_name));
        CartoonFragment fragment = new CartoonFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_fragment, fragment, getString(R.string.cartoon_fragment));
        transaction.commit();
    }

    public void progressBar(View view) {
        Toast.makeText(this, "جاري التحميل من فضلك انتظر...", Toast.LENGTH_SHORT).show();
    }

    public void resetTitleAndSelection(){
        getSupportActionBar().setTitle(getString(R.string.app_name));
        mBinding.navView.getMenu().getItem(0).setChecked(true);
    }

    public void getNewCartoons(){
        CartoonFragment cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));

        if(selectedType == 0){
            cartoonFragment.getAllCartoons();
        }else{
            cartoonFragment.getCartoonsByType(selectedType);
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

    private void loadRewardedAd(){

        if(admob.getRewardedVideo() != null){

            rewardedAd = new RewardedAd(this, admob.getRewardedVideo());

            RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
                @Override
                public void onRewardedAdLoaded() {
                    // Ad successfully loaded.
                }

                @Override
                public void onRewardedAdFailedToLoad(int errorCode) {
                    // Ad failed to load.
                }
            };
            rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        }

    }

    private void showRewardedAd(){
        if(rewardedAd != null && rewardedAd.isLoaded()){
            RewardedAdCallback adCallback = new RewardedAdCallback() {
                @Override
                public void onRewardedAdOpened() {
                    // Ad opened.
                }

                @Override
                public void onRewardedAdClosed() {
                    // Ad closed.
                    loadRewardedAd();
                }

                @Override
                public void onUserEarnedReward(@NonNull RewardItem reward) {
                    // User earned reward.
                    Toast.makeText(MainActivity.this, "شكرا علي دعمك لنا", Toast.LENGTH_SHORT).show();
                    loadRewardedAd();
                }

                @Override
                public void onRewardedAdFailedToShow(int errorCode) {
                    // Ad failed to display.
                }
            };
            rewardedAd.show(this, adCallback);

        }
    }

    public void getMessage(){
        disposable.add(
                apiService
                        .getMessage()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Redirect>() {
                            @Override
                            public void onSuccess(Redirect redirect) {
                                if(redirect.getIs_active().equals("yes")){

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    builder.setMessage(redirect.getMessage());
                                    builder.setCancelable(true);
                                    builder.setPositiveButton("الذهاب للتطبيق", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if(redirect.getRedirect_type().equals("package_name")){

                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + redirect.getPackage_name())));

                                            }else if(redirect.getRedirect_type().equals("url")){

                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(redirect.getUrl()));
                                                startActivity(browserIntent);

                                            }
                                        }
                                    });

                                    builder.setNegativeButton("لاحقا", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.show();

                                }else{

                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                            }
                        })
        );
    }

    //-----------Override Methods----------------//


    @Override
    protected void onResume() {
        super.onResume();
        getAdmobData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem search_item = menu.findItem(R.id.menusearch);

        searchView = (android.widget.SearchView) search_item.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if(!TextUtils.isEmpty(s)){
                    CartoonFragment cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));
                    cartoonFragment.filterAdapter(s);
                }
                hideSoftKeyboard();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                CartoonFragment cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));
                if(!TextUtils.isEmpty(s)){
                    searchCase = true;
                    cartoonFragment.filterAdapter(s);
                }else{
                    searchCase = false;
                    cartoonFragment.sortByCategory(selectedType);
                }
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.share:
                Config.shareApp(MainActivity.this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            hideDrawer();
        }
        else if(searchView != null && !searchView.isIconified()){
            searchView.setIconified(true);
            hideSoftKeyboard();
        }
        else if(selectedType != ALL){
            selectedType = ALL;
            mBinding.navView.getMenu().getItem(0).setChecked(true);
            CartoonFragment cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));
            assert cartoonFragment != null;
            cartoonFragment.sortByCategory(ALL);
            Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.app_name));
        }
        else{
            /*Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/

            //Check If rated
            /*SharedPreferences prefs = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);
            boolean isRated = prefs.getBoolean("isRated", false);
            if (isRated) {
                MainActivity.this.finish();
            }else{
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MainActivity.this);

                builder.setMessage("قيمنا بخمس نجوم");
                builder.setCancelable(true);
                builder.setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE).edit();
                        editor.putBoolean("isRated", true);
                        editor.apply();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    }
                });

                builder.setNegativeButton("في المرة القادمة", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        MainActivity.this.finish();
                    }
                });

                android.app.AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }*/

            MainActivity.this.finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        CartoonFragment cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));

        switch (item.getItemId()){
            case R.id.main:
                selectedType = ALL;
                cartoonFragment.sortByCategory(ALL);
                getSupportActionBar().setTitle(getString(R.string.app_name));
                break;

            case R.id.action:
                selectedType = Config.ACTION;
                cartoonFragment.sortByCategory(Config.ACTION);
                getSupportActionBar().setTitle(getString(R.string.action));
                break;

            case R.id.adventure:
                selectedType = Config.ADVENTURE;
                cartoonFragment.sortByCategory(Config.ADVENTURE);
                getSupportActionBar().setTitle(getString(R.string.adventure));
                break;

            case R.id.girls_anime:
                selectedType = Config.GIRLSANIME;
                cartoonFragment.sortByCategory(Config.GIRLSANIME);
                getSupportActionBar().setTitle(getString(R.string.girls_anime));
                break;

            case R.id.translated_anime:
                selectedType = Config.TRANSLATED_ANIME;
                cartoonFragment.sortByCategory(Config.TRANSLATED_ANIME);
                getSupportActionBar().setTitle("انمي مترجم");
                break;

            case R.id.child_cartoon:
                selectedType = Config.CHILD_ANIME;
                cartoonFragment.sortByCategory(Config.CHILD_ANIME);
                getSupportActionBar().setTitle("كرتون اطفال");
                break;

            case R.id.sport_cartoon:
                selectedType = Config.SPORT_ANIME;
                cartoonFragment.sortByCategory(Config.SPORT_ANIME);
                getSupportActionBar().setTitle("كرتون رياضة");
                break;

            case R.id.new_cartoon:
                selectedType = Config.NEW_ANIME;
                cartoonFragment.sortByCategory(Config.NEW_ANIME);
                getSupportActionBar().setTitle("المضاف حديثا");
                break;

            case R.id.favorite_episodes:
                startActivity(new Intent(MainActivity.this, FavoriteEpisodeActivity.class));
                break;

            case R.id.favorite_cartoons:
                startActivity(new Intent(MainActivity.this, FavoriteCartoonsActivity.class));
                break;

            case R.id.downloads:
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
                break;

            case R.id.support:
                showRewardedAd();
                break;

            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                break;

            case R.id.contact_us:{
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "developerappsllc@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email"));
            }

                break;

            default: //No Action
        }

        getAdmobData();
        hideDrawer();
        return true;
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }
}
