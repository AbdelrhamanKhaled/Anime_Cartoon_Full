package com.app.cartoons.anime.newappcartoons.mydatabind;

import android.content.Context;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cartoons.anime.newappcartoons.adapters.CartoonsAdapter;
import com.app.cartoons.anime.newappcartoons.adapters.CartoonsFavoriteAdapter;
import com.app.cartoons.anime.newappcartoons.adapters.DownloadsAdapter;
import com.app.cartoons.anime.newappcartoons.adapters.EpisodesAdapter;
import com.app.cartoons.anime.newappcartoons.adapters.FavoritesAdapter;
import com.app.cartoons.anime.newappcartoons.adapters.PlaylistsAdapter;
import com.app.cartoons.anime.newappcartoons.model.Cartoon;
import com.app.cartoons.anime.newappcartoons.model.Download;
import com.app.cartoons.anime.newappcartoons.model.Episode;
import com.app.cartoons.anime.newappcartoons.model.Favorite;
import com.app.cartoons.anime.newappcartoons.model.Playlist;

import java.util.List;

public class RecyclerviewBindingAdapters {

    @BindingAdapter("cartoonListBinding")
    public static void setCartoonList(RecyclerView recyclerView, List<Cartoon> cartoonList)
    {
        Context context = recyclerView.getContext();

        if(cartoonList == null){
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if(cartoonList.get(position).getId() == 0){
                        return 3;
                    }
                    return 1;
                }
            });
        }

        CartoonsAdapter adapter = (CartoonsAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new CartoonsAdapter(context, cartoonList);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
        }
    }

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

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        PlaylistsAdapter adapter = (PlaylistsAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new PlaylistsAdapter(context, playlistList);
            recyclerView.setAdapter(adapter);
        }
    }

    @BindingAdapter("episodeListBinding")
    public static void setEpisodeList(RecyclerView recyclerView, List<Episode> episodeList)
    {
        Context context = recyclerView.getContext();

        if(episodeList == null){
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(gridLayoutManager);
        }

        EpisodesAdapter adapter = (EpisodesAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new EpisodesAdapter(context, episodeList);
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


    @BindingAdapter("downloadListBinding")
    public static void setDownloadList(RecyclerView recyclerView, List<Download> downloadList)
    {
        Context context = recyclerView.getContext();

        if(downloadList == null){
            return;
        }

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager == null){
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        DownloadsAdapter adapter = (DownloadsAdapter) recyclerView.getAdapter();
        if(adapter == null){
            adapter = new DownloadsAdapter(context, downloadList);
            recyclerView.setAdapter(adapter);
        }
    }
}
