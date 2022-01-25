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

import java.time.LocalTime;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoCheckActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();


    TextView lectureName;

    Calendar rightNow;
    private int day_of_week;
    LocalTime now;

    int currHour;
    String lectureInfoId;
    String week = "1";
    boolean isAutoCheck;


    private String[] days = {"일", "월", "화", "수", "목", "금", "토"};

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_check);

        lectureName = findViewById(R.id.autoCheckName);

        rightNow = Calendar.getInstance();
        day_of_week = rightNow.get(Calendar.DAY_OF_WEEK) - 1;

        now = LocalTime.now();
        currHour = now.getHour();

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
//                            currHour = 4;
                            if(days[day_of_week].equals(le.getDayOfWeek()) && currHour < lectureStartHour) {
                                lectureName.setText(le.getLectureName() + "  -  " + le.getDayOfWeek() + "요일");
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
}