package com.anime.rashon.speed.loyert.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.MalfunctionsDialog;
import com.anime.rashon.speed.loyert.databinding.ActivitySettingsBinding;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class SettingsActivity extends AppCompatActivity implements MalfunctionsDialog.onReportClickListener {

    ActivitySettingsBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }



    private void init() {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        initToolbar();
    }


    private void initToolbar() {
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setTitle("الإعدادات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onReportClicked(String description , MalfunctionsDialog dialog) {
        // add api call to report a Malfunctions
        if(description.trim().isEmpty()) {
            Toast.makeText(this, "يرجي ملأ الحقل أولا", Toast.LENGTH_SHORT).show();
            return;
        }
        dialog.dismissDialog();
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        CompositeDisposable disposable = new CompositeDisposable();
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        disposable.add(
                apiService
                        .makeMalfunctionsReport(description)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إرسال الإبلاغ بنجاح سنعمل علي حل مشكلتك قريبا :)");

                                } else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Log.i("ab_do", "error when make report");
                                    showSnackMsg("حدث خطأ أثناء إرسال إبلاغك يرجي إعادة المحاولة لاحقا");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                    Log.i("ab_do", "error when make report");
                                    showSnackMsg("حدث خطأ أثناء إرسال إبلاغك يرجي إعادة المحاولة لاحقا");

                            }
                        })
        );
    }

    private void showSnackMsg (String s) {
        Snackbar snack = Snackbar.make(binding.getRoot(), s, Snackbar.LENGTH_LONG);
        showSnack(snack);
    }

    private void showSnack(Snackbar snack) {
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snack.show();
    }
}