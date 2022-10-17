package com.anime.rashon.speed.loyert.adapters;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.DownloadedEpisodeItemviewBinding;
import com.anime.rashon.speed.loyert.model.Episode;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadHolder> {

    private final String TAG = DownloadsAdapter.class.getSimpleName();
    private Activity mContext;
    private List<Episode> downloadList;
    OnRemoveDownloadedEpisode onRemoveDownloadedEpisode;

    public DownloadsAdapter(Activity mContext, List<Episode> downloadList) {
        this.mContext = mContext;
        this.downloadList = downloadList;
        this.onRemoveDownloadedEpisode = (OnRemoveDownloadedEpisode) mContext;
    }

    @NonNull
    @Override
    public DownloadHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DownloadedEpisodeItemviewBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.downloaded_episode_itemview, parent, false);

        return new DownloadHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadHolder holder, final int position) {
        Episode episode = downloadList.get(position);

        holder.mBinding.cartoonTitle.setText(episode.getCartoon().getTitle());
        String episode_title = episode.getTitle() + "   " + episode.getPlaylist().getTitle();
        holder.mBinding.episodeName.setText(episode_title);
        String imgUrl;
        if (episode.getThumb() == null || episode.getThumb().isEmpty()) {
            imgUrl = episode.getPlaylist().getThumb();
        } else imgUrl = episode.getThumb();
        Glide.with(mContext).load(imgUrl).centerCrop().into(holder.mBinding.cartoonImg);
        holder.mBinding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Config.openExoPlayerApp(mContext, episode.getVideo_url(), episode, null);
            }
        });
//
        holder.mBinding.delete.setOnClickListener(v -> {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);

            builder.setMessage("هل تريد حذف الحلقة من التحميلات ؟");
            builder.setCancelable(true);
            builder.setPositiveButton("نعم", (dialog, which) -> {
                File file = new File(episode.getVideo_url());
                try {
                    boolean deleted = file.delete();
                } catch (Exception exception) {
                    Log.i("ab_do", "error when delete file " + exception.getMessage());
                }
                // add api call to remove episode from server
                onRemoveDownloadedEpisode.onRemove(position);
            });

            builder.setNegativeButton("لا", (dialog, which) -> dialog.cancel());

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    public  class DownloadHolder extends RecyclerView.ViewHolder {

        DownloadedEpisodeItemviewBinding mBinding;

        public DownloadHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            int theme_id = sharedPreferences.getInt(mContext.getString(R.string.THEME_KEY) , mContext.getResources().getInteger(R.integer.default_theme));
            if (theme_id == mContext.getResources().getInteger(R.integer.black_theme)) {
                mBinding.play.setImageResource(R.drawable.play_white);
            }
        }
    }

    public interface OnRemoveDownloadedEpisode {
        void onRemove(int pos);
    }
}
