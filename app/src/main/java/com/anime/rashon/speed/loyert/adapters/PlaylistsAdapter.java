package com.anime.rashon.speed.loyert.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.activities.PlayListsActivity;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclerplaylistItemBinding;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclerplaylistItemListBinding;
import com.anime.rashon.speed.loyert.model.Playlist;

import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = PlaylistsAdapter.class.getSimpleName();
    private Context mContext;
    private List<Playlist> playlistList;
    private String cartoonTitle;
    private boolean grid ;

    public PlaylistsAdapter(Context mContext, List<Playlist> playlistList, boolean grid) {
        this.mContext = mContext;
        this.playlistList = playlistList;
        cartoonTitle = ((PlayListsActivity)mContext).getCartoonTitle();
        this.grid = grid ;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewDataBinding binding ;
        if (grid) {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclerplaylist_item, parent, false);
            return new PlaylistHolder((LayoutRecyclerplaylistItemBinding) binding);
        }
        else {
            binding = DataBindingUtil.inflate(
                    LayoutInflater.from(mContext), R.layout.layout_recyclerplaylist_item_list, parent, false);
            return new PlaylistHolder((LayoutRecyclerplaylistItemListBinding) binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        PlaylistHolder playlistHolder = (PlaylistHolder) holder;
        final Playlist playlist = playlistList.get(position);
        holder.itemView.setAnimation(AnimationUtils.loadAnimation(mContext , R.anim.anim_itemview));
        if (grid)
        playlistHolder.gridBinding.setPlaylist(playlist);
        else
        playlistHolder.listBinding.setPlaylist(playlist);
        playlistHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayListsActivity)mContext).openEpisodesActivity(playlist);
            }
        });
    }


    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public class PlaylistHolder extends RecyclerView.ViewHolder{

        LayoutRecyclerplaylistItemBinding gridBinding;
        LayoutRecyclerplaylistItemListBinding listBinding;

        public PlaylistHolder(LayoutRecyclerplaylistItemBinding binding) {
            super(binding.getRoot());
            gridBinding = DataBindingUtil.bind(itemView);
        }
        public PlaylistHolder(LayoutRecyclerplaylistItemListBinding binding) {
            super(binding.getRoot());
            listBinding = DataBindingUtil.bind(itemView);
        }
    }
}
