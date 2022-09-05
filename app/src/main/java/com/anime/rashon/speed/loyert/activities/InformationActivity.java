package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.ActivityInformationBinding;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.Information;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class InformationActivity extends AppCompatActivity {
    ActivityInformationBinding binding ;
    Cartoon cartoon ;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private InformationActivity activity ;
    private Menu menu;
    SQLiteDatabaseManager sqLiteDatabaseManager ;

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
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        activity = this ;
        cartoon = (Cartoon) getIntent().getSerializableExtra("cartoon");
        binding.watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPlaylistActivity();
            }
        });
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
                    binding.age.setText("+13 سنة");
                    break;

                case 3:
                    binding.age.setText("+17 سنة");
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
        binding.rate.setText(information.getWorld_rate());
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
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        if (sqLiteDatabaseManager.isCartoonFavorite(cartoon.getId())) {
            menu.findItem(R.id.menu_empty_star).setVisible(false);
            menu.findItem(R.id.menu_filled_star).setVisible(true);
        }
        menu.findItem(R.id.change_order).setVisible(false);
        menu.findItem(R.id.grid_or_list).setVisible(false);
        menu.findItem(R.id.menusearch).setVisible(false);
        menu.findItem(R.id.share).setVisible(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();//No Action
        if (itemId == android.R.id.home) {
            finish();
        }
        else if (itemId == R.id.share) {
            Config.shareApp(InformationActivity.this);
        }
        else if (itemId == R.id.menu_empty_star) {
            menu.findItem(R.id.menu_empty_star).setVisible(false);
            menu.findItem(R.id.menu_filled_star).setVisible(true);
            sqLiteDatabaseManager.insertFavoriteCartoon(cartoon);
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                insertCartoonIntoFirebase(cartoon);
            setResult(RESULT_OK);
        } else if (itemId == R.id.menu_filled_star) {
            menu.findItem(R.id.menu_filled_star).setVisible(false);
            menu.findItem(R.id.menu_empty_star).setVisible(true);
            sqLiteDatabaseManager.deleteFavoriteCartoon(cartoon.getId());
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                deleteCartoonFromFirebase(cartoon);
            setResult(RESULT_OK);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteCartoonFromFirebase(Cartoon cartoon) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("FavouriteCartoon");
        myRef.removeValue();
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(this);
        List<Cartoon>  cartoons = sqLiteDatabaseManager.getCartoonsFavoriteData();
        for (Cartoon car : cartoons) {
            myRef.push().setValue(car);
        }
    }

    private void insertCartoonIntoFirebase(Cartoon cartoon) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("FavouriteCartoon");
        myRef.push().setValue(cartoon);
    }
}