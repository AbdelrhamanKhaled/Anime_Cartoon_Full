package com.app.cartoons.anime.newappcartoons.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.cartoons.anime.newappcartoons.Database.SQLiteDatabaseManager;
import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.activities.EpisodesActivity;
import com.app.cartoons.anime.newappcartoons.databinding.LayoutRecyclerepisodeItemBinding;
import com.app.cartoons.anime.newappcartoons.model.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = EpisodesAdapter.class.getSimpleName();
    private Context mContext;
    private List<Episode> episodeList;
    private String thumb;
    private String playlistTitle;
    private String cartoonTitle;
    private SQLiteDatabaseManager sqLiteDatabaseManager;

    public EpisodesAdapter(Context mContext, List<Episode> episodeList) {
        this.mContext = mContext;
        this.episodeList = episodeList;
        this.thumb = ((EpisodesActivity)mContext).getThumb();
        this.playlistTitle = ((EpisodesActivity)mContext).getPlaylistTitle();
        this.cartoonTitle = ((EpisodesActivity)mContext).getCartoonTitle();

        sqLiteDatabaseManager = new SQLiteDatabaseManager(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclerepisodeItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_recyclerepisode_item, parent, false);

        return new EpisodeHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final EpisodeHolder episodeHolder = (EpisodeHolder) holder;
        final Episode episode = episodeList.get(position);

        if(!TextUtils.isEmpty(episode.getThumb())){
            episodeHolder.mBinding.setThumb(episode.getThumb());
        }else{
            episodeHolder.mBinding.setThumb(thumb);
        }

        if(!TextUtils.isEmpty(episode.getTitle())){
            episodeHolder.mBinding.setTitle(episode.getTitle());
        }else{
            episodeHolder.mBinding.setTitle("الحلقة " + (position + 1));
        }

        if(sqLiteDatabaseManager.isEpisodeSeen(episode.getId())){
            episodeHolder.mBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_seen_icon));
        }else{
            episodeHolder.mBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_not_seen));
        }

        episodeHolder.itemView.setOnClickListener(v -> ((EpisodesActivity)mContext).startVideoActivity(position, episode, episodeHolder.mBinding.getTitle(),
                thumb, playlistTitle, cartoonTitle));

        if(position == episodeList.size()-1){
            ((EpisodesActivity)mContext).getEpisodes();
        }
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    public class EpisodeHolder extends RecyclerView.ViewHolder{

        LayoutRecyclerepisodeItemBinding mBinding;

        public EpisodeHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
