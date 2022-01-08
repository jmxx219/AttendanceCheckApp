package com.example.attendancecheckapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private final String BASE_URL = "http://15.164.68.238:8080";
    private MyAPI mMyAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
    }

    public void subjectListClick(View view) {
        UserLectureResponse();

//        Intent intent = new Intent(HomeActivity.this, SubjectListActivity.class);
//        startActivity(intent);
    }

    private void UserLectureResponse() {
        initMyAPI(BASE_URL);
        Log.d(TAG, "User Lecture");

        String userId = "2";
        String token = PreferenceManager.getString(getApplicationContext(), "token");

        Log.d(TAG, userId + " : " + token);

        Call<List<Lecture>> postCall = mMyAPI.getUserLecture(userId, token);
        postCall.enqueue(new Callback<List<Lecture>>() {
            @Override
            public void onResponse(Call<List<Lecture>> call, Response<List<Lecture>> response) {
                if (response.isSuccessful()) {
                    List<Lecture> res = response.body();
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "유저 강의 목록");
                    for(Lecture le : res){
                        Log.d(TAG, le.getId() +", " + le.getLectureId() + ", " + le.getLectureRoom());
                    }

                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
                    Log.d(TAG, call.request().body().toString());
                }
            }

            @Override
            public void onFailure(Call<List<Lecture>> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

    }


    private void initMyAPI(String baseUrl) {
        Log.d(TAG, "initMyAPI : " + baseUrl);

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mMyAPI = retrofit.create(MyAPI.class);
    }
}