package com.example.attendancecheckapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AttendanceListViewAdapter extends BaseAdapter {
    private ArrayList<Attendance> listViewItemList = new ArrayList<Attendance>();



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
        Button attend = (Button) convertView.findViewById(R.id.attend) ;

        Attendance listViewItem = listViewItemList.get(position);

        week.setText(listViewItem.getWeek() + "주차");
        dayOfWeek.setText(listViewItem.getDayOfWeek() + "요일");

        // attend가 0이라면 결석, 1이라면 출석
        if(listViewItem.getIsAttend().equals("0")) attend.setText("N");
        else if(listViewItem.getIsAttend().equals("1")) attend.setText("Y");

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

    public void addItem(String id, String userId, String week, String lectureDay, String isAttend) {
        Attendance item = new Attendance(id, userId, week, lectureDay, isAttend);
        listViewItemList.add(item);
    }
}
