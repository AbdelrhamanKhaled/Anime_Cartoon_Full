package com.anime.rashon.speed.loyert.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anime.rashon.speed.loyert.R;
import com.anime.rashon.speed.loyert.databinding.LayoutRecyclerfavoriteItemBinding;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.Favorite;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = FavoritesAdapter.class.getSimpleName();
    private Context mContext;
    private List<Favorite> favoriteList;

    public FavoritesAdapter(Context mContext, List<Favorite> favoriteList) {
        this.mContext = mContext;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutRecyclerfavoriteItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext), R.layout.layout_recyclerfavorite_item, parent, false);

        return new FavoriteHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        FavoriteHolder favoriteHolder = (FavoriteHolder) holder;
        final Favorite favorite = favoriteList.get(position);
        favoriteHolder.mBinding.setFavorite(favorite);

        favoriteHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(mContext, ExoplayerActivity.class);

                Episode episode = new Episode();
                episode.setVideo(favorite.getVideoUrl());
//                episode.setUid(favorite.getUid());

//                intent.putExtra("episode", episode);
//                intent.putExtra("title", favorite.getTitle());
//                intent.putExtra("thumb", favorite.getImgUrl());
//                intent.putExtra("playlistTitle", favorite.getPlaylistTitle());
//                intent.putExtra("cartoonTitle", favorite.getCartoonTitle());
//                mContext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class FavoriteHolder extends RecyclerView.ViewHolder{

        LayoutRecyclerfavoriteItemBinding mBinding;

        public FavoriteHolder(View itemView) {
            super(itemView);
            mBinding = DataBindingUtil.bind(itemView);
        }
    }
}
