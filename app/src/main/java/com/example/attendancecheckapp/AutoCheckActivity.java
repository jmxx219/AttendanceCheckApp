package com.example.attendancecheckapp;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoCheckActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    TextView lectureName;
    boolean isAutoCheck;

    Calendar rightNow;
    private int day_of_week;
    LocalTime nowTime;
    LocalDate nowDate;

    int currHour;
    String lectureInfoId;
    String week = "1";

    String currDate;

    private final String START_DATE = "20220302";
//    private final String START_DATE = "20220901";

    private String[] days = {"일", "월", "화", "수", "목", "금", "토"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_check);

        lectureName = findViewById(R.id.autoCheckName);

        rightNow = Calendar.getInstance();
        day_of_week = rightNow.get(Calendar.DAY_OF_WEEK) - 1;

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

        Button checkButton = (Button) findViewById(R.id.check) ;

        getTodayLecture();
    }

    private void getTodayLecture() {
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<String> postCall = RetrofitClient.getApiService().getUserLecture(token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "현재 시간 기준 강의 정보");

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray lectureArray = jsonObject.getJSONArray("data");
                        isAutoCheck = false;
                        for(int i=0; i<lectureArray.length(); i++){
                            JSONObject lectureObject = lectureArray.getJSONObject(i);

                            LectureInfo le = new LectureInfo();
                            le.setId(lectureObject.getString("lectureInfoId"));
                            le.setLectureId(lectureObject.getString("lectureId"));
                            le.setLectureName(lectureObject.getString("lectureName"));

                            JSONObject lectureTimeObject = lectureObject.getJSONObject("lectureTime");
                            le.setDayOfWeek(lectureTimeObject.getString("day_of_week"));
                            le.setLectureStart(lectureTimeObject.getString("lecture_start"));

                            int lectureStartHour = Integer.valueOf(le.getLectureStart().substring(0, 2));
                            currHour = 4; // test
                            if(days[day_of_week].equals(le.getDayOfWeek()) && currHour < lectureStartHour) {
                                lectureName.setText(le.getLectureName() + "  -  " + week + "주차 " + le.getDayOfWeek() + "요일");
                                lectureInfoId = le.getId();
                                isAutoCheck = true;
                                Log.d(TAG, le.toString());
                                break;
                            }
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
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
                    Toast.makeText(AutoCheckActivity.this, "자동 출석 실패", Toast.LENGTH_SHORT).show();
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