package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.app.UserOptions;
import com.anime.rashon.speed.loyert.databinding.ActivityInformationBinding;
import com.anime.rashon.speed.loyert.model.CartoonWithInfo;
import com.anime.rashon.speed.loyert.model.Information;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class InformationActivity extends AppCompatActivity {
    ActivityInformationBinding binding ;
    CartoonWithInfo cartoon ;
    private final CompositeDisposable disposable = new CompositeDisposable();
    ApiService apiService ;
    private InformationActivity activity ;
    private Menu menu;
    SQLiteDatabaseManager sqLiteDatabaseManager ;
    LoginUtil loginUtil ;
    boolean canGoBack = true ;
    boolean favourite , addedToWatched , addedToWatchedLater ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInformationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        initToolbar();
        getInformation();
    }

    private void init() {
        apiService = ApiClient.getClient(this).create(ApiService.class);
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        activity = this ;
        cartoon = (CartoonWithInfo) getIntent().getSerializableExtra("cartoon");
        favourite = UserOptions.getUserOptions().getFavouriteCartoonsIds().contains(cartoon.getId());
        addedToWatched = UserOptions.getUserOptions().getWatchedCartoonsIds().contains(cartoon.getId());
        addedToWatchedLater = UserOptions.getUserOptions().getWatchLaterCartoonsIds().contains(cartoon.getId());
        binding.addFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null)
                startActivity(new Intent(getBaseContext() , FeedbacksActivity.class).putExtra(Constants.CARTOON_ID , cartoon.getId()));
                else {
                    showSnackMsg("عفوا يرجي تسجيل الدخول أولا ");
                }

            }
        });
        binding.watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlaylistActivity();
            }
        });
        loginUtil = new LoginUtil(this);
        initMyListBtn();
    }

    private void showSnackMsg (String s) {
        Snackbar snack = Snackbar.make(binding.getRoot(), s, Snackbar.LENGTH_SHORT);
        showSnack(snack);
    }

    private void initMyListBtn() {
        updateAddToWatchLaterIcon();
        binding.addToWatchLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginUtil.userIsLoggedIN()&&loginUtil.getCurrentUser()!=null) {
                    if (canGoBack) {
                        binding.progressBarLayout.setVisibility(View.VISIBLE);
                        if (addedToWatchedLater) {
                            // remove it
                            canGoBack = false;
                            removeWatchLaterCartoon();
                        } else {
                            // add it
                            canGoBack = false;
                            addWatchLaterCartoon();
                        }
                    }
                }
                else {
                    showSnackMsg("عفوا يرجي تسجيل الدخول أولا ");
                }
            }
        });
    }

    private void addWatchLaterCartoon() {
        disposable.add(
                apiService
                        .addWatchedLaterCartoon(loginUtil.getCurrentUser().getId() , cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    List<CartoonWithInfo> watchedLaterCartoons = UserOptions.getUserOptions().getWatchLaterCartoons();
                                    watchedLaterCartoons.add(cartoon);
                                    UserOptions.getUserOptions().setWatchLaterCartoons(watchedLaterCartoons);
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إضافة الإنمي إلي قائمتئ بنجاح");
                                    canGoBack = true ;
                                    addedToWatchedLater = true ;
                                    updateAddToWatchLaterIcon();
                                }
                                else {
                                    canGoBack = true ;
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(InformationActivity.this, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                canGoBack = true ;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(InformationActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void removeWatchLaterCartoon() {
        disposable.add(
                apiService
                        .removeWatchLater(loginUtil.getCurrentUser().getId() , cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    List<CartoonWithInfo> watchedLaterCartoons = UserOptions.getUserOptions().getWatchLaterCartoons();
                                    watchedLaterCartoons.remove(cartoon);
                                    UserOptions.getUserOptions().setWatchLaterCartoons(watchedLaterCartoons);
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إزالة الإنمي من قائمتئ بنجاح");
                                    canGoBack = true ;
                                    addedToWatchedLater = false ;
                                    updateAddToWatchLaterIcon();
                                }
                                else {
                                    canGoBack = true ;
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(InformationActivity.this, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                canGoBack = true ;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(InformationActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void updateAddToWatchLaterIcon() {
        // update icon
        if (addedToWatchedLater) {
            binding.addToWatchLater.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_done_24, 0, 0);
        }
        else {
            binding.addToWatchLater.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_add_24, 0, 0);
        }
    }

    private void handleFavouriteBtn(MenuItem favourite_item) {
        if (loginUtil==null) loginUtil = new LoginUtil(this);
        if (loginUtil.userIsLoggedIN()) {
            if (favourite) {
                favourite_item.setIcon(R.drawable.filled_star);
            } else {
                favourite_item.setIcon(R.drawable.empty_star);
            }
        }
        else favourite_item.setIcon(R.drawable.empty_star);
    }


    private void addFavourite(List<CartoonWithInfo> favouriteCartoons, MenuItem item) {
        // add api call to add favourite cartoon
        disposable.add(
                apiService
                        .addFavourite(loginUtil.getCurrentUser().getId() , cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    favouriteCartoons.add(cartoon);
                                    UserOptions.getUserOptions().setFavouriteCartoons(favouriteCartoons);
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إضافة الإنمي إلي المفضلة بنجاح");
                                    canGoBack = true ;
                                    favourite = true ;
                                    item.setIcon(R.drawable.filled_star);
                                }
                                else {
                                    canGoBack = true ;
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(InformationActivity.this, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                canGoBack = true ;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(InformationActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void deleteFavourite(List<CartoonWithInfo> favouriteCartoons, MenuItem item) {
        disposable.add(
                apiService
                        .deleteFavourite(loginUtil.getCurrentUser().getId() , cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    boolean del = favouriteCartoons.remove(cartoon);
                                    Log.i("ab_do" , "item is Deleted "  + del);
                                    UserOptions.getUserOptions().setFavouriteCartoons(favouriteCartoons);
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إزالة الانمي من المفضلة بنجاح");
                                    canGoBack = true ;
                                    favourite = false ;
                                    item.setIcon(R.drawable.empty_star);
                                }
                                else {
                                    canGoBack = true ;
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(InformationActivity.this, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                canGoBack = true ;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(InformationActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void showSnack(Snackbar snack) {
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snack.show();
    }

    private void getInformation() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        disposable.add(
                apiService
                        .getCartoonInformation(cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Information>() {
                            @Override
                            public void onSuccess(Information information) {
                                if (information == null) {
                                    goToPlaylistActivity();
                                    finish();
                                    return;
                                }
                                InformationActivity.this.updateUI(information);
                                binding.progressBarLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                            }
                        })
        );
    }

    private void updateUI(Information information) {
        if(information.getStory() != null && information.getStory().length()!=0){
            if (information.getStory().length() > 300) {
                binding.story.setText(information.getStory().substring(0, 300));
                binding.story.append(" ... ");
                binding.showMore.setVisibility(View.VISIBLE);
                binding.showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        binding.story.setText(information.getStory());
                        binding.showMore.setVisibility(View.GONE);
                    }
                });
            }
            else {
                binding.story.setText(information.getStory());
                binding.showMore.setVisibility(View.GONE);
            }

        }
        else {
            binding.story.setText("غير متاحة");
        }

        if (information.getAge_rate()!=null) {
            switch (information.getAge_rate()) {
                case 1:
                    binding.age.setText("لكل الاعمار");
                    break;

                case 2:
                    binding.age.setText("+ 13");
                    break;

                case 3:
                    binding.age.setText("+ 17");
                    break;
                default:
                    binding.age.setText("غير محدد");
            }
        }
        else {
            binding.age.setText("غير محدد");
        }
        if (information.getStatus()!=null) {
            switch (information.getStatus()) {
                case 1:
                    binding.statues.setText("مكتمل");
                    break;

                case 2:
                    binding.statues.setText("مستمر");
                    break;
                default:
                    binding.statues.setText("");
            }
        }
        else binding.statues.setText("");
        if (cartoon.getTitle()!=null && !cartoon.getTitle().isEmpty())
        binding.title.setText(cartoon.getTitle());
        else
        binding.title.setText("");
        if (information.getView_date()!=null)
        binding.year.setText(information.getView_date());
        else binding.year.setText("غير محدد");
        if (information.getCategory()!=null)
        binding.category.setText(information.getCategory());
        else binding.category.setText("غير محدد");
        if (information.getWorld_rate()!=null) {
            String rate = information.getWorld_rate() + "/10";
            SpannableString ss1=  new SpannableString(rate);
            int end_index = rate.indexOf("/");
            ss1.setSpan(new RelativeSizeSpan(1.2f), 0,end_index, 0); // set size
            ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, end_index, 0);
            binding.rate.setText(ss1);
        }
        else binding.rate.setText("غير محدد");
        if (cartoon.getThumb()!=null && !cartoon.getThumb().isEmpty()) {
            String imgUrl = cartoon.getThumb();
            Glide.with(activity)
                    .load(imgUrl)
                    .centerCrop()
                    .into(binding.img);
            Glide.with(activity)
                    .load(imgUrl)
                    .centerCrop()
                    .into(binding.background);
        }
        else {
            Glide.with(activity)
                    .load(R.raw.loading_1)
                    .centerCrop()
                    .into(binding.img);
            Glide.with(activity)
                    .load(R.raw.loading_1)
                    .centerCrop()
                    .into(binding.background);
        }
    }

    private void initToolbar() {
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setTitle("حول الإنمي");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void goToPlaylistActivity() {
        Intent intent = new Intent(getBaseContext(), PlayListsActivity.class);
        intent.putExtra("cartoon", cartoon);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu ;
        sqLiteDatabaseManager = new SQLiteDatabaseManager(InformationActivity.this);
        getMenuInflater().inflate(R.menu.information_activity_menu, menu);
        MenuItem favourite = menu.findItem(R.id.favourite);
        handleFavouriteBtn(favourite);
        menu.findItem(R.id.add_to_watched).setVisible(!addedToWatched);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (canGoBack) {
            doFinish();
        }
        super.onBackPressed();
    }

    private void doFinish() {
        if (isTaskRoot()) {
            // it`s the last activity now so just start the main Activity
            startActivity(new Intent(getBaseContext() , MainActivity.class));
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();//No Action
        if (itemId == android.R.id.home) {
            if (canGoBack)
                doFinish();
        }
        else if (itemId == R.id.share) {
            Config.shareApp(InformationActivity.this);
        }
        else if (itemId == R.id.favourite) {
            if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null) {
                canGoBack = false ;
                binding.progressBarLayout.setVisibility(View.VISIBLE);
                List<CartoonWithInfo> favouriteCartoons = UserOptions.getUserOptions().getFavouriteCartoons();
                if (favourite) {
                    // delete from favourite
                    deleteFavourite(favouriteCartoons , menu.findItem(R.id.favourite));
                }
                else {
                    // add favourite
                    addFavourite(favouriteCartoons , menu.findItem(R.id.favourite));
                }
            }
            else {
                showSnackMsg("عفوا يرجي تسجيل الدخول أولا ");
            }
        }

        else if (itemId == R.id.add_to_watched) {
            if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null) {
                canGoBack = false ;
                binding.progressBarLayout.setVisibility(View.VISIBLE);
                addCartoonToWatched(menu.findItem(R.id.add_to_watched));
            }
            else {
                showSnackMsg("عفوا يرجي تسجيل الدخول أولا ");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void addCartoonToWatched(MenuItem item) {
        // add api call to add watched cartoon
        disposable.add(
                apiService
                        .addWatchedCartoon(loginUtil.getCurrentUser().getId() , cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    List<CartoonWithInfo> watchedCartoons = UserOptions.getUserOptions().getWatchedCartoons();
                                    watchedCartoons.add(cartoon);
                                    UserOptions.getUserOptions().setWatchedCartoons(watchedCartoons);
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إضافة الإنمي إلي قائمة ( تمت مشاهدته ) بنجاح");
                                    canGoBack = true ;
                                    addedToWatched = true ;
                                    item.setVisible(false);
                                }
                                else {
                                    canGoBack = true ;
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(InformationActivity.this, "حدث خطأ ما يرجي إعادة المحاولة لاحقا", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onError(Throwable e) {
                                canGoBack = true ;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(InformationActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

}