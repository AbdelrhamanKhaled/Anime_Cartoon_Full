package com.anime.rashon.speed.loyert.mydatabind;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anime.rashon.speed.loyert.adapters.CartoonsFavoriteAdapter;
import com.anime.rashon.speed.loyert.adapters.FavoritesAdapter;
import com.anime.rashon.speed.loyert.adapters.PlaylistsAdapter;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.Favorite;
import com.anime.rashon.speed.loyert.model.Playlist;

import java.util.List;

public class RecyclerviewBindingAdapters {

    @BindingAdapter("favoriteCartoonListBinding")
    public static void setFavoriteCartoonList(RecyclerView recyclerView, List<Cartoon> cartoonList)
    {
        Context context = recyclerView.getContext();

        if(cartoonList == null){
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        CartoonsFavoriteAdapter adapter = (CartoonsFavoriteAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new CartoonsFavoriteAdapter(context, cartoonList);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
    }

    @BindingAdapter("playlistListBinding")
    public static void setPlaylistList(RecyclerView recyclerView, List<Playlist> playlistList)
    {
        Context context = recyclerView.getContext();

        if(playlistList == null){
            return;
        }

        PlaylistsAdapter adapter = (PlaylistsAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new PlaylistsAdapter(context, playlistList , true);
            recyclerView.setAdapter(adapter);
        }
    }

    @BindingAdapter("favoriteListBinding")
    public static void setFavoriteList(RecyclerView recyclerView, List<Favorite> favoriteList)
    {
        Context context = recyclerView.getContext();

        if(favoriteList == null){
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        FavoritesAdapter adapter = (FavoritesAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new FavoritesAdapter(context, favoriteList);
            recyclerView.setAdapter(adapter);
        }
    }

}
