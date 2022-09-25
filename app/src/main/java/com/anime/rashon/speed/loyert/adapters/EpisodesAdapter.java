package com.anime.rashon.speed.loyert.adapters;

import android.app.Activity;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.EpisodesActivity;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.app.UserOptions;
import com.anime.rashon.speed.loyert.databinding.LayoutNativeAdBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclerepisodeItemBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclerepisodeItemListBinding;
import com.anime.rashon.speed.loyert.model.Episode;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = EpisodesAdapter.class.getSimpleName();
    private Activity mContext;
    private List<Episode> episodeList;
    private String thumb;
    private String playlistTitle;
    private String cartoonTitle;
    private SQLiteDatabaseManager sqLiteDatabaseManager;

    private final int episodeView = 1;
    private final int nativeAdView = 2;
    private boolean grid ;

    public EpisodesAdapter(Activity mContext, List<Episode> episodeList, boolean grid) {
        this.mContext = mContext;
        this.episodeList = episodeList;
        this.thumb = ((EpisodesActivity)mContext).getThumb();
        this.playlistTitle = ((EpisodesActivity)mContext).getPlaylistTitle();
        this.cartoonTitle = ((EpisodesActivity)mContext).getCartoonTitle();
        this.grid = grid ;
        sqLiteDatabaseManager = new SQLiteDatabaseManager(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == nativeAdView){
            LayoutNativeAdBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_native_ad, parent, false);

            return new NativeAdHolder(binding.getRoot());
        }

        ViewDataBinding binding ;
        if (grid) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclerepisode_item, parent, false);
            return new EpisodeHolder((LayoutRecyclerepisodeItemBinding) binding);
        }
        else {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclerepisode_item_list, parent, false);
            return new EpisodeHolder((LayoutRecyclerepisodeItemListBinding) binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if(holder.getItemViewType() == nativeAdView){
            NativeAdHolder nativeAdHolder = (NativeAdHolder) holder;
            Config.loadNativeAd(mContext, nativeAdHolder.mBinding.nativeAdTemplate);
        }
        else{
            holder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext , R.anim.anim_itemview));
            final EpisodeHolder episodeHolder = (EpisodeHolder) holder;
            final Episode episode = episodeList.get(position);

            if (grid) {
                if (!TextUtils.isEmpty(episode.getThumb())) {
                    episodeHolder.gridBinding.setThumb(episode.getThumb());
                } else {
                    episodeHolder.gridBinding.setThumb(thumb);
                }

                if (!TextUtils.isEmpty(episode.getTitle())) {
                    episodeHolder.gridBinding.setTitle(episode.getTitle());
                } else {
                    episodeHolder.gridBinding.setTitle("الحلقة " + (position + 1));
                }

                if (UserOptions.getUserOptions().getSeenEpisodesIds().contains(episode.getId())) {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

                episodeHolder.itemView.setOnClickListener(v -> ((EpisodesActivity) mContext).startVideoActivity(position, episode, episodeHolder.gridBinding.getTitle(),
                        thumb, playlistTitle, cartoonTitle));
            }
            else {

                if (!TextUtils.isEmpty(episode.getTitle())) {
                    episodeHolder.listBinding.setTitle(episode.getTitle());
                } else {
                    episodeHolder.listBinding.setTitle("الحلقة " + (position + 1));
                }

                if (UserOptions.getUserOptions().getSeenEpisodesIds().contains(episode.getId())) {
                    episodeHolder.listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    episodeHolder.listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

                episodeHolder.itemView.setOnClickListener(v -> ((EpisodesActivity) mContext).startVideoActivity(position, episode, episodeHolder.listBinding.getTitle(),
                        thumb, playlistTitle, cartoonTitle));
            }
        }


//        if(position == episodeList.size()-1){
//            ((EpisodesActivity)mContext).getEpisodes();
//        }
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(episodeList.get(position).getId() == 0){
            return nativeAdView;
        }
        return episodeView;
    }

    public class EpisodeHolder extends RecyclerView.ViewHolder{

        LayoutRecyclerepisodeItemBinding gridBinding;
        LayoutRecyclerepisodeItemListBinding listBinding ;

        public EpisodeHolder(LayoutRecyclerepisodeItemBinding binding) {
            super(binding.getRoot());
            gridBinding = DataBindingUtil.bind(itemView);
        }
        public EpisodeHolder(LayoutRecyclerepisodeItemListBinding binding) {
            super(binding.getRoot());
            listBinding = DataBindingUtil.bind(itemView);
        }
    }

    private static class NativeAdHolder extends RecyclerView.ViewHolder{

        LayoutNativeAdBinding mBinding;

        NativeAdHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }

    public void updateList (List<Episode> episodeList) {
        this.episodeList = episodeList ;
    }

}
