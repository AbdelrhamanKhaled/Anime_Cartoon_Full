package com.anime.rashon.speed.loyert.network;

import com.anime.rashon.speed.loyert.model.Admob;
import com.anime.rashon.speed.loyert.model.Cartoon;
import com.anime.rashon.speed.loyert.model.CartoonWithInfo;
import com.anime.rashon.speed.loyert.model.Episode;
import com.anime.rashon.speed.loyert.model.EpisodeWithInfo;
import com.anime.rashon.speed.loyert.model.Feedbacks;
import com.anime.rashon.speed.loyert.model.Information;
import com.anime.rashon.speed.loyert.model.Playlist;
import com.anime.rashon.speed.loyert.model.Redirect;
import com.anime.rashon.speed.loyert.model.Report;
import com.anime.rashon.speed.loyert.model.UserResponse;

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

    @FormUrlEncoded
    @POST ("Accses/LoginWithEmail.php")
    Single<UserResponse> loginWithEmail (
            @Field("email") String email ,
            @Field("password") String password
    );


    @FormUrlEncoded
    @POST ("Accses/RegisterWithEmail.php")
    Single<UserResponse> createNewUserWithEmail (
            @Field("email") String email ,
            @Field("password") String password ,
            @Field("name") String name ,
            @Field("photo_Uri") String photoUrl
    );

    @FormUrlEncoded
    @POST ("Accses/RegisterWithToken.php")
    Single<UserResponse> createNewUserWithToken (
            @Field("token") String token ,
            @Field("email") String email ,
            @Field("name") String name ,
            @Field("photo_Uri") String photo_Uri
    );

    @GET("UserLoggedOptions/getAllFavouriteCartoons.php")
    Single<List<CartoonWithInfo>> getAllFavouriteCartoons(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/getAllWatchedCartonns.php")
    Single<List<CartoonWithInfo>> getAllWatchedCartoons(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/getAllWatchedLaterCartoons.php")
    Single<List<CartoonWithInfo>> getAllWatchedLaterCartoons(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/getAllSeenEpisodes.php")
    Single<List<Integer>> getAllSeenEpisodes(
            @Query("user_id") int userId
    );

    @GET("UserLoggedOptions/addFavourite.php")
    Single<UserResponse> addFavourite(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/removeFavourite.php")
    Single<UserResponse> deleteFavourite(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/addwatchedCartoon.php")
    Single<UserResponse> addWatchedCartoon(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/removeWatchedCartoon.php")
    Single<UserResponse> removeWatchedCartoon(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/addWatchedLaterCartoon.php")
    Single<UserResponse> addWatchedLaterCartoon(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );

    @GET("UserLoggedOptions/removeWatchLater.php")
    Single<UserResponse> removeWatchLater(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoonId
    );


    @GET("UserLoggedOptions/getCartoonFeedbacks.php")
    Single<List<Feedbacks>> getFeedbacks(
            @Query("cartoon_id") int cartoonId
    );


    @GET("UserLoggedOptions/insertSeenEpisode.php")
    Single<UserResponse> insertSeenEpisode(
            @Query("user_id") int userId,
            @Query("episode_id") int episodeId
    );

    @GET("UserLoggedOptions/incrementWatchedEpisodes.php")
    Single<UserResponse> incrementWatchedEpisodes(
            @Query("user_id") int userId
    );


    @GET("UserLoggedOptions/addCartoonFeedback.php")
    Single<UserResponse> addCartoonFeedback(
            @Query("user_id") int userId,
            @Query("cartoon_id") int cartoon_id,
            @Query("feedback") String feedback,
            @Query("name") String name,
            @Query("photo_Uri") String photo_Uri
    );


}
