package com.example.attendancecheckapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_professor);

        userType = PreferenceManager.getString(getApplicationContext(), "userType");
        if(userType.equals("0")) setContentView(R.layout.activity_home_sutdent);
    }

    public void subjectListClick(View view) {
        Intent intent = new Intent(HomeActivity.this, LectureListActivity.class);
        startActivity(intent);
    }


}