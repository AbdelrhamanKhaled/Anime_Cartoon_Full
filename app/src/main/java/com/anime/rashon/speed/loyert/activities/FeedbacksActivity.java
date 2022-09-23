package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.databinding.ActivityCommentBinding;
import com.anime.rashon.speed.loyert.model.Feedbacks;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class FeedbacksActivity extends AppCompatActivity {
    int user_id , cartoon_id ;
    CompositeDisposable disposable ;
    ApiService apiService ;
    ActivityCommentBinding binding ;
    List<Feedbacks> loaded_feedbacks = new ArrayList<>();
    List<Integer> feedbackLikesIDs = new ArrayList<>();
    List<Integer> feedbackDisLikesIDs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadFeedbacks();
    }

    private void init() {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        user_id = new LoginUtil(this).getCurrentUser().getId();
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        cartoon_id = getIntent().getIntExtra(Constants.CARTOON_ID , -1);
    }

    private void loadFeedbacks() {
        disposable.add(
                apiService
                        .getFeedbacks(cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Feedbacks>>() {
                            @Override
                            public void onSuccess(List<Feedbacks> feedbacks) {
                                loaded_feedbacks = feedbacks ;
                                loadFeedbackLikesIDS();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );

    }

    private void loadFeedbackLikesIDS() {
        disposable.add(
                apiService
                        .getFeedbacksLikesIds(user_id , cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                feedbackLikesIDs = ids;
                                loadFeedbackDisLikesIDS();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadFeedbackDisLikesIDS() {
        disposable.add(
                apiService
                        .getFeedbacksDisLikesIds(user_id , cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                feedbackDisLikesIDs = ids;
                                binding.progressBarLayout.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }
}