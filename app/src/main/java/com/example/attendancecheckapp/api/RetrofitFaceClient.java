package com.example.attendancecheckapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitFaceClient {
//    private static final String BASE_URL = "15.164.221.137:5000";
    private static final String BASE_URL = "http://192.168.0.17:80/";


    public static FaceServerAPI getApiService(){return getInstance().create(FaceServerAPI.class);}

    private static Retrofit getInstance(){
        Gson gson = new GsonBuilder().setLenient().create();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }
}
