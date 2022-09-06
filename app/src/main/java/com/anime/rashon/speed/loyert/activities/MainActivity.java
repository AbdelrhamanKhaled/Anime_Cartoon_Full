package com.anime.rashon.speed.loyert.activities;

import static com.anime.rashon.speed.loyert.app.Config.ALL;
import static com.anime.rashon.speed.loyert.app.Config.FILMS;
import static com.anime.rashon.speed.loyert.app.Config.admob;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.GoogleAuth;
import com.anime.rashon.speed.loyert.Utilites.dialogUtilities;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.ActivityMainBinding;
import com.anime.rashon.speed.loyert.fragments.CartoonFragment;
import com.anime.rashon.speed.loyert.fragments.LatestEpisodesFragment;
import com.anime.rashon.speed.loyert.model.Admob;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;
import com.anime.rashon.speed.loyert.model.Playlist;
import com.anime.rashon.speed.loyert.model.Redirect;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.anime.rashon.speed.loyert.network.EpisodeDate;
import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final String TAG = MainActivity.class.getSimpleName();

    ActivityMainBinding mBinding;

    LatestEpisodesFragment LatestEpisodesFragmentFragment ;

    private android.widget.SearchView searchView;

    private CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService;

    public static boolean searchCase = false;

    public static int selectedType = 0;

    public static MenuItem search_item ;
    FirebaseAuth auth ;
    RewardedAd rewardedAd;
    DatabaseReference FavouriteRef;
    ValueEventListener FavouriteListener ;
    ValueEventListener SeenEpisodeslistener;
    DatabaseReference SeenEpisodesRef ;
    SQLiteDatabaseManager sqLiteDatabaseManager ;
    CartoonFragment cartoonFragment ;
    boolean grid ;
    private  dialogUtilities dialogUtilities ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        init();
        initToolbar();
        initNavDrawer();
        initRetrofit();
        getRedirect(savedInstanceState);
        updateSeenEpisodes();
        updateFavouriteCartoon();
        //test();
        //Print onesignal token
        /*OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d("debug", "User:" + userId);
            if (registrationId != null)
                Log.d("debug", "registrationId:" + registrationId);

        });*/
    }

    private void test() {
        disposable.add(
                apiService
                        .episodeDates()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<EpisodeDate>>() {
                            @Override
                            public void onSuccess(List<EpisodeDate> episodeDateList) {
                                for (int i=0; i<episodeDateList.size(); i++) {
                                    Log.i("ab_dob" , episodeDateList.get(i).getName());
                                    Log.i("ab_dob" , episodeDateList.get(i).getImg());
                                    Log.i("ab_dob" , String.valueOf(episodeDateList.get(i).getDay()));
                                    Log.i("ab_dob" , "----------------------------------");
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                            }
                        })
        ) ;
    }

    private void init() {
        //        FirebaseDatabase.getInstance().getReference().removeValue();
        auth = FirebaseAuth.getInstance();
        checkIfTheUserLodged();
        sqLiteDatabaseManager = new SQLiteDatabaseManager(this);
        grid = true ;
        dialogUtilities = new dialogUtilities();
    }

    private void checkIfTheUserLodged() {
        FirebaseUser user = auth.getCurrentUser();
        View view = mBinding.navView.getHeaderView(0);
        TextView username, login;
        ImageView user_profile = view.findViewById(R.id.user_profile);
        username = view.findViewById(R.id.Username);
        login = view.findViewById(R.id.Login);
        username.setTextColor(getResources().getColor(R.color.black));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext() , LoginActivity.class));
            }
        });
        if (user!=null) {
            String name = user.getDisplayName();
            if (name == null || name.isEmpty())
                username.setText("لا يوجد اسم");
            else
                username.setText(name);
            login.setVisibility(View.GONE);
            Uri uri = user.getPhotoUrl();
            if (uri != null) {
                Glide.with(this)
                        .load(uri)
                        .centerCrop()
                        .placeholder(R.drawable.user_profile)
                        .error(R.drawable.user_profile)
                        .into(user_profile);
                ;

            }
        }
    }

    private void updateFavouriteCartoon() {
        sqLiteDatabaseManager.deleteAllFavouriteCartoons();
        if (auth.getCurrentUser()!=null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FavouriteRef = database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("FavouriteCartoon");
            FavouriteListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("ab_do" , "onDataChange " + dataSnapshot);
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        Cartoon cartoon = postSnapshot.getValue(Cartoon.class);
                        if (!sqLiteDatabaseManager.isCartoonFavorite(cartoon.getId()))
                        sqLiteDatabaseManager.insertFavoriteCartoon(cartoon);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            FavouriteRef.addValueEventListener(FavouriteListener);
        }
    }



    private void updateSeenEpisodes() {
        sqLiteDatabaseManager.deleteAllSeenEpisode();
        if (auth.getCurrentUser()!=null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            //database.getReference().removeValue();
            SeenEpisodesRef = database.getReference().child("Users").child(auth.getCurrentUser().getUid()).child("SeenEpisodes");
            SeenEpisodeslistener = new ValueEventListener(){
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("ab_do" , "onDataChange " + dataSnapshot);
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        // TODO: handle the post
                        int val = postSnapshot.getValue(int.class);
                        Log.d("ab_do" , "Val = " + val);
                        checkTheEpisodesInDatabase(val);
                    }
//                    if (LatestEpisodesFragmentFragment!=null &&LatestEpisodesFragmentFragment.getAdapter()!=null)
//                        LatestEpisodesFragmentFragment.getAdapter().notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                    // ...
                }
            };
            SeenEpisodesRef.addValueEventListener(SeenEpisodeslistener);
        }
    }


      private void checkTheEpisodesInDatabase(int id) {
            if (!sqLiteDatabaseManager.isEpisodeSeen(id))
            sqLiteDatabaseManager.insertSeenEpisode(id);
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
//                                    Config.showFacebookBannerAd(MainActivity.this, mBinding.addContainer);

                                    try {
                                        ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
                                        Bundle bundle = ai.metaData;
                                        String myApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d(TAG, "Name Found: " + myApiKey);
                                        ai.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", admob.getApp_id());//you can replace your key APPLICATION_ID here
                                        String ApiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                                        Log.d(TAG, "ReNamed Found: " + ApiKey);
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
                                    } catch (NullPointerException e) {
                                        Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
                                    }

                                    MobileAds.initialize(MainActivity.this, initializationStatus -> {

                                    });
                                    createBannerAd();
//                                    loadRewardedAd();

                                    //Test Mediation
//                                    MediationTestSuite.launch(MainActivity.this);

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
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        Menu nav_Menu = mBinding.navView.getMenu();
        nav_Menu.findItem(R.id.log_out).setVisible(auth.getCurrentUser() != null);
        mBinding.navView.setCheckedItem(R.id.latest_episodes);
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
//                                        inflateCartoonsFragment();
                                        inflateLatestEpisodesFragment();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                getMessage();
                                initNavDrawer();

                                if(savedInstanceState == null){
                                    inflateLatestEpisodesFragment();
//                                    inflateCartoonsFragment();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    private void showDrawer(){
        mBinding.drawerLayout.openDrawer(GravityCompat.START);
    }

    private void hideDrawer(){
        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void inflateLatestEpisodesFragment(){
        Objects.requireNonNull(getSupportActionBar()).setTitle("اخر الحلقات المضافة");
        if (getIntent().getSerializableExtra("list") !=null) {
            Log.i("ab_do2" , "list is ready");
            List<EpisodeWithInfo> episodes =  (List<EpisodeWithInfo>)getIntent().getSerializableExtra("list") ;
            Log.i("ab_do2" , "size " + episodes.size());
            LatestEpisodesFragmentFragment = new LatestEpisodesFragment(episodes);
        }
        else
        LatestEpisodesFragmentFragment = new LatestEpisodesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_fragment, LatestEpisodesFragmentFragment, getString(R.string.latest_episodes_fragment));
        transaction.commit();
    }

    private void replaceLatestEpisodesFragment(){
        Objects.requireNonNull(getSupportActionBar()).setTitle("أحدث الحلقات");
        LatestEpisodesFragmentFragment = new LatestEpisodesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, LatestEpisodesFragmentFragment, getString(R.string.latest_episodes_fragment));
        transaction.commit();
    }

    private void replaceCartoonsFragment(){
//        Objects.requireNonNull(getSupportActionBar()).setTitle("قائمة الانيميات");
        cartoonFragment = new CartoonFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_fragment, cartoonFragment, getString(R.string.cartoon_fragment));
        transaction.commit();
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
         cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.cartoon_fragment));

        if(selectedType == 0){
            cartoonFragment.getAllCartoons();
        }
        else{
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
                                    builder.setPositiveButton("حسنا", new DialogInterface.OnClickListener() {
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

                                    builder.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
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
        search_item = menu.findItem(R.id.menusearch);

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
                CartoonFragment cartoonFragment = (CartoonFragment) getSupportFragmentManager().findFragmentByTag(CartoonFragment.class.getSimpleName());
                if(!TextUtils.isEmpty(s)){
                    searchCase = true;
                    if (cartoonFragment!=null)
                    cartoonFragment.filterAdapter(s);
                }else{
                    searchCase = false;
                    if (cartoonFragment!=null)
                    cartoonFragment.sortByCategory(selectedType);
                }
                return true;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            Config.shareApp(MainActivity.this);
        }
        else if (item.getItemId() == R.id.grid_or_list) {
            if (grid) {
                item.setIcon(R.drawable.ic_baseline_grid_on_24);
                item.setTitle("شبكة");
            }
            else {
                item.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                item.setTitle("قائمة");
            }
            grid = !grid ;
            if (cartoonFragment!=null) {
                cartoonFragment.initRecyclerview(grid);
            }
            else if (LatestEpisodesFragmentFragment!=null) {
                LatestEpisodesFragmentFragment.initRecyclerview(grid);
            }
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

        int itemId = item.getItemId();//No Action
        mBinding.navView.setCheckedItem(itemId);
        if (itemId == R.id.latest_episodes) {
            replaceLatestEpisodesFragment();}
        else if (itemId == R.id.anime) {
            selectedType = ALL;
            if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle("المدبلج");
            replaceCartoonsFragment();
        }
        else if (itemId == R.id.films_t) {
            //dialogUtilities.ShowDialog(this);
            mBinding.progressBarLayout.setVisibility(View.VISIBLE);
            getFilms("الافلام المترجمه");
        }

        else if (itemId == R.id.films_d) {
            mBinding.progressBarLayout.setVisibility(View.VISIBLE);
            //dialogUtilities.ShowDialog(this);
            getFilms("الافلام المدبلجة");
        }
//        else if (itemId == R.id.action) {
//            selectedType = Config.ACTION;
//            getSupportActionBar().setTitle(getString(R.string.action));
//            replaceCartoonsFragment();
//        } else if (itemId == R.id.adventure) {
//            selectedType = Config.ADVENTURE;
//            getSupportActionBar().setTitle(getString(R.string.adventure));
//            replaceCartoonsFragment();
//        } else if (itemId == R.id.girls_anime) {
//            selectedType = Config.GIRLSANIME;
//            getSupportActionBar().setTitle(getString(R.string.girls_anime));
//            replaceCartoonsFragment();
        else if (itemId == R.id.translation_anime) {
            selectedType = Config.TRANSLATED_ANIME;
            if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle("المترجم");
            replaceCartoonsFragment();
        }
//        } else if (itemId == R.id.child_cartoon) {
//            selectedType = Config.CHILD_ANIME;
//            getSupportActionBar().setTitle("كرتون اطفال");
//            replaceCartoonsFragment();
//        } else if (itemId == R.id.sport_cartoon) {
//            selectedType = Config.SPORT_ANIME;
//            getSupportActionBar().setTitle("كرتون رياضة");
//            replaceCartoonsFragment();
       // }
        else if (itemId == R.id.new_cartoon) {
            selectedType = Config.NEW_ANIME;
            if (getSupportActionBar()!=null)
            getSupportActionBar().setTitle("المستمر");
            replaceCartoonsFragment();
        }
//        else if (itemId == R.id.favorite_episodes) {
//            startActivity(new Intent(MainActivity.this, FavoriteEpisodeActivity.class));
//        } else if (itemId == R.id.favorite_cartoons) {
//            startActivity(new Intent(MainActivity.this, FavoriteCartoonsActivity.class));
//        } else if (itemId == R.id.downloads) {
//            startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
//        } else if (itemId == R.id.support) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/m_c_w_a")));
//        } else if (itemId == R.id.rate) {
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://apps-anime.com")));
//        } else if (itemId == R.id.contact_us) {
//            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
//                    "mailto", "contactus.developerapps@gmail.com", null));
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
//            startActivity(Intent.createChooser(emailIntent, "Send email"));
//        }

        else if (itemId == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
            try {
                GoogleSignInClient client = GoogleAuth.getGoogleSignInClient(this);
                client.signOut();
                LoginManager.getInstance().logOut();
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
            }
            catch (Exception exception) {
             Log.d("ab_do" , exception.getMessage());
            }
        }
        else {
            Toast.makeText(this, "القسم تحت الصيانة أخي الغالي :)", Toast.LENGTH_LONG).show();
        }
        getAdmobData();
        hideDrawer();
        return true;
    }

    public void getFilms(String action){
        Log.i("ab_do" , "getFilms " + action);
        int pageNumber = 1 ;
        disposable.add(
                apiService
                        .getCartoons(pageNumber)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Cartoon>>() {
                            @Override
                            public void onSuccess(List<Cartoon> retrievedCartoonList) {
                                    for (int i = 0; i < retrievedCartoonList.size(); i++) {
//
                                        if (retrievedCartoonList.get(i).getTitle() != null && retrievedCartoonList.get(i).getTitle().equals("الافلام")) {
                                            Log.i("Ab_do", "catch Film");
                                            Cartoon cartoon = retrievedCartoonList.get(i);
                                            getPlaylists(cartoon , action);
                                            break;
                                        }
                                    }
                            }

                            @Override
                            public void onError(Throwable e) {
//                                Toast.makeText(getActivity(), getString(R.string.err_general), Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void getPlaylists(Cartoon cartoon, String action){
        Log.i("ab_bo" , "getPlaylists " +action);
        disposable.add(
                apiService
                        .getPlaylists(cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Playlist>>() {
                            @Override
                            public void onSuccess(List<Playlist> playlistList) {
                                for (int  i = 0 ; i < playlistList.size(); i++) {
                                    if (playlistList.get(i).getTitle() != null && playlistList.get(i).getTitle().trim().equals(action.trim())) {
                                        // catch playlist
                                        //dialogUtilities.dismissDialog();
                                        mBinding.progressBarLayout.setVisibility(View.GONE);
                                        Intent intent = new Intent(getBaseContext(), EpisodesActivity.class);
                                        intent.putExtra("playlist", playlistList.get(i));
                                        intent.putExtra("cartoon", cartoon);
                                        intent.putExtra("title" , action);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                //mBinding.progressBarLayout.setVisibility(View.GONE);
                            }
                        })
        );

    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        if (FavouriteRef!=null)
        FavouriteRef.removeEventListener(FavouriteListener);
        if (SeenEpisodesRef!=null)
        SeenEpisodesRef.removeEventListener(SeenEpisodeslistener);
        super.onDestroy();
    }
}
