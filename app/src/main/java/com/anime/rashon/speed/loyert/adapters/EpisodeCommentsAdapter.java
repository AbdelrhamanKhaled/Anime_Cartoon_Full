package com.anime.rashon.speed.loyert.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.Utilites.ReportDialog;
import com.anime.rashon.speed.loyert.databinding.CartoonFeedbackItemviewBinding;
import com.anime.rashon.speed.loyert.databinding.CommentItemViewBinding;
import com.anime.rashon.speed.loyert.model.EpisodeComment;
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

public class EpisodeCommentsAdapter extends RecyclerView.Adapter<EpisodeCommentsAdapter.commentHolder> {

    private final CompositeDisposable disposable;
    private final ApiService apiService;
    private final int user_id;
    List<EpisodeComment> comments = new ArrayList<>();
    private List<Integer> commentsLikesIDs = new ArrayList<>();
    private List<Integer> commentsDisLikesIDs = new ArrayList<>();
    Context context;
    ReportDialog reportDialog;

    public EpisodeCommentsAdapter(Activity context, int user_id, ApiService apiService, CompositeDisposable disposable) {
        this.context = context;
        this.apiService = apiService;
        this.disposable = disposable;
        this.user_id = user_id;
        reportDialog = new ReportDialog(context);
    }

    @NonNull
    @Override
    public commentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CommentItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.comment_item_view, parent, false);
        return new commentHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull commentHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    public void submitList(List<EpisodeComment> feedbacks) {
        this.comments = feedbacks;
        notifyDataSetChanged();
    }

    public void addComment(int pos, EpisodeComment comment) {
        this.comments.add(pos , comment);
        notifyDataSetChanged();
    }

    public void setCommentsLikesIDs(List<Integer> commentsLikesIDs) {
        this.commentsLikesIDs = commentsLikesIDs;
    }

    public void setCommentsDisLikesIDs(List<Integer> commentsDisLikesIDs) {
        this.commentsDisLikesIDs = commentsDisLikesIDs;
    }

    public class commentHolder extends RecyclerView.ViewHolder {
        CommentItemViewBinding binding;

        public commentHolder(@NonNull CommentItemViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(int pos) {
            if (comments.isEmpty()) return;
            EpisodeComment comment = comments.get(pos);
            binding.usernameTxtView.setText(comment.getUsername());
            binding.likesTxtView.setText(String.valueOf(comment.getLikes()));
            binding.dislikesTxtView.setText(String.valueOf(comment.getDislikes()));
            binding.feedbackTxtView.setText(comment.getComment());
            Glide.with(context)
                    .load(comment.getPhoto_Uri())
                    .error(R.drawable.user_profile)
                    .placeholder(R.drawable.user_profile)
                    .into(binding.userImgImageView);
            if (commentsLikesIDs.contains(comment.getCommentId()))
                binding.likesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.like_pressed, 0);
            else
                binding.likesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_thumb_up_24, 0);
            if (commentsDisLikesIDs.contains(comment.getCommentId()))
                binding.dislikesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.dislike_pressed, 0);
            else
                binding.dislikesTxtView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_thumb_down_24, 0);

            if(comment.getUserID() == user_id) {
                // this comment is belong to the user so hide report and show delete
                binding.deleteImgView.setVisibility(View.VISIBLE);
                binding.reportImgView.setVisibility(View.GONE);
            }
            else {
                binding.deleteImgView.setVisibility(View.GONE);
                binding.reportImgView.setVisibility(View.VISIBLE);
            }
            PrettyTime prettyTime = new PrettyTime(Locale.getDefault());
            String ago = prettyTime.format(new Date(Long.parseLong(comment.getTime())));
            if (ago.contains("بعض")) {
                ago = ago.replace( "بعض" , "منذ");
            }
            binding.dateTxtView.setText(ago);
            setListeners(comment , pos);
        }

        private void setListeners(EpisodeComment comment , int pos) {

            binding.likesTxtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // case --1-- user is already liked this comment :
                    // remove like from db and from list
                    if (commentsLikesIDs.contains(comment.getCommentId())) {
                        comment.decrementLikes();
                        commentsLikesIDs.remove(comment.getCommentId());
                        // add api call to remove like :
                        disposable.add(
                                apiService
                                        .removeCommentLike(user_id, comment.getCommentId())
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
                    // case --2-- user is already disliked this comment :
                    // so remove dislike and add like
                    else if (commentsDisLikesIDs.contains(comment.getCommentId())) {
                        comment.decrementDisLikes();
                        comment.incrementLikes();
                        commentsLikesIDs.add(comment.getCommentId());
                        commentsDisLikesIDs.remove(comment.getCommentId());
                        // add api call first to remove dislike
                        disposable.add(
                                apiService
                                        .removeCommentDislike(user_id, comment.getCommentId())
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
                                                                    .likeEpisodeComment(user_id, comment.getCommentId())
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
                        commentsLikesIDs.add(comment.getCommentId());
                        comment.incrementLikes();
                        // case --3-- user is not interact with this comment :
                        // so just add like
                        disposable.add(
                                apiService
                                        .likeEpisodeComment(user_id, comment.getCommentId())
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
                    // case --1-- user is already disliked this comment :
                    // remove dislike from db and from list
                    if (commentsDisLikesIDs.contains(comment.getCommentId())) {
                        commentsDisLikesIDs.remove(comment.getCommentId());
                        comment.decrementDisLikes();
                        // add api call to remove dislike :
                        disposable.add(
                                apiService
                                        .removeCommentDislike(user_id, comment.getCommentId())
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
                    // case --2-- user is already liked this comment :
                    // so remove like and add dislike
                    else if (commentsLikesIDs.contains(comment.getCommentId())) {
                        commentsLikesIDs.remove(comment.getCommentId());
                        commentsDisLikesIDs.add(comment.getCommentId());
                        comment.decrementLikes();
                        comment.incrementDisLikes();
                        // add api call first to remove like
                        disposable.add(
                                apiService
                                        .removeCommentLike(user_id, comment.getCommentId())
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
                                                                    .dislikeEpisodeComment(user_id, comment.getCommentId())
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
                        // case --3-- user is not interact with this comment :
                        // so just add dislike
                        comment.incrementDisLikes();
                        commentsDisLikesIDs.add(comment.getCommentId());
                        disposable.add(
                                apiService
                                        .dislikeEpisodeComment(user_id, comment.getCommentId())
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
                    reportDialog.setFeedback_id(comment.getCommentId());
                    reportDialog.setUser_id(comment.getUserID());
                    reportDialog.showDialog();
                }
            });

            binding.deleteImgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // delete comment
                    comments.remove(comment);
                    notifyItemChanged(pos);
                    // add api call to remove like :
                    disposable.add(
                            apiService
                                    .removeEpisodeComment(comment.getCommentId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                                        @Override
                                        public void onSuccess(UserResponse response) {
                                            if (!response.isError()) {
                                                Log.i("ab_do", "onSuccess remove comment");
                                                // now remove like from list
                                            } else {
                                                Log.i("ab_do", "error when remove comment");
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.i("ab_do", "error when remove comment");
                                        }
                                    })
                    );
                }
            });
        }

    }
}
