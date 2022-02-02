package com.example.attendancecheckapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancecheckapp.api.RetrofitClient;
import com.example.attendancecheckapp.data.ManualCheck;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualAttendanceListViewAdapter extends BaseAdapter {
    private final String TAG = getClass().getSimpleName();
    private ArrayList<ManualCheck> listViewItemList = new ArrayList<ManualCheck>();

    public static Context context;

    public void ListViewAdapter() {

    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }
    public String getCountString() {
        return String.valueOf(listViewItemList.size());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_manual_attendance, parent, false);
        }

        TextView studentName = (TextView) convertView.findViewById(R.id.manualStudentName) ;
        TextView studentNumber = (TextView) convertView.findViewById(R.id.manualStudentNumber) ;
        Switch attendButton = (Switch) convertView.findViewById(R.id.manualAttend) ;

        ManualCheck listViewItem = listViewItemList.get(position);

        studentName.setText(listViewItem.getName());
        studentNumber.setText(listViewItem.getSchoolNumber());

        // attend가 0이라면 결석, 1이라면 출석
        if(listViewItem.getIsAttend().equals("0")) attendButton.setChecked(false);
        else if(listViewItem.getIsAttend().equals("1")) attendButton.setChecked(true);

        attendButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Change Of Attendance");

                String token = "Bearer " + PreferenceManager.getString(context, "token");

                Call<String> postCall = RetrofitClient.getApiService().setChangeOfAttendance(listViewItem.getAttendanceId(), token);
                postCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "Status Code : " + response.code());
                            Log.d(TAG, "수동 출석 변경 완료");

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

                Toast.makeText(context, listViewItem.getAttendanceId() + " " + studentName.getText() + " 학생 수동 출석 체크", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    public void addItem(String name, String schoolNumber, String isAttend, String studentId) {
        ManualCheck item = new ManualCheck(name, schoolNumber, isAttend, studentId);
        listViewItemList.add(item);
    }
}
