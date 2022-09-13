package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
    boolean favourite ;

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
        binding.addFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext() , FeedbacksActivity.class).putExtra(Constants.CARTOON_ID , cartoon.getId()));
            }
        });
        binding.watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlaylistActivity();
            }
        });
        loginUtil = new LoginUtil(this);
        handleFavouriteBtn();
    }

    private void handleFavouriteBtn() {
        if (loginUtil.userIsLoggedIN()) {
            if (favourite) {
                binding.addFavourite.setImageResource(R.drawable.filled_star);
            } else {
                binding.addFavourite.setImageResource(R.drawable.empty_star);
            }
        }
        else binding.addFavourite.setImageResource(R.drawable.empty_star);
        binding.addFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginUtil.userIsLoggedIN() && loginUtil.getCurrentUser()!=null) {
                    canGoBack = false ;
                    binding.progressBarLayout.setVisibility(View.VISIBLE);
                    List<CartoonWithInfo> favouriteCartoons = UserOptions.getUserOptions().getFavouriteCartoons();
                    if (favourite) {
                        // delete from favourite
                        deleteFavourite(favouriteCartoons);
                    }
                    else {
                        // add favourite
                        addFavourite(favouriteCartoons);
                    }
                }
                else {
                    Snackbar snack = Snackbar.make(binding.getRoot() , "عفوا يرجي تسجيل الدخول أولا " , Snackbar.LENGTH_SHORT);
                    showSnack(snack);
                }
            }
        });
    }

    private void addFavourite(List<CartoonWithInfo> favouriteCartoons) {
        binding.addFavourite.setImageResource(R.drawable.filled_star);
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
                                    Snackbar snack = Snackbar.make(binding.getRoot() , "تم إضافة الإنمي إلي المفضلة بنجاح" , Snackbar.LENGTH_SHORT);
                                    showSnack(snack);
                                    canGoBack = true ;
                                    favourite = true ;
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

    private void deleteFavourite(List<CartoonWithInfo> favouriteCartoons) {
        binding.addFavourite.setImageResource(R.drawable.empty_star);
        disposable.add(
                apiService
                        .deleteFavourite(loginUtil.getCurrentUser().getId() , cartoon.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    favouriteCartoons.remove(cartoon);
                                    UserOptions.getUserOptions().setFavouriteCartoons(favouriteCartoons);
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Snackbar snack = Snackbar.make(binding.getRoot() , "تم إزالة الانمي من المفضلة بنجاح" , Snackbar.LENGTH_SHORT);
                                    showSnack(snack);
                                    canGoBack = true ;
                                    favourite = false ;
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
        if (information.getWorld_rate()!=null)
        binding.rate.setText("10 / " + information.getWorld_rate());
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
        getMenuInflater().inflate(R.menu.share_menu, menu);
//        if (sqLiteDatabaseManager.isCartoonFavorite(cartoon.getId())) {
//            menu.findItem(R.id.menu_empty_star).setVisible(false);
//            menu.findItem(R.id.menu_filled_star).setVisible(true);
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (canGoBack)
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();//No Action
        if (itemId == android.R.id.home) {
            if (canGoBack)
            finish();
        }
        else if (itemId == R.id.share) {
            Config.shareApp(InformationActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

}