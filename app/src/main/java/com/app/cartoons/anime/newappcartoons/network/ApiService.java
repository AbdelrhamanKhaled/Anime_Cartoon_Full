package com.app.cartoons.anime.newappcartoons.network;

import com.app.cartoons.anime.newappcartoons.model.Admob;
import com.app.cartoons.anime.newappcartoons.model.Cartoon;
import com.app.cartoons.anime.newappcartoons.model.Episode;
import com.app.cartoons.anime.newappcartoons.model.Playlist;
import com.app.cartoons.anime.newappcartoons.model.Redirect;
import com.app.cartoons.anime.newappcartoons.model.Report;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("cartoon/readPaging.php")
    Single<List<Cartoon>> getCartoons(
            @Query("page") int pageNumber
    );

    @GET("cartoon/readPagingByType.php")
    Single<List<Cartoon>> getCartoonsByType(
            @Query("page") int pageNumber,
            @Query("type") int type
    );

    @FormUrlEncoded
    @POST("playlist/read.php")
    Single<List<Playlist>> getPlaylists(
            @Field("cartoon_id") int cartoonId
    );

    @FormUrlEncoded
    @POST("episode/readPaging.php")
    Single<List<Episode>> getEpisodes(
            @Field("playlist_id") int playlistId,
            @Query("page") int pageNumber
    );

    @POST("report/create.php")
    Single<String> createReport(
            @Body Report report
    );

    @GET("admob/readOne.php")
    Single<Admob> getAdmob(
    );

    @GET("cartoon/search.php")
    Single<List<Cartoon>> searchCartoons(
            @Query("s") String keyword
    );

    @GET("episode/search.php")
    Single<List<Episode>> searchEpisodes(
            @Query("s") String keyword,
            @Query("playlistId") int playlistId
    );

    @GET("redirect/readOneV16.php")
    Single<Redirect> getRedirect(
    );

    @GET("message/readOne.php")
    Single<Redirect> getMessage(
    );
}
