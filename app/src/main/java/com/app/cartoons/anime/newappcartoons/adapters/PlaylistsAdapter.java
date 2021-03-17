package com.app.cartoons.anime.newappcartoons.adapters;

import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.cartoons.anime.newappcartoons.R;
import com.app.cartoons.anime.newappcartoons.activities.PlayListsActivity;
import com.app.cartoons.anime.newappcartoons.databinding.LayoutRecyclerplaylistItemBinding;
import com.app.cartoons.anime.newappcartoons.model.Playlist;

import java.util.List;

public class PlaylistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = PlaylistsAdapter.class.getSimpleName();
    private Context mContext;
    private List<Playlist> playlistList;
    private String cartoonTitle;

    public PlaylistsAdapter(Context mContext, List<Playlist> playlistList) {
        this.mContext = mContext;
        this.playlistList = playlistList;
        cartoonTitle = ((PlayListsActivity)mContext).getCartoonTitle();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclerplaylistItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_recyclerplaylist_item, parent, false);

        return new PlaylistHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        PlaylistHolder playlistHolder = (PlaylistHolder) holder;
        final Playlist playlist = playlistList.get(position);
        playlistHolder.mBinding.setPlaylist(playlist);

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

        LayoutRecyclerplaylistItemBinding mBinding;

        public PlaylistHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
