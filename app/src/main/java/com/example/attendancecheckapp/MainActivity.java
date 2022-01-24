package com.example.attendancecheckapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {  // 로그인 액티비티
    private final String TAG = getClass().getSimpleName();

    private EditText loginId;
    private EditText loginPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginId = (EditText) findViewById(R.id.et_name);
        loginPw = (EditText) findViewById(R.id.et_pwd);
    }


    public void loginClick(View view) {
        String id = loginId.getText().toString();
        String pw = loginPw.getText().toString();

        // 로그인 정보 미입력 시
        if (id.trim().length() == 0 || pw.trim().length() == 0 || id == null || pw == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("알림")
                    .setMessage("로그인 정보를 입력바랍니다.")
                    .setPositiveButton("확인", null)
                    .create()
                    .show();
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else {
            // 로그인 통신
            StartLoginResponse();
        }
    }

    private void StartLoginResponse() {
        Log.d(TAG, "Login POST");

        LoginRequest item = new LoginRequest(loginId.getText().toString(), loginPw.getText().toString());

        Call<String> postCall = RetrofitClient.getApiService().getLoginResponse(item);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "등록 완료");

                    LoginResponse res = new LoginResponse();

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        Log.d(TAG, jsonObject.toString());
                        Log.d(TAG, dataObject.toString());

                        res.setToken(dataObject.getString("access_token"));
                        Log.d(TAG, res.getToken());

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_LONG).show();

                    try {
                        JWTUtils.decoded(res.getToken());
                        PreferenceManager.setString(getApplicationContext(), "userId", JWTUtils.getPayload("id")); // 값 저장

                        // userType : PROFESSOR - 교수, STUDENT - 학생
                        PreferenceManager.setString(getApplicationContext(), "userType", JWTUtils.getPayload("aud"));
                        Log.d(TAG, PreferenceManager.getString(getApplicationContext(), "userId"));
                        Log.d(TAG, PreferenceManager.getString(getApplicationContext(), "userType"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    PreferenceManager.setString(getApplicationContext(), "token", res.getToken()); // 값 저장

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "Fail msg : " + t.getMessage());
            }
        });

    }
}