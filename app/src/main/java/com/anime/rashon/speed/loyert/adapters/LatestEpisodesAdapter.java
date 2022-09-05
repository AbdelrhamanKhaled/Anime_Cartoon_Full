package com.anime.rashon.speed.loyert.adapters;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.EpisodesActivity;
import com.anime.rashon.speed.loyert.app.Config;
import com.anime.rashon.speed.loyert.databinding.LayoutLatestEpisodeItemBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutLatestEpisodeItemListBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutNativeAdBinding;
import com.anime.rashon.speed.loyert.model.Episode;

import java.util.List;

public class LatestEpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = LatestEpisodesAdapter.class.getSimpleName();
    private Activity mContext;
    private List<Episode> episodeList;
    private SQLiteDatabaseManager sqLiteDatabaseManager;

    private final int albumView = 1;
    private final int nativeAdView = 2;
    boolean isGrid ;

    public LatestEpisodesAdapter(Activity mContext, List<Episode> episodeList ,boolean isGrid) {
        this.mContext = mContext;
        this.episodeList = episodeList;
        this.isGrid = isGrid ;
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
        ViewDataBinding binding;

        if (isGrid) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_latest_episode_item, parent, false);
            return new EpisodeHolder((LayoutLatestEpisodeItemBinding) binding);
        }
        else {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_latest_episode_item_list, parent, false);
            return new EpisodeHolder((LayoutLatestEpisodeItemListBinding) binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {


        if(holder.getItemViewType() == nativeAdView){
            LatestEpisodesAdapter.NativeAdHolder nativeAdHolder = (LatestEpisodesAdapter.NativeAdHolder) holder;
            Config.loadNativeAd(mContext, nativeAdHolder.mBinding.nativeAdTemplate);

//            FacebookNativeAdHolder nativeAdHolder = (FacebookNativeAdHolder) holder;
//            Config.showFacebookNativeAd(mContext, nativeAdHolder.mBinding.fbNativeAd);

        }

        else {

            final EpisodeHolder episodeHolder = (EpisodeHolder) holder;
            final Episode episode = episodeList.get(position);

            holder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext , R.anim.anim_itemview));
            if (isGrid) {
                LayoutLatestEpisodeItemBinding gridBinding = ((EpisodeHolder) holder).gridBinding ;
                if (episode.getThumb().isEmpty())
                    gridBinding.setThumb(episode.getCartoon().getThumb());
                else
                    gridBinding.setThumb(episode.getThumb());

                gridBinding.setCartoon(episode.getCartoon().getTitle());

                if (!TextUtils.isEmpty(episode.getTitle())) {
                   gridBinding.setTitle(episode.getTitle());
                } else {
                    gridBinding.setTitle("الحلقة : " + (position + 1));
                }

                if (sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

        /*episodeHolder.itemView.setOnClickListener(v -> ((MainActivity)mContext).startVideoActivity(position, episode, episodeHolder.mBinding.getTitle(),
                episode.getThumb(), "", ""));*/


            }
            else {
                LayoutLatestEpisodeItemListBinding listBinding = ((EpisodeHolder) holder).listBinding ;
                if (episode.getThumb().isEmpty())
                    listBinding.setThumb(episode.getCartoon().getThumb());
                else
                    listBinding.setThumb(episode.getThumb());

                listBinding.setCartoon(episode.getCartoon().getTitle());

                if (!TextUtils.isEmpty(episode.getTitle())) {
                    listBinding.setTitle(episode.getTitle());
                } else {
                    listBinding.setTitle("الحلقة " + (position + 1));
                }

                if (sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
                   listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }
            }
            episodeHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, EpisodesActivity.class);
                intent.putExtra("cartoon", episode.getCartoon());
                intent.putExtra("playlist", episode.getPlaylist());
                mContext.startActivity(intent);
            });
        }
    }


    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if((position+1)%10 == 0){
            return nativeAdView;
        }
        else
        return albumView;
    }

    private static class EpisodeHolder extends RecyclerView.ViewHolder{

        LayoutLatestEpisodeItemBinding gridBinding;
        LayoutLatestEpisodeItemListBinding listBinding ;

        public EpisodeHolder(LayoutLatestEpisodeItemBinding Binding) {
            super(Binding.getRoot());

            gridBinding = Binding ;
        }

        public EpisodeHolder(LayoutLatestEpisodeItemListBinding Binding) {
            super(Binding.getRoot());

            listBinding = Binding;
        }
    }

    private static class NativeAdHolder extends RecyclerView.ViewHolder{

        LayoutNativeAdBinding mBinding;

        NativeAdHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }



}
