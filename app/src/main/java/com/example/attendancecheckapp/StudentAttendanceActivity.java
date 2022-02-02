package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancecheckapp.adapter.AttendanceListViewAdapter;
import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.Attendance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentAttendanceActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public static Context context;

    private ListView attendance_view;
    AttendanceListViewAdapter adapter;

    private TextView lectureName;
    String lectureId;

    String userType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        context = this;
        attendance_view = (ListView) findViewById(R.id.attendanceStudentListView);
        adapter = new AttendanceListViewAdapter();
        attendance_view.setAdapter(adapter);

        lectureName = (TextView) findViewById(R.id.lectureAttendanceName);

        Intent intent = getIntent();
        lectureName.setText(intent.getStringExtra("lectureName"));
        lectureId = intent.getStringExtra("lectureId");

        userType = PreferenceManager.getString(getApplicationContext(), "userType");

        Log.d(TAG, lectureName.getText().toString() +", " + lectureId);

        showStudentLectureAttendance();
    }

    private void showStudentLectureAttendance() {
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<String> postCall = RetrofitClient.getApiService().getUserLectureAttend(lectureId, token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status code : " + response.code());
                    Log.d(TAG, "유저 강의 출석 현황");

                    ArrayList<Attendance> attendanceList = new ArrayList<>();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray lectureArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<lectureArray.length(); i++){
                            JSONObject lectureObject = lectureArray.getJSONObject(i);

                            Attendance attendance = new Attendance();
                            attendance.setId(lectureObject.getString("attendanceId"));
                            attendance.setWeek(lectureObject.getString("week"));
                            attendance.setDayOfWeek(lectureObject.getString("day_of_week"));
                            attendance.setIsAttend(lectureObject.getString("isAttend"));

                            Log.d(TAG, attendance.toString());
                            attendanceList.add(attendance);
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(Attendance at : attendanceList){
                        // id, userId, week, lectureDay, isAttend
                        adapter.addItem(at.getId(), at.getWeek(), at.getDayOfWeek(), at.getIsAttend());
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
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });
    }
}
