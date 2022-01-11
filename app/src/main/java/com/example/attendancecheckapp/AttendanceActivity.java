package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceActivity  extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public static Context context;

    private ListView attendance_view;
    AttendanceListViewAdapter adapter;

    private TextView lectureName;
    String lectureId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        context = this;
        attendance_view = (ListView) findViewById(R.id.attendanceListView);
        adapter = new AttendanceListViewAdapter();
        attendance_view.setAdapter(adapter);

        lectureName = (TextView) findViewById(R.id.lectureAttendanceName);

        Intent intent = getIntent();
        lectureName.setText(intent.getStringExtra("lectureName"));
        lectureId = intent.getStringExtra("lectureId");

        Log.d(TAG, lectureName.getText().toString() +", " + lectureId);

        showLectureAttendance();
    }

    private void showLectureAttendance() {
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<ArrayList<Attendance>> postCall = RetrofitClient.getApiService().getUserLectureAttend(lectureId, token);
        postCall.enqueue(new Callback<ArrayList<Attendance>>() {
            @Override
            public void onResponse(Call<ArrayList<Attendance>> call, Response<ArrayList<Attendance>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Attendance> res = response.body();
                    Log.d(TAG, "Status code : " + response.code());
                    Log.d(TAG, "유저 강의 출석 현황");
                    for(Attendance at : res){
                        // id, userId, week, lectureDay, isAttend
                        adapter.addItem(at.getId(), at.getUserId(), at.getWeek(), at.getDayOfWeek(), at.getIsAttend());
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "주차: " + at.getWeek() +",  요일: " + at.getDayOfWeek() + ",  출석여부: " + at.getIsAttend());
                    }

                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
//                    Log.d(TAG, call.request().body().getClass().toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Attendance>> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });
    }
}
