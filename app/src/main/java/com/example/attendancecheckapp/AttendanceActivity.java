package com.example.attendancecheckapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity  extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private TextView lectureName;
    private TextView text;
    String lectureId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        lectureName = (TextView) findViewById(R.id.lectureAttendanceName);
        text = (TextView) findViewById(R.id.textView7);

        Intent intent = getIntent();
        lectureName.setText(intent.getStringExtra("lectureName"));
        lectureId = intent.getStringExtra("lectureId");
        Log.d(TAG, lectureName.getText().toString() +", " + lectureId);
        showLectureAttendance();
    }

    private void showLectureAttendance() {
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<ArrayList<LectureAttendance>> postCall = RetrofitClient.getApiService().getUserLectureAttend(lectureId, token);
        postCall.enqueue(new Callback<ArrayList<LectureAttendance>>() {
            @Override
            public void onResponse(Call<ArrayList<LectureAttendance>> call, Response<ArrayList<LectureAttendance>> response) {
                if (response.isSuccessful()) {
                    ArrayList<LectureAttendance> res = response.body();
                    Log.d(TAG, "Status code : " + response.code());
                    Log.d(TAG, "유저 강의 출석 현황");
                    for(LectureAttendance le : res){
                        // id, lectureId, lectureRoom, lectureDay, lectureStartTime, lectureEndTime
//                        adapter.addItem(le.getId(), le.getLectureId(), le.getLectureName(), le.getLectureRoom(), le.getDayOfWeek(), le.getLectureStart(), le.getLectureEnd());
//                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "주차: " + le.getWeek() +",  요일: " + le.getDayOfWeek() + ",  출석여부: " + le.getIsAttend());
                    }

                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
//                    Log.d(TAG, call.request().body().getClass().toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<LectureAttendance>> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });
    }
}
