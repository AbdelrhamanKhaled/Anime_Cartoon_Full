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

import com.anime.rashon.speed.loyert.Constants.Constants;
import com.anime.rashon.speed.loyert.Database.SQLiteDatabaseManager;
import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.InformationActivity;
import com.anime.rashon.speed.loyert.databinding.LayoutLatestEpisodeItemBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutLatestEpisodeItemListBinding;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;

import java.util.List;

public class LatestEpisodesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = LatestEpisodesAdapter.class.getSimpleName();
    private Activity mContext;
    private List<EpisodeWithInfo> episodeList;
    private SQLiteDatabaseManager sqLiteDatabaseManager;

    boolean isGrid ;

    public LatestEpisodesAdapter(Activity mContext, List<EpisodeWithInfo> episodeList ,boolean isGrid) {
        this.mContext = mContext;
        this.episodeList = episodeList;
        this.isGrid = isGrid ;
        sqLiteDatabaseManager = new SQLiteDatabaseManager(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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

            final EpisodeHolder episodeHolder = (EpisodeHolder) holder;
            final EpisodeWithInfo episode = episodeList.get(position);
            if (episode==null) return;
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
                    gridBinding.setTitle("???????????? : " + (position + 1));
                }

                if (sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    episodeHolder.gridBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

        /*episodeHolder.itemView.setOnClickListener(v -> ((MainActivity)mContext).startVideoActivity(position, episode, episodeHolder.mBinding.getTitle(),
                episode.getThumb(), "", ""));*/

                   episodeHolder.gridBinding.rate.setText(String.valueOf(episode.getWorld_rate()));
                switch (episode.getStatus()) {
                    case 1:
                        episodeHolder.gridBinding.statues.setText("??????????");
                        break;

                    case 2:
                        episodeHolder.gridBinding.statues.setText("??????????");
                        break;
                    default:
                        episodeHolder.gridBinding.statues.setText("?????? ????????");
                }
            }
            else {
                LayoutLatestEpisodeItemListBinding listBinding = ((EpisodeHolder) holder).listBinding ;
                if (episode.getThumb().isEmpty())
                    listBinding.setThumb(episode.getCartoon().getThumb());
                else
                    listBinding.setThumb(episode.getThumb());

                listBinding.setCartoon(episode.getCartoon().getTitle());

                if (episode.getCartoon().getType() == Constants.IS_FILM) {
                    listBinding.type.setText("????????");
                    listBinding.title.setVisibility(View.GONE);
                }
                  else {
                    listBinding.type.setText("??????????");
                    listBinding.title.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(episode.getTitle())) {
                        listBinding.setTitle(episode.getTitle());
                    } else {
                        listBinding.setTitle("???????????? " + (position + 1));
                    }
                }

                listBinding.date.setText(episode.getCartoon().getView_date());


                if (sqLiteDatabaseManager.isEpisodeSeen(episode.getId())) {
                   listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eye));
                } else {
                    listBinding.seenImg.setImageDrawable(mContext.getResources().getDrawable(R.drawable.unseen));
                }

                listBinding.rate.setText(String.valueOf(episode.getWorld_rate()));
                switch (episode.getStatus()) {
                    case 1:
                        listBinding.statues.setText("??????????");
                        break;

                    case 2:
                        listBinding.statues.setText("??????????");
                        break;
                    default:
                        listBinding.statues.setText("?????? ????????");
                }
            }
            episodeHolder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(mContext, InformationActivity.class);
                intent.putExtra("cartoon", episode.getCartoon());
//                intent.putExtra("playlist", episode.getPlaylist());
                mContext.startActivity(intent);
            });
        }


    @Override
    public int getItemCount() {
        return episodeList.size();
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

}
