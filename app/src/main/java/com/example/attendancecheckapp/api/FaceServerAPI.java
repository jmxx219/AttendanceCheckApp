package com.example.attendancecheckapp.api;

import com.example.attendancecheckapp.data.LoginRequest;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface FaceServerAPI {
    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("")
    Call<String> geTest();

    @Headers({ "Content-Type: application/json;charset=UTF-8"})
    @GET("image")
    Call<ResponseBody> getUserImage();
}
