package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancecheckapp.adapter.TodayLectureListViewAdapter;
import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.LectureInfo;
import com.example.attendancecheckapp.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
        if(userType.equals("STUDENT")) setContentView(R.layout.activity_home_sutdent);

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

        Call<String> postCall = RetrofitClient.getApiService().getUserLecture(token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "오늘 유저 수강 목록");
                    ArrayList<LectureInfo> lectureInfoList = new ArrayList<>();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray lectureArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<lectureArray.length(); i++){
                            JSONObject lectureObject = lectureArray.getJSONObject(i);

                            LectureInfo lectureInfo = new LectureInfo();
                            lectureInfo.setId(lectureObject.getString("lectureInfoId"));
                            lectureInfo.setLectureId(lectureObject.getString("lectureId"));
                            lectureInfo.setLectureName(lectureObject.getString("lectureName"));
                            lectureInfo.setLectureRoom(lectureObject.getString("lectureRoom"));

                            JSONObject lectureTimeObject = lectureObject.getJSONObject("lectureTime");
                            lectureInfo.setDayOfWeek(lectureTimeObject.getString("day_of_week"));
                            lectureInfo.setLectureStart(lectureTimeObject.getString("lecture_start"));
                            lectureInfo.setLectureEnd(lectureTimeObject.getString("lecture_end"));

                            Log.d(TAG, lectureInfo.toString());
                            lectureInfoList.add(lectureInfo);
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(LectureInfo le : lectureInfoList){
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
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

    }

    public void subjectListClick(View view) {
        Intent intent = new Intent(HomeActivity.this, LectureListActivity.class);
        startActivity(intent);
    }

    public void autoCheckClick(View view) {
        Intent intent = new Intent(HomeActivity.this, AutoCheckActivity.class);
        startActivity(intent);
    }

    private void getUserResponse() {
        Log.d(TAG, "User GET");

        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<String> postCall = RetrofitClient.getApiService().getUser(token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "유저 정보 불러오기");

                    User user = new User();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        Log.d(TAG, jsonObject.toString());
                        Log.d(TAG, dataObject.toString());

                        user.setName(dataObject.getString("name"));
                        user.setUser_type(dataObject.getString("user_type"));
                        user.setSchool_number(dataObject.getString("school_number"));
                        Log.d(TAG, user.getSchool_number());

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    uName.setText(user.getName());
                    uType.setText(user.getUser_type());
                    if(userType.equals("STUDENT"))  stNumber.setText(user.getSchool_number());
                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
//                    Log.d(TAG, call.request().body().toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

    }


}