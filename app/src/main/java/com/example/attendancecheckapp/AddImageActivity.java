package com.example.attendancecheckapp;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.PreferenceManager;
import com.example.attendancecheckapp.data.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

public class AddImageActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    ArrayList<Uri> uriList = new ArrayList<>();     // 이미지의 uri를 담을 ArrayList 객체

    RecyclerView recyclerView;  // 이미지를 보여줄 리사이클러뷰
    AddImageAdapter adapter;  // 리사이클러뷰에 적용시킬 어댑터

    private final int image_cnt = 3; // 이미지 개수

    Button saveButton;

    Uri singleImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);

        saveButton = (Button) findViewById(R.id.save);
        saveButton.setVisibility(View.INVISIBLE);
        saveButton.setEnabled(false);

        // 앨범으로 이동하는 버튼
        Button btn_getImage = findViewById(R.id.getImage);
        btn_getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2222);
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
    }


    /**
     * 다중 이미지 전송
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
            uriList.clear();
            adapter.notifyDataSetChanged();
            saveButton.setVisibility(View.INVISIBLE);
            saveButton.setEnabled(false);
        }
        else{   // 이미지를 하나라도 선택한 경우
            ClipData clipData = data.getClipData();
            if(clipData.getItemCount() < image_cnt){     // 이미지가 3장 미만인 경우
                Toast.makeText(getApplicationContext(), "사진을 3장 선택 해주세요.", Toast.LENGTH_LONG).show();
                uriList.clear();
                adapter.notifyDataSetChanged();
                saveButton.setVisibility(View.INVISIBLE);
                saveButton.setEnabled(false);
            }
            else{
                if(clipData.getItemCount() > image_cnt){   // 선택한 이미지가 3장 초과인 경우
                    Toast.makeText(getApplicationContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
                    uriList.clear();
                    adapter.notifyDataSetChanged();
                    saveButton.setVisibility(View.INVISIBLE);
                    saveButton.setEnabled(false);
                }
                else{   // 선택한 이미지가 1장 이상 3장 이하인 경우
                    Log.e(TAG, "multiple choice");

                    for (int i = 0; i < clipData.getItemCount(); i++){
                        Uri imageUri = clipData.getItemAt(i).getUri();  // 선택한 이미지들의 uri를 가져온다.
                        try {
                            uriList.add(imageUri);  //uri를 list에 담는다.

                        } catch (Exception e) {
                            Log.e(TAG, "File select error", e);
                        }
                    }

                    adapter = new AddImageAdapter(uriList, getApplicationContext());
                    recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
                    recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용

                    Toast.makeText(getApplicationContext(), "저장 버튼을 눌러주세요.", Toast.LENGTH_LONG).show();

                    saveButton.setVisibility(View.VISIBLE);
                    saveButton.setEnabled(true);
                }
            }
        }
        Log.d(TAG, "uriList.size : " + String.valueOf(uriList.size()));
    }

    public void onSaveClick(View view) {
        Log.d(TAG, "Save Image POST");

        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");

        ArrayList<MultipartBody.Part> imageList = new ArrayList<>();

        //filepath는 String 변수로 갤러리에서 이미지를 가져올 때 photoUri.getPath()를 통해 받아온다.

        // 파일 경로들을 가지고있는 `ArrayList<Uri> uriList`
        for(int i=0; i<uriList.size(); i++) {

            // 사진 파일 이름
            String fileName = PreferenceManager.getString(getApplicationContext(), "userSchoolNumber") + "_" + (i+1) + ".jpg";
            Log.d(TAG, fileName);

            String path = FileUtil.getPath(uriList.get(i), this);
            Log.d(TAG, path);
            File file = new File(path);

            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", fileName, requestFile);
            imageList.add(uploadFile);
        }

        Log.d(TAG, imageList.get(0).body().contentType().toString());

        Call<String> postCall = RetrofitClient.getApiService().sendUserImage(imageList, token);
        postCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Status Code : " + response.code());
                    Log.d(TAG, "이미지 등록");

                    try{
                        JSONObject jsonObject = new JSONObject(response.body());
                        JSONArray dataArray = jsonObject.getJSONArray("data");

                        for(int i=0; i<dataArray.length(); i++) {
                            JSONObject imageObject = dataArray.getJSONObject(i);

                            Log.d(TAG, imageObject.getString("file_name"));
                            Log.d(TAG, imageObject.getString("saved_url"));
                            Log.d(TAG, imageObject.getString("face_image_id"));
                        }

                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
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


    /**
     * 단일 이미지 전송
     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(data == null){   // 어떤 이미지도 선택하지 않은 경우
//            Toast.makeText(getApplicationContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
//            uriList.clear();
//            adapter.notifyDataSetChanged();
//            saveButton.setVisibility(View.INVISIBLE);
//            saveButton.setEnabled(false);
//        }
//        else{   // 이미지를 하나라도 선택한 경우
//            ClipData clipData = data.getClipData();
//            if(clipData.getItemCount() > image_cnt){
//                Toast.makeText(getApplicationContext(), "사진은 1장까지 선택 가능합니다.", Toast.LENGTH_LONG).show();
//                uriList.clear();
//                adapter.notifyDataSetChanged();
//                saveButton.setVisibility(View.INVISIBLE);
//                saveButton.setEnabled(false);
//            }
//            else{   // 선택한 이미지가 1장인 경우
//                Log.e(TAG, "single choice");
//
//                singleImageUri = clipData.getItemAt(0).getUri();
//                uriList.add(singleImageUri);
//
//                adapter = new AddImageAdapter(uriList, getApplicationContext());
//                recyclerView.setAdapter(adapter);   // 리사이클러뷰에 어댑터 세팅
//                recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true));     // 리사이클러뷰 수평 스크롤 적용
//
//                Toast.makeText(getApplicationContext(), "저장 버튼을 눌러주세요.", Toast.LENGTH_LONG).show();
//
//                saveButton.setVisibility(View.VISIBLE);
//                saveButton.setEnabled(true);
//            }
//        }
//
//        Log.d(TAG, "uriList.size : " + String.valueOf(uriList.size()));
//    }
//
//    public void onSaveClick(View view) {
//        Log.d(TAG, "Save Image POST");
//
//        String token = "Bearer " + PreferenceManager.getString(getApplicationContext(), "token");
//
//        //filepath는 String 변수로 갤러리에서 이미지를 가져올 때 photoUri.getPath()를 통해 받아온다.
//
//        // 사진 파일 이름
//        String fileName = PreferenceManager.getString(getApplicationContext(), "userSchoolNumber") + "_1.jpg";
//        Log.d(TAG, fileName);
//
//        String path = FileUtil.getPath(singleImageUri, this);
//        Log.d(TAG, path);
//        File file = new File(path);
//
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
//        MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", fileName, requestFile);
//
//        Call<String> postCall = RetrofitClient.getApiService().sendUserImageSingle(uploadFile, token);
//        postCall.enqueue(new Callback<String>() {
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                if (response.isSuccessful()) {
//                    Log.d(TAG, "Status Code : " + response.code());
//                    Log.d(TAG, "이미지 등록");
//
//                    try{
//                        JSONObject jsonObject = new JSONObject(response.body());
//                        JSONArray dataArray = jsonObject.getJSONArray("data");
//
//                        for(int i=0; i<dataArray.length(); i++) {
//                            JSONObject imageObject = dataArray.getJSONObject(i);
//
//                            Log.d(TAG, imageObject.getString("file_name"));
//                            Log.d(TAG, imageObject.getString("saved_url"));
//                            Log.d(TAG, imageObject.getString("face_image_id"));
//                        }
//
//                    }catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.d(TAG, "Status Code : " + response.code());
//                    Log.d(TAG, response.errorBody().toString());
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//                Log.d(TAG, "Fail msg : " + t.getMessage());
//            }
//        });
//    }
//

}
