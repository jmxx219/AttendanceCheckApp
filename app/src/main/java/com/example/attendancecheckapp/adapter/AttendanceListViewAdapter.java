package com.example.attendancecheckapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.attendancecheckapp.PreferenceManager;
import com.example.attendancecheckapp.R;
import com.example.attendancecheckapp.data.Attendance;

import java.util.ArrayList;

public class AttendanceListViewAdapter extends BaseAdapter {
    private ArrayList<Attendance> listViewItemList = new ArrayList<Attendance>();

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
            convertView = inflater.inflate(R.layout.listview_attendance, parent, false);
        }

        TextView week = (TextView) convertView.findViewById(R.id.week) ;
        TextView dayOfWeek = (TextView) convertView.findViewById(R.id.dayOfWeek) ;
        Button attendButton = (Button) convertView.findViewById(R.id.attend) ;

        Attendance listViewItem = listViewItemList.get(position);

        week.setText(listViewItem.getWeek() + "주차");
        dayOfWeek.setText(listViewItem.getDayOfWeek() + "요일");


        if(PreferenceManager.getString(context, "userType").equals("PROFESSOR")){
            // 교수일 때, 출석 여부 버튼 안보이게
            attendButton.setVisibility(View.INVISIBLE);
            attendButton.setEnabled(true);

        }
        else {
            // attend가 0이라면 결석, 1이라면 출석
            if(listViewItem.getIsAttend().equals("0")) {
                attendButton.setText("N");
                attendButton.setBackgroundColor(Color.parseColor("#dc143c"));
            }
            else if(listViewItem.getIsAttend().equals("1")) {
                attendButton.setText("Y");
                attendButton.setBackgroundColor(Color.parseColor("#4169E1"));
            }
        }
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

    public void addItem(String id, String week, String lectureDay, String isAttend) {
        Attendance item = new Attendance(id, week, lectureDay, isAttend);
        listViewItemList.add(item);
    }
}
