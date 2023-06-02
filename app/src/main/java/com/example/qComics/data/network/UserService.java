package com.example.qComics.data.network;

import com.example.qComics.data.network.auth.LoginRequest;
import com.example.qComics.data.network.auth.LoginResponse;
import com.example.qComics.data.network.auth.RegisterRequest;
import com.example.qComics.data.network.auth.RegisterResponse;
import com.example.qComics.data.network.auth.User;
import com.example.qComics.data.network.auth.Validate;
import com.example.qComics.data.network.comics.Chapter;
import com.example.qComics.data.network.comics.ChapterStatus;
import com.example.qComics.data.network.comics.Comics;
import com.example.qComics.data.network.comics.Filter;
import com.example.qComics.data.network.comics.Images;
import com.example.qComics.data.network.comics.Map;
import com.example.qComics.data.network.comics.RequestChapterStatus;
import com.example.qComics.data.network.comics.RequestType;
import com.example.qComics.data.network.comics.SaveRequest;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserService {

    @POST("/api/v1/auth/authenticate")
    Call<LoginResponse> userLogin(@Body LoginRequest loginRequest);

    @POST("/api/v1/auth/register")
    Call<ResponseBody> registration(@Body RegisterRequest registerRequest);

    @POST("/api/v1/auth/validate")
    Call<ResponseBody> validate(@Body Validate validate);

    @GET("/api/v1/users/usernames/{username}")
    Call<User> getCurrentUser(@Path("username") String username);

    @GET("/api/v1/users/subscribers/amount/usernames/{username}")
    Call<String> getSubscriberAmount(@Path("username") String username);

    @GET("/api/v1/users/subscriptions/usernames/{username}")
    Call<ArrayList<User>> getSubscriptions(@Path("username") String username);

    @POST("/api/v1/users/subscribe/username")
    Call<String> subscribe(@Query("subscriberUsername") String subscriberUsername,
                           @Query("userToSubscribeUsername") String userToSubscribeUsername);

    @POST("/api/v1/users/update")
    Call<ResponseBody> updateUser(@Body User user);

    @GET("api/v1/comics/all")
    Call<ArrayList<Comics>> getAllComics();

    @GET("/api/v1/comics/{name}")
    Call<Comics> getComic(@Path("name") String comicsName);

    @POST("/api/v1/comics/findAll/filter")
    Call<ArrayList<Comics>> filteredComics(@Body Filter filteredRequest);

    @POST("/api/v1/comics/findAll/map")
    Call<ArrayList<Comics>> mapComics(@Body RequestType requestType,
                                      @Query("pageable") Map map);

    @POST("/api/v1/bookmarks/add")
    Call<ResponseBody> addToFavorites(@Body SaveRequest saveRequest);

    @GET("/api/v1/bookmarks/all")
    Call<ArrayList<Comics>> getFavorites(@Query("username") String username);

    @GET("/api/v1/bookmarks/check")
    Call<Boolean> getFavoriteStatus(@Query("username") String username,
                                         @Query("comicName") String comicName);

    @GET("/api/v1/chapters/comic-names/{comicName}")
    Call<ArrayList<Chapter>> getChapters(@Path("comicName") String comicsName);

    @GET("/api/v1/images/all")
    Call<ArrayList<Images>> getImages(@Query("chapterName") String chapterName,
                                      @Query("comicName") String comicName);

    @GET("/api/v1/reading-status")
    Call<ChapterStatus> getChapterStatus(@Query("username") String username,
                                         @Query("chapterId") Integer chapterId);

    @POST("/api/v1/reading-status/read")
    Call<ResponseBody> makeRead(@Body RequestChapterStatus requestChapterStatus);
}
