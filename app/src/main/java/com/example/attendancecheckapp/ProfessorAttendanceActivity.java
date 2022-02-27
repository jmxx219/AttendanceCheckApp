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
import com.example.attendancecheckapp.data.LectureInfo;
import com.example.attendancecheckapp.data.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorAttendanceActivity extends AppCompatActivity {
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

        Log.d(TAG, "dd");

        context = this;
        attendance_view = (ListView) findViewById(R.id.attendanceStudentListView);
        adapter = new AttendanceListViewAdapter();
        attendance_view.setAdapter(adapter);

        lectureName = (TextView) findViewById(R.id.lectureAttendanceName);

        Intent it = getIntent();
        lectureName.setText(it.getStringExtra("lectureName"));
        lectureId = it.getStringExtra("lectureId");

        userType = PreferenceManager.getString(getApplicationContext(), "userType");

        Log.d(TAG, lectureName.getText().toString() +", " + lectureId);

        showProfessorLectureAttendance();

        attendance_view.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getApplicationContext(), ManualCheckActivity.class);

            // intent 객체에 데이터를 실어서 보내기
            Attendance item = (Attendance) adapter.getItem(position);
            intent.putExtra("lectureId", lectureId);
            intent.putExtra("lectureName", lectureName.getText());
            intent.putExtra("lectureInfoId", item.getId());
            intent.putExtra("dayOfWeek", item.getDayOfWeek());
            intent.putExtra("week", item.getWeek());

            startActivity(intent);
        });
    }


    private void showProfessorLectureAttendance() {
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        Call<String> postCall = RetrofitClient.getApiService().getLecture(lectureId, token);
        Log.d(TAG, "tt");
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status code : " + response.code());
                    Log.d(TAG, "강의 주차 정보");

                    ArrayList<Attendance> attendanceList = new ArrayList<>();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject lectureObject = jsonObject.getJSONObject("data");
                        JSONArray lectureInfoArray = lectureObject.getJSONArray("lecture_info");
                        for(int j=0; j<lectureInfoArray.length(); j++) {
                            JSONObject lectureInfoObject = lectureInfoArray.getJSONObject(j);
                            JSONObject lectureTimeObject = lectureInfoObject.getJSONObject("lecture_time");
                            for(int w =0; w < 14; w++) {
                                Attendance attendance = new Attendance();
                                attendance.setId(lectureInfoObject.getString("lecture_info_id"));
                                attendance.setWeek(String.valueOf(w + 1));
                                attendance.setDayOfWeek(lectureTimeObject.getString("day_of_week"));
                                attendance.setIsAttend("");

                                attendanceList.add(attendance);
                            }

                        }


                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Collections.sort(attendanceList);

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
