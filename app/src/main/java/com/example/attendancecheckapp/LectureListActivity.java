package com.example.attendancecheckapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LectureListActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    public static Context context;

    private ListView lecture_view;
    LectureListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_list);

        context = this;
        lecture_view = (ListView) findViewById(R.id.lectureListView);
        adapter = new LectureListViewAdapter();
        lecture_view.setAdapter(adapter);

        UserLectureResponse();

        lecture_view.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent( getApplicationContext(), AttendanceActivity.class);

            // intent 객체에 데이터를 실어서 보내기
            Lecture item = (Lecture) adapter.getItem(position);
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
            public void onFailure(Call<ArrayList<Lecture>> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

    }
}