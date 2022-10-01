package com.anime.rashon.speed.loyert.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.Utilites.ReportDialog;
import com.anime.rashon.speed.loyert.adapters.EpisodeCommentsAdapter;
import com.anime.rashon.speed.loyert.databinding.ActivityEpisodeCommentsBinding;
import com.anime.rashon.speed.loyert.model.EpisodeComment;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiClient;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class EpisodeCommentsActivity extends AppCompatActivity implements ReportDialog.onReportClickListener {
    int user_id, episode_id;
    CompositeDisposable disposable;
    ApiService apiService;
    ActivityEpisodeCommentsBinding binding;
    List<EpisodeComment> loaded_comments = new ArrayList<>();
    List<Integer> commentsLikesIDs = new ArrayList<>();
    List<Integer> commentsDisLikesIDs = new ArrayList<>();
    LoginUtil loginUtil ;
    EpisodeCommentsAdapter commentsAdapter ;
    private boolean isRefresh = false;
    boolean desc = true ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEpisodeCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadComments();
    }

    private void init() {
        loginUtil = new LoginUtil(this) ;
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        user_id = loginUtil.getCurrentUser().getId();
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        episode_id = getIntent().getIntExtra(Constants.EPISODE_ID, -1);
        initToolbar();
        commentsAdapter = new EpisodeCommentsAdapter(this , user_id , apiService , disposable);
        binding.recycleView.setAdapter(commentsAdapter);
        binding.recycleView.setItemAnimator(null);
        binding.sendFeedbackImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = binding.feedbackEditText.getText().toString();
                if (comment.trim().isEmpty()) {
                    showSnackMsg("يرجي ملئ الحقل أولا !");
                }
                else {
                    addComment(comment);
                }
            }
        });
        initSwipeRefresh();
    }

    private void initSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRefreshLayout.setRefreshing(true);
                isRefresh = true;
                loadComments();
            }
        });
    }

    private void addComment(String comment) {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        // add api call to save the feedback :)
        disposable.add(
                apiService
                        .addEpisodeComment(user_id , episode_id, comment ,loginUtil.getCurrentUser().getName() , loginUtil.getCurrentUser().getPhoto_url() , String.valueOf(System.currentTimeMillis()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                Log.i("ab_do" , "onSuccess addedFeedback");
                                if (!response.isError()) {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    binding.feedbackEditText.setText("");
                                    binding.feedbackEditText.clearFocus();
                                    addNewCommentToAdapter(response.getReturned_id() , comment);
                                }
                                else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("حدث خطا ما أثناء إضافة التعليق الخاص بك");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do" , "onError addedFeedback " + e.getMessage());
                                binding.progressBarLayout.setVisibility(View.GONE);
                                showSnackMsg("حدث خطا ما أثناء إضافة التعليق الخاص بك");
                            }
                        })
        );
    }

    private void addNewCommentToAdapter(int returned_id, String comment) {
        EpisodeComment episodeComment = new EpisodeComment(returned_id , episode_id, user_id , comment , loginUtil.getCurrentUser().getName() , loginUtil.getCurrentUser().getPhoto_url() , 0 , 0 , String.valueOf(System.currentTimeMillis()));
        int pos = desc ? 0 : loaded_comments.size() ;
        commentsAdapter.addComment(pos , episodeComment);
    }

    private void showSnackMsg (String s) {
        Snackbar snack = Snackbar.make(binding.getRoot(), s, Snackbar.LENGTH_SHORT);
        showSnack(snack);
    }

    private void showSnack(Snackbar snack) {
        View view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.CENTER;
        view.setLayoutParams(params);
        snack.show();
    }

    private void initToolbar() {
        setSupportActionBar(binding.includedToolbar.toolbar);
        getSupportActionBar().setTitle("التعليقات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadComments() {
        if (desc) {
            disposable.add(
                    apiService
                            .getCommentsDesc(episode_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<EpisodeComment>>() {
                                @Override
                                public void onSuccess(List<EpisodeComment> episodeComments) {
                                    loaded_comments.clear();
                                    loaded_comments = episodeComments;
                                    loadFeedbackLikesIDS();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.i("ab_do" , "onError " + e.getMessage());
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(EpisodeCommentsActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                }
                            })
            );
        }
        else {
            disposable.add(
                    apiService
                            .getCommentsAsc(episode_id)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(new DisposableSingleObserver<List<EpisodeComment>>() {
                                @Override
                                public void onSuccess(List<EpisodeComment> episodeComments) {
                                    loaded_comments.clear();
                                    loaded_comments = episodeComments;
                                    loadFeedbackLikesIDS();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    Toast.makeText(EpisodeCommentsActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                                }
                            })
            );
        }

    }

    private void loadFeedbackLikesIDS() {
        disposable.add(
                apiService
                        .getCommentsLikesIds(user_id, episode_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                commentsLikesIDs = ids;
                                loadFeedbackDisLikesIDS();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(EpisodeCommentsActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void loadFeedbackDisLikesIDS() {
        disposable.add(
                apiService
                        .getCommentsDisLikesIds(user_id, episode_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                commentsDisLikesIDs = ids;
                                binding.progressBarLayout.setVisibility(View.GONE);
                                if (isRefresh) {
                                    binding.swipeRefreshLayout.setRefreshing(false);
                                    isRefresh = false;
                                }
                                updateAdapter();
                            }

                            @Override
                            public void onError(Throwable e) {
                                binding.progressBarLayout.setVisibility(View.GONE);
                                Toast.makeText(EpisodeCommentsActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void updateAdapter() {
        Log.i("ab_do" , "updateAdapter");
        commentsAdapter.setCommentsLikesIDs(commentsLikesIDs);
        commentsAdapter.setCommentsDisLikesIDs(commentsDisLikesIDs);
        commentsAdapter.submitList(loaded_comments);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        else if (item.getItemId() == R.id.change_order) {
            desc = !desc ;
            binding.progressBarLayout.setVisibility(View.VISIBLE);
            loadComments();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void sendReport(int user_id , int comment_id, String description) {
        Log.i("ab_do" , "id = " + comment_id + " " + "description = " + description);
        disposable.add(
                apiService
                        .makeCommentReport(user_id , comment_id , description)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                            @Override
                            public void onSuccess(UserResponse response) {
                                if (!response.isError()) {
                                    Log.i("ab_do", "onSuccess make report");
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("تم إرسال الإبلاغ بنجاح");

                                } else {
                                    Log.i("ab_do", "error when make report");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do", "error when make report");
                            }
                        })
        );
    }

    @Override
    public void onReportClicked(String description, int comment_id , int user_id) {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        sendReport(user_id , comment_id , description);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.feedback_menu , menu) ;
        return super.onCreateOptionsMenu(menu);
    }


}