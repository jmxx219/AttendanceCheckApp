package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancecheckapp.adapter.TodayLectureListViewAdapter;
import com.example.attendancecheckapp.api.JWTUtils;
import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.LectureInfo;
import com.example.attendancecheckapp.data.PreferenceManager;
import com.example.attendancecheckapp.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
    LocalTime nowTime;
    LocalDate nowDate;

    int currHour;
    String week;
    String currDate;

    String lectureId;
    String lectureInfoId;
    String lectureName;
    boolean isAutoCheck;

    private final String START_DATE = "20220302"; // 1학기 개강 일자
//    private final String START_DATE = "20220901"; // 2학기 개강 일자

    private String[] days = {"일", "월", "화", "수", "목", "금", "토"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        userType = PreferenceManager.getString(getApplicationContext(), "userType");
        Log.d(TAG, userType.toString());
        if(userType.equals("STUDENT")) setContentView(R.layout.activity_home_sutdent);

        uName = findViewById(R.id.userName);
        uType = findViewById(R.id.userType);
        stNumber = findViewById(R.id.stNumber);

        context = this;
        today_lecture_view = (ListView) findViewById(R.id.todayLectureListView);
        adapter = new TodayLectureListViewAdapter();
        today_lecture_view.setAdapter(adapter);

        lectureName = "null";

        rightNow = Calendar.getInstance();
//        day_of_week = rightNow.get(Calendar.DAY_OF_WEEK) - 1;
        day_of_week = 2; // test
        Log.d(TAG, "요일 " + day_of_week);

        nowTime = LocalTime.now();
        currHour = nowTime.getHour();

        // 현재 날짜 구하기
        nowDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
//        currDate = nowDate.format(formatter);
        currDate = "20220401"; // test

        Log.d(TAG, getDateWeekOfYear(START_DATE));
        Log.d(TAG, getDateWeekOfYear(currDate));

        // 주차 계산 : 현재 주차 - 개강일(3월 첫째주 or 9월 첫째주) 주차 + 1
        week = String.valueOf(Integer.valueOf(getDateWeekOfYear(currDate)) - Integer.valueOf(getDateWeekOfYear(START_DATE)) + 1);
        Log.d(TAG, week + "주차");

        getUserResponse();
        getTodayLectureResponse();

    }

    public void subjectListClick(View view) {
        Intent intent = new Intent(HomeActivity.this, LectureListActivity.class);
        startActivity(intent);
    }

    public void autoCheckClick(View view) {
        Intent intent = new Intent(HomeActivity.this, AutoCheckActivity.class);

        intent.putExtra("lectureId", lectureId);
        intent.putExtra("lectureName", lectureName);
        intent.putExtra("lectureInfoId", lectureInfoId);
        intent.putExtra("dayOfWeek", days[day_of_week]);
        intent.putExtra("week", week);
        intent.putExtra("isAutoCheck", String.valueOf(isAutoCheck));

        startActivity(intent);
    }

    public void manualCheckClick(View view) {
        Intent intent = new Intent(HomeActivity.this, ManualCheckActivity.class);

        intent.putExtra("lectureId", lectureId);
        intent.putExtra("lectureName", lectureName);
        intent.putExtra("lectureInfoId", lectureInfoId);
        intent.putExtra("dayOfWeek", days[day_of_week]);
        intent.putExtra("week", week);

        startActivity(intent);
    }

    public void addImageClick(View view) {
        Intent intent = new Intent(HomeActivity.this, AddImageActivity.class);
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
                        user.setUserType(dataObject.getString("userType"));
                        user.setSchoolNumber(dataObject.getString("schoolNumber"));
                        PreferenceManager.setString(getApplicationContext(), "userSchoolNumber", user.getSchoolNumber());
                        Log.d(TAG, user.getSchoolNumber());


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                    uName.setText(user.getName());
                    uType.setText(user.getUserType());
                    if(userType.equals("STUDENT"))  stNumber.setText(user.getSchoolNumber());
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

                            LectureInfo le = new LectureInfo();
                            le.setId(lectureObject.getString("lectureInfoId"));
                            le.setLectureId(lectureObject.getString("lectureId"));
                            le.setLectureName(lectureObject.getString("lectureName"));
                            le.setLectureRoom(lectureObject.getString("lectureRoom"));

                            JSONObject lectureTimeObject = lectureObject.getJSONObject("lectureTime");
                            le.setDayOfWeek(lectureTimeObject.getString("day_of_week"));
                            le.setLectureStart(lectureTimeObject.getString("lecture_start"));
                            le.setLectureEnd(lectureTimeObject.getString("lecture_end"));


                            int lectureStartHour = Integer.valueOf(le.getLectureStart().substring(0, 2));
                            currHour = 2; // test
                            if(!isAutoCheck && days[day_of_week].equals(le.getDayOfWeek()) && currHour <= lectureStartHour && Integer.valueOf(week) > 0 && Integer.valueOf(week) <= 14) {
                                lectureName = le.getLectureName();
                                lectureInfoId = le.getId();
                                lectureId = le.getLectureId();
                                isAutoCheck = true;
                                Log.d(TAG, le.toString());
                            }

                            Log.d(TAG, le.toString());
                            lectureInfoList.add(le);
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

    public String getDateWeekOfYear(String yyyyMMdd) {
        String weekOfYear = "";
        try {
            /** 20201231 기존 53주차(Calendar) > 1주차 표기 (GregorianCalendar)
             *  20210101 기존 1주차(Calendar) > 1주차 표기 (GregorianCalendar)
             */
            GregorianCalendar cal = new GregorianCalendar();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date date = format.parse(yyyyMMdd);
            cal.setTime(date);
            int dayNum = cal.get(Calendar.DAY_OF_WEEK);
            // 주말인지 구한다. 1이면 일요일 7이면 토요일 - 일요일 경우 저번주 주차 가져오기
            if(dayNum == 1) cal.add(Calendar.DATE, -7);
            weekOfYear = Integer.toString(cal.get(Calendar.WEEK_OF_YEAR));
        } catch (ParseException e) { }
        return weekOfYear;
    }


}