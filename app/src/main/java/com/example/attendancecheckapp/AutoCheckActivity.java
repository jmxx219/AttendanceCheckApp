package com.example.attendancecheckapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoCheckActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    TextView lectureName;
    String lectureId;
    String lectureInfoId;
    String dayOfWeek;
    String week;
    Boolean isAutoCheck;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_check);

        lectureName = findViewById(R.id.autoCheckName);

        Intent it = getIntent();
        lectureId = it.getStringExtra("lectureId");
        lectureInfoId = it.getStringExtra("lectureInfoId");
        dayOfWeek = it.getStringExtra("dayOfWeek");
        week = it.getStringExtra("week");

        if(it.getStringExtra("isAutoCheck").equals("true")) isAutoCheck = true;
        else isAutoCheck = false;

        if(it.getStringExtra("lectureName").equals("null"))
            lectureName.setText("현재 출결 가능한 수업이 없습니다.");
        else
            lectureName.setText(it.getStringExtra("lectureName") + "  -  " + week +"주차" + " "  +dayOfWeek + "요일");

        Button checkButton = (Button) findViewById(R.id.check) ;

    }

    public void onAutoCheckClick(View view) {

        if(!isAutoCheck) {
            Log.d(TAG, "현재 출결 가능한 수업이 없습니다.");
            Toast.makeText(AutoCheckActivity.this, "현재 출결 가능한 수업이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "출석 시작");
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<String> postCall = RetrofitClient.getApiService().setAutoCheck(lectureInfoId, week, token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "자동 출석 완료");
                    Toast.makeText(AutoCheckActivity.this, "자동 출석 완료", Toast.LENGTH_SHORT).show();

                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
//                    Toast.makeText(AutoCheckActivity.this, "자동 출석 실패", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
                Log.d(TAG, "자동 출석 실패");
                Toast.makeText(AutoCheckActivity.this, "자동 출석 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}