package com.example.attendancecheckapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    String userType;
    TextView uName;
    TextView uType;
    TextView stNummber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        userType = PreferenceManager.getString(getApplicationContext(), "userType");
        if(userType.equals("0")) setContentView(R.layout.activity_home_sutdent);

        uName = findViewById(R.id.userName);
        uType = findViewById(R.id.userType);
        stNummber = findViewById(R.id.stNumber);

        getUserResponse();
    }

    public void subjectListClick(View view) {
        Intent intent = new Intent(HomeActivity.this, LectureListActivity.class);
        startActivity(intent);
    }

    private void getUserResponse() {
        Log.d(TAG, "User GET");

        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<User> postCall = RetrofitClient.getApiService().getUser(token);
        postCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User res = response.body();

                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "유저 정보 불러오기");
                    uName.setText(res.getName());
                    uType.setText(res.getUser_type());
                    if(userType.equals("0"))  stNummber.setText(res.getSchool_number());

                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
//                    Log.d(TAG, call.request().body().toString());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

    }


}