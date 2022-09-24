package com.anime.rashon.speed.loyert.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.Utilites.LoginUtil;
import com.anime.rashon.speed.loyert.Utilites.ReportDialog;
import com.anime.rashon.speed.loyert.adapters.CartoonFeedbacksAdapter;
import com.anime.rashon.speed.loyert.databinding.ActivityCommentBinding;
import com.anime.rashon.speed.loyert.model.Feedback;
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

public class FeedbacksActivity extends AppCompatActivity implements ReportDialog.onReportClickListener {
    int user_id, cartoon_id;
    CompositeDisposable disposable;
    ApiService apiService;
    ActivityCommentBinding binding;
    List<Feedback> loaded_feedbacks = new ArrayList<>();
    List<Integer> feedbackLikesIDs = new ArrayList<>();
    List<Integer> feedbackDisLikesIDs = new ArrayList<>();
    LoginUtil loginUtil ;
    CartoonFeedbacksAdapter feedbacksAdapter ;
    private boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        loadFeedbacks();
    }

    private void init() {
        loginUtil = new LoginUtil(this) ;
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        user_id = loginUtil.getCurrentUser().getId();
        disposable = new CompositeDisposable();
        apiService = ApiClient.getClient(this).create(ApiService.class);
        cartoon_id = getIntent().getIntExtra(Constants.CARTOON_ID, -1);
        initToolbar();
        feedbacksAdapter = new CartoonFeedbacksAdapter(this , user_id , apiService , disposable);
        binding.recycleView.setAdapter(feedbacksAdapter);
        binding.recycleView.setItemAnimator(null);
        binding.sendFeedbackImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = binding.feedbackEditText.getText().toString();
                if (feedback.trim().isEmpty()) {
                    showSnackMsg("يرجي ملئ الحقل أولا !");
                }
                else {
                    addFeedback(feedback);
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
                 loadFeedbacks();
            }
        });
    }

    private void addFeedback(String feedback) {
        binding.progressBarLayout.setVisibility(View.VISIBLE);
        // add api call to save the feedback :)
        disposable.add(
                apiService
                        .addCartoonFeedback(user_id , cartoon_id , feedback,loginUtil.getCurrentUser().getName() , loginUtil.getCurrentUser().getPhoto_url())
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
                                    addNewFeedbackToAdapter(response.getReturned_id() , feedback);
                                }
                                else {
                                    binding.progressBarLayout.setVisibility(View.GONE);
                                    showSnackMsg("حدث خطا ما أثناء إضافة التوصية الخاصة بك");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.i("ab_do" , "onError addedFeedback " + e.getMessage());
                                binding.progressBarLayout.setVisibility(View.GONE);
                                showSnackMsg("حدث خطا ما أثناء إضافة التوصية الخاصة بك");
                            }
                        })
        );
    }

    private void addNewFeedbackToAdapter(int returned_id, String feedback) {
        Feedback _feedback = new Feedback(returned_id , cartoon_id , user_id , feedback , loginUtil.getCurrentUser().getName() , loginUtil.getCurrentUser().getPhoto_url() , 0 , 0);
        feedbacksAdapter.addFeedback(_feedback);
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
        getSupportActionBar().setTitle("التوصيات");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadFeedbacks() {
        disposable.add(
                apiService
                        .getFeedbacks(cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Feedback>>() {
                            @Override
                            public void onSuccess(List<Feedback> feedbacks) {
                                loaded_feedbacks.clear();
                                loaded_feedbacks = feedbacks;
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
                        .getFeedbacksLikesIds(user_id, cartoon_id)
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
                        .getFeedbacksDisLikesIds(user_id, cartoon_id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Integer>>() {
                            @Override
                            public void onSuccess(List<Integer> ids) {
                                feedbackDisLikesIDs = ids;
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
                                Toast.makeText(FeedbacksActivity.this, "حدث خطأ ما", Toast.LENGTH_SHORT).show();
                            }
                        })
        );
    }

    private void updateAdapter() {
        Log.i("ab_do" , "updateAdapter");
        feedbacksAdapter.setFeedbackLikesIDs(feedbackLikesIDs);
        feedbacksAdapter.setFeedbackDisLikesIDs(feedbackDisLikesIDs);
        feedbacksAdapter.submitList(loaded_feedbacks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void sendReport(int user_id , int feedback_id, String description) {
        Log.i("ab_do" , "id = " + feedback_id + " " + "description = " + description);
        disposable.add(
                apiService
                        .makeFeedbackReport(user_id , feedback_id , description)
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
    public void onReportClicked(String description, int feedback_id , int user_id) {
         binding.progressBarLayout.setVisibility(View.VISIBLE);
         sendReport(user_id , feedback_id , description);
    }
}