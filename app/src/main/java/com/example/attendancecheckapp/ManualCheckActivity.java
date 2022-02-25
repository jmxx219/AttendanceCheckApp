package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.ManualCheck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualCheckActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public static Context context;

    private ListView attendance_view;
    ManualCheckListViewAdapter adapter;

    private TextView lectureName;
    String lectureId;
    String lectureInfoId;
    String dayOfWeek;
    String week;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_check);

        context = this;
        attendance_view = (ListView) findViewById(R.id.manualAttendanceStudentListView);
        adapter = new ManualCheckListViewAdapter();
        attendance_view.setAdapter(adapter);

        lectureName = (TextView) findViewById(R.id.manualAttendanceName);

        Intent it = getIntent();
        lectureId = it.getStringExtra("lectureId");
        lectureInfoId = it.getStringExtra("lectureInfoId");
        dayOfWeek = it.getStringExtra("dayOfWeek");
        week = it.getStringExtra("week");

        if(it.getStringExtra("lectureName").equals("null"))
            lectureName.setText("현재 출결 가능한 수업이 없습니다.");
        else
            lectureName.setText(it.getStringExtra("lectureName") + "  -  " + week +"주차" + " "  +dayOfWeek + "요일");

        showManualAttendance();
    }

    private void showManualAttendance() {
        Log.d(TAG, "Manual Attendance");

        String userId = PreferenceManager.getString(getApplicationContext(), "userId");
        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");
        Log.d(TAG, userId + " : " + token);

        Call<String> postCall = RetrofitClient.getApiService().getStudentList(lectureInfoId, week, token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "강의를 수강하는 학생 목록");

                    ArrayList<ManualCheck> studentList = new ArrayList<>();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray studentArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<studentArray.length(); i++){
                            JSONObject lectureObject = studentArray.getJSONObject(i);

                            ManualCheck student = new ManualCheck();
                            student.setName(lectureObject.getString("name"));
                            student.setSchoolNumber(lectureObject.getString("school_number"));
                            student.setIsAttend(lectureObject.getString("is_attend"));
                            student.setAttendanceId(lectureObject.getString("attendance_id"));

                            Log.d(TAG, student.toString());
                            studentList.add(student);
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(ManualCheck le : studentList){
                        adapter.addItem(le.getName(), le.getSchoolNumber(), le.getIsAttend(), le.getAttendanceId());
                        adapter.notifyDataSetChanged();
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
