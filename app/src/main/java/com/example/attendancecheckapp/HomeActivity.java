package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    String userType;
    TextView uName;
    TextView uType;
    TextView stNumber;

    public static Context context;
    private ListView today_lecture_view;
    TodayLectureListViewAdapter adapter;

    Calendar rightNow;
    private int day_of_week;

    private String[] days = {"일", "월", "화", "수", "목", "금", "토"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        userType = PreferenceManager.getString(getApplicationContext(), "userType");
        if(userType.equals("0")) setContentView(R.layout.activity_home_sutdent);

        uName = findViewById(R.id.userName);
        uType = findViewById(R.id.userType);
        stNumber = findViewById(R.id.stNumber);

        context = this;
        today_lecture_view = (ListView) findViewById(R.id.todayLectureListView);
        adapter = new TodayLectureListViewAdapter();
        today_lecture_view.setAdapter(adapter);

        rightNow = Calendar.getInstance();
        day_of_week = rightNow.get(Calendar.DAY_OF_WEEK) - 1;
        Log.d(TAG, "요일 " + day_of_week);

        getUserResponse();
        getTodayLectureResponse();
    }

    private void getTodayLectureResponse() {
        Log.d(TAG, "User Lecture");

        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");
        Log.d(TAG, userId + " : " + token);

        Call<ArrayList<Lecture>> postCall = RetrofitClient.getApiService().getUserLecture(userId, token);
        postCall.enqueue(new Callback<ArrayList<Lecture>>() {
            @Override
            public void onResponse(Call<ArrayList<Lecture>> call, Response<ArrayList<Lecture>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Lecture> res = response.body();
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "유저 강의 목록");
                    for(Lecture le : res){
                        // id, lectureId, lectureRoom, lectureDay, lectureStartTime, lectureEndTime
                        if(days[day_of_week].equals(le.getDayOfWeek())) {
                            adapter.addItem(le.getId(), le.getLectureId(), le.getLectureName(), le.getLectureRoom(), le.getDayOfWeek(), le.getLectureStart(), le.getLectureEnd());
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, le.getId() + ", " + le.getLectureId() + ", " + le.getLectureName());

                        }
                    }

                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
//                    Log.d(TAG, call.request().body().getClass().toString());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Lecture>> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

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
                    if(userType.equals("0"))  stNumber.setText(res.getSchool_number());

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