package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.attendancecheckapp.adapter.LectureListViewAdapter;
import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.LectureInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureListActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public static Context context;

    String userType;
    private ListView lecture_view;
    LectureListViewAdapter adapter;
    Map<String, Integer> lecturesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list);

        context = this;
        lecture_view = (ListView) findViewById(R.id.lectureListView);
        adapter = new LectureListViewAdapter();
        lecture_view.setAdapter(adapter);
        userType = PreferenceManager.getString(getApplicationContext(), "userType");

        lecturesId = new HashMap<>();

        UserLectureResponse();

        lecture_view.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent;

            if(userType.equals("STUDENT")) intent = new Intent(getApplicationContext(), StudentAttendanceActivity.class);
            else intent = new Intent(getApplicationContext(), ProfessorAttendanceActivity.class);

            // intent 객체에 데이터를 실어서 보내기
            LectureInfo item = (LectureInfo) adapter.getItem(position);
            intent.putExtra("lectureName", item.getLectureName());
            intent.putExtra("lectureId", item.getLectureId());

            startActivity(intent);
        });
    }


    private void UserLectureResponse() {
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
                    Log.d(TAG, "유저 강의 목록");

                    ArrayList<LectureInfo> lectureInfoList = new ArrayList<>();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray lectureArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<lectureArray.length(); i++){
                            JSONObject lectureObject = lectureArray.getJSONObject(i);

                            String lectureId = lectureObject.getString("lectureId");
                            if(lecturesId.containsKey(lectureId)) {
                                LectureInfo info = lectureInfoList.get(lecturesId.get(lectureId));

                                JSONObject lectureTimeObject = lectureObject.getJSONObject("lectureTime");
                                info.setDayOfWeek(info.getDayOfWeek() + "\n" + lectureTimeObject.getString("day_of_week"));
                                info.setLectureStart(info.getLectureStart() + "\n" + lectureTimeObject.getString("lecture_start"));
                                info.setLectureEnd(info.getLectureEnd() + "\n" + lectureTimeObject.getString("lecture_end"));
                            }
                            else {
                                LectureInfo lectureInfo = new LectureInfo();
                                lectureInfo.setId(lectureObject.getString("lectureInfoId"));
                                lectureInfo.setLectureId(lectureId);
                                lectureInfo.setLectureName(lectureObject.getString("lectureName"));
                                lectureInfo.setLectureRoom(lectureObject.getString("lectureRoom"));

                                JSONObject lectureTimeObject = lectureObject.getJSONObject("lectureTime");
                                lectureInfo.setDayOfWeek(lectureTimeObject.getString("day_of_week"));
                                lectureInfo.setLectureStart(lectureTimeObject.getString("lecture_start"));
                                lectureInfo.setLectureEnd(lectureTimeObject.getString("lecture_end"));

                                lectureInfoList.add(lectureInfo);
                                lecturesId.put(lectureId, lectureInfoList.size() - 1);
                                Log.d(TAG, lectureInfo.toString());
                            }
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    for(LectureInfo le : lectureInfoList){
                        // id, lectureId, lectureRoom, lectureDay, lectureStartTime, lectureEndTime
                        adapter.addItem(le.getId(), le.getLectureId(), le.getLectureName(), le.getLectureRoom(), le.getDayOfWeek(), le.getLectureStart(), le.getLectureEnd());
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, le.getId() +", " + le.getLectureId() + ", " + le.getLectureName());
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