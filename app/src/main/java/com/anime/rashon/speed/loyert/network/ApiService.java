package com.anime.rashon.speed.loyert.network;

import com.anime.rashon.speed.loyert.model.Admob;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.CartoonWithInfo;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;
import com.anime.rashon.speed.loyert.model.Information;
import com.anime.rashon.speed.loyert.model.Playlist;
import com.anime.rashon.speed.loyert.model.Redirect;
import com.anime.rashon.speed.loyert.model.Report;

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

    @GET("cartoon_with_info/readPaging.php")
    Single<List<CartoonWithInfo>> getCartoonsWithInfo(
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

    @GET("admob/animecartoonafterdelete.php")
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

    @GET("redirect/animelive.php")
    Single<Redirect> getRedirect(
    );

    @GET("message/animelive.php")
    Single<Redirect> getMessage(
    );

    @GET("episode/latest.php")
    Single<List<Episode>> latestEpisodes();

    @GET("episodeWithInfo/latest.php")
    Single<List<EpisodeWithInfo>> latestEpisodesWithInfo();

    @FormUrlEncoded
    @POST("information/readOne.php")
    Single<Information> getCartoonInformation(
            @Field("cartoon_id") int cartoonId
    );

    @GET("episode/dates.php")
    Single<List<EpisodeDate>> episodeDates();

}
