package com.example.attendancecheckapp;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/*
1. Http요청을 어노테이션으로 명시

2. URL Parameter와 Query Parameter 사용이 가능하다.
 ex) @GET("/group/{id}/users") Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort)

3. 객체는 Body 로 json형태로 전달한다. url 끝에 / 를 빼먹으면 error 가 발생할 수 있으니 유의
 */

interface MyAPI {

//    @POST("/test/")
//    Call<LoginRequest> post_posts(@Body LoginRequest post);
//
//    @PATCH("/test/{pk}/")
//    Call<LoginRequest> patch_posts(@Path("pk") int pk, @Body LoginRequest post);
//
//    @DELETE("/test/{pk}/")
//    Call<LoginRequest> delete_posts(@Path("pk") int pk);


    /**
     * User Controller
     */
    @POST("user/login")
    Call<LoginResponse> getLoginResponse(@Body LoginRequest loginRequest);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("user/me")
    Call<User> getUser(@Header("Authorization") String auth);


    /**
     * Lecture Controller
     */
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("lecture")
    Call<ArrayList<Lecture>> getUserLecture(@Query("userId") String id, @Header("Authorization") String auth);

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("lecture/student")
    Call<ArrayList<Attendance>> getUserLectureAttend(@Query("lectureId") String id, @Header("Authorization") String auth);

}
