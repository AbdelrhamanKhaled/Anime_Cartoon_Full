package com.anime.rashon.speed.loyert.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.ReportDialog;
import com.anime.rashon.speed.loyert.activities.FeedbacksActivity;
import com.anime.rashon.speed.loyert.databinding.CartoonFeedbackItemviewBinding;
import com.anime.rashon.speed.loyert.model.Feedback;
import com.anime.rashon.speed.loyert.model.UserResponse;
import com.anime.rashon.speed.loyert.network.ApiService;
import com.bumptech.glide.Glide;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartoonFeedbacksAdapter extends RecyclerView.Adapter<CartoonFeedbacksAdapter.feedbackHolder> {
    private final CompositeDisposable disposable;
    private final ApiService apiService;
    private final int user_id;
    List<Feedback> feedbacks = new ArrayList<>();
    private List<Integer> feedbackLikesIDs = new ArrayList<>();
    private List<Integer> feedbackDisLikesIDs = new ArrayList<>();
    Context context;
    ReportDialog reportDialog;

    public CartoonFeedbacksAdapter(FeedbacksActivity context, int user_id, ApiService apiService, CompositeDisposable disposable) {
        this.context = context;
        this.apiService = apiService;
        this.disposable = disposable;
        this.user_id = user_id;
        reportDialog = new ReportDialog(context);
    }

    @NonNull
    @Override
    public feedbackHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CartoonFeedbackItemviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.cartoon_feedback_itemview, parent, false);
        return new feedbackHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull feedbackHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return feedbacks.size();
    }

    public void submitList(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
        notifyDataSetChanged();
    }

    public void addFeedback(int pos , Feedback feedback) {
        this.feedbacks.add(pos , feedback);
        notifyDataSetChanged();
    }

    public class feedbackHolder extends RecyclerView.ViewHolder {
        CartoonFeedbackItemviewBinding binding;

        public feedbackHolder(@NonNull CartoonFeedbackItemviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            if (feedbacks.isEmpty()) return;
            Feedback feedback = feedbacks.get(pos);
            binding.usernameTxtView.setText(feedback.getUsername());
            binding.likesTxtView.setText(String.valueOf(feedback.getLikes()));
            binding.dislikesTxtView.setText(String.valueOf(feedback.getDislikes()));
            binding.feedbackTxtView.setText(feedback.getFeedback());
            Glide.with(context)
                    .load(feedback.getPhoto_Uri())
                    .error(R.drawable.user_profile)
                    .placeholder(R.drawable.user_profile)
                    .into(binding.userImgImageView);
            if (feedbackLikesIDs.contains(feedback.getFeedbackId()))
                binding.likesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.like_pressed, 0);
            else
                binding.likesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_thumb_up_24, 0);
            if (feedbackDisLikesIDs.contains(feedback.getFeedbackId()))
                binding.dislikesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dislike_pressed, 0);
            else
                binding.dislikesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_thumb_down_24, 0);

            if(feedback.getUserID() == user_id) {
                // this feedback is belong to the user so hide report and show delete
                binding.deleteImgView.setVisibility(View.VISIBLE);
                binding.reportImgView.setVisibility(View.GONE);
            }
            else {
                binding.deleteImgView.setVisibility(View.GONE);
                binding.reportImgView.setVisibility(View.VISIBLE);
            }
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(new Date(Long.parseLong(feedback.getTime())));
            if (ago.contains("بعض")) {
                ago = ago.replace( "بعض" , "منذ");
            }
            binding.dateTxtView.setText(ago);
            setListeners(feedback , pos);
        }

        private void setListeners(Feedback feedback , int pos) {

            binding.likesTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // case --1-- user is already liked this feedback :
                    // remove like from db and from list
                    if (feedbackLikesIDs.contains(feedback.getFeedbackId())) {
                        feedback.decrementLikes();
                        feedbackLikesIDs.remove(feedback.getFeedbackId());
                        // add api call to remove like :
                        disposable.add(
                                apiService
                                        .removeLike(user_id, feedback.getFeedbackId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                            @Override
                                            public void onSuccess(UserResponse response) {
                                                if (!response.isError()) {
                                                    Log.i("ab_do", "onSuccess save remove like");
                                                    // now remove like from list
                                                } else {
                                                    Log.i("ab_do", "error when save remove like");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("ab_do", "error when save remove like");
                                            }
                                        })
                        );
                    }
                    // case --2-- user is already disliked this feedback :
                    // so remove dislike and add like
                    else if (feedbackDisLikesIDs.contains(feedback.getFeedbackId())) {
                        feedback.decrementDisLikes();
                        feedback.incrementLikes();
                        feedbackLikesIDs.add(feedback.getFeedbackId());
                        feedbackDisLikesIDs.remove(feedback.getFeedbackId());
                        // add api call first to remove dislike
                        disposable.add(
                                apiService
                                        .removeDisLike(user_id, feedback.getFeedbackId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                            @Override
                                            public void onSuccess(UserResponse response) {
                                                if (!response.isError()) {
                                                    Log.i("ab_do", "onSuccess remove dislike");
                                                    // now add user like
                                                    disposable.add(
                                                            apiService
                                                                    .likeFeedback(user_id, feedback.getFeedbackId())
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                                                        @Override
                                                                        public void onSuccess(UserResponse response) {
                                                                            if (!response.isError()) {
                                                                                Log.i("ab_do", "onSuccess save like");
                                                                            } else {
                                                                                Log.i("ab_do", "error when save like");
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onError(Throwable e) {
                                                                            Log.i("ab_do", "error when save like");
                                                                        }
                                                                    })
                                                    );
                                                }

                                                else {
                                                    Log.i("ab_do", "error when  remove dislike");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("ab_do", "error when remove dislike");
                                            }
                                        })
                        );
                    }

                    else {
                        feedbackLikesIDs.add(feedback.getFeedbackId());
                        feedback.incrementLikes();
                        // case --3-- user is not interact with this feedback :
                        // so just add like
                        disposable.add(
                                apiService
                                        .likeFeedback(user_id, feedback.getFeedbackId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                            @Override
                                            public void onSuccess(UserResponse response) {
                                                if (!response.isError()) {
                                                    Log.i("ab_do", "onSuccess save like");
                                                } else {
                                                    Log.i("ab_do", "error when save like");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("ab_do", "error when save like");
                                            }
                                        })
                        );
                    }
                    notifyItemChanged(pos);
                }
            });

            binding.dislikesTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // case --1-- user is already disliked this feedback :
                    // remove dislike from db and from list
                    if (feedbackDisLikesIDs.contains(feedback.getFeedbackId())) {
                        feedbackDisLikesIDs.remove(feedback.getFeedbackId());
                        feedback.decrementDisLikes();
                        // add api call to remove dislike :
                        disposable.add(
                                apiService
                                        .removeDisLike(user_id, feedback.getFeedbackId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                            @Override
                                            public void onSuccess(UserResponse response) {
                                                if (!response.isError()) {
                                                    Log.i("ab_do", "onSuccess save remove dislike");
                                                    // now remove like from list
                                                } else {
                                                    Log.i("ab_do", "error when save remove dislike");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("ab_do", "error when save remove dislike");
                                            }
                                        })
                        );
                    }
                    // case --2-- user is already liked this feedback :
                    // so remove like and add dislike
                    else if (feedbackLikesIDs.contains(feedback.getFeedbackId())) {
                        feedbackLikesIDs.remove(feedback.getFeedbackId());
                        feedbackDisLikesIDs.add(feedback.getFeedbackId());
                        feedback.decrementLikes();
                        feedback.incrementDisLikes();
                        // add api call first to remove like
                        disposable.add(
                                apiService
                                        .removeLike(user_id, feedback.getFeedbackId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                            @Override
                                            public void onSuccess(UserResponse response) {
                                                if (!response.isError()) {
                                                    Log.i("ab_do", "onSuccess remove like");
                                                    // now add user dislike
                                                    disposable.add(
                                                            apiService
                                                                    .dislikeFeedback(user_id, feedback.getFeedbackId())
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread())
                                                                    .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                                                        @Override
                                                                        public void onSuccess(UserResponse response) {
                                                                            if (!response.isError()) {
                                                                                Log.i("ab_do", "onSuccess save dislike");
                                                                            }
                                                                            else {
                                                                                Log.i("ab_do", "error when save dislike");
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onError(Throwable e) {
                                                                            Log.i("ab_do", "error when save dislike");
                                                                        }
                                                                    })
                                                    );
                                                }

                                                else {
                                                    Log.i("ab_do", "error  save remove like");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("ab_do", "error when remove like");
                                            }
                                        })
                        );
                    }

                    else {
                        // case --3-- user is not interact with this feedback :
                        // so just add dislike
                        feedback.incrementDisLikes();
                        feedbackDisLikesIDs.add(feedback.getFeedbackId());
                        disposable.add(
                                apiService
                                        .dislikeFeedback(user_id, feedback.getFeedbackId())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                            @Override
                                            public void onSuccess(UserResponse response) {
                                                if (!response.isError()) {
                                                    Log.i("ab_do", "onSuccess save dislike");
                                                } else {
                                                    Log.i("ab_do", "error when save dislike");
                                                }
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.i("ab_do", "error when save dislike");
                                            }
                                        })
                        );
                    }
                    notifyItemChanged(pos);
                }
            });

            binding.reportImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportDialog.setFeedback_id(feedback.getFeedbackId());
                    reportDialog.setUser_id(feedback.getUserID());
                    reportDialog.showDialog();
                }
            });

            binding.deleteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete feedback
                    feedbacks.remove(feedback);
                    notifyItemChanged(pos);
                    // add api call to remove like :
                    disposable.add(
                            apiService
                                    .removeFeedback(feedback.getFeedbackId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                        @Override
                                        public void onSuccess(UserResponse response) {
                                            if (!response.isError()) {
                                                Log.i("ab_do", "onSuccess remove feedback");
                                                // now remove like from list
                                            } else {
                                                Log.i("ab_do", "error when remove feedback");
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.i("ab_do", "error when remove feedback");
                                        }
                                    })
                    );
                }
            });
        }

    }

    public void setFeedbackLikesIDs(List<Integer> feedbackLikesIDs) {
        this.feedbackLikesIDs = feedbackLikesIDs;
    }

    public void setFeedbackDisLikesIDs(List<Integer> feedbackDisLikesIDs) {
        this.feedbackDisLikesIDs = feedbackDisLikesIDs;
    }

}
