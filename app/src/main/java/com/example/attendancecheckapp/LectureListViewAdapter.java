package com.example.attendancecheckapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LectureListViewAdapter extends BaseAdapter {
    private ArrayList<LectureInfo> listViewItemList = new ArrayList<LectureInfo>();
//    private Map<String, List<Lecture>> map = new HashMap<>();


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
            convertView = inflater.inflate(R.layout.listview_lecture, parent, false);
        }

        TextView lectureName = (TextView) convertView.findViewById(R.id.lectureName) ;
        TextView lectureRoom = (TextView) convertView.findViewById(R.id.lectureRoom) ;
        TextView lectureDay = (TextView) convertView.findViewById(R.id.lectureDay) ;
        TextView lectureStartTime = (TextView) convertView.findViewById(R.id.lectureStartTime) ;
        TextView lectureEndTime = (TextView) convertView.findViewById(R.id.lectureEndTime) ;

        LectureInfo listViewItem = listViewItemList.get(position);

        lectureName.setText(listViewItem.getLectureName());
        lectureRoom.setText(listViewItem.getLectureRoom());
        lectureDay.setText(listViewItem.getDayOfWeek());
        lectureStartTime.setText(listViewItem.getLectureStart());
        lectureEndTime.setText(listViewItem.getLectureEnd());

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

    public void addItem(String id, String lectureId, String lectureName, String lectureRoom, String lectureDay, String lectureStartTime, String lectureEndTime) {
        LectureInfo item = new LectureInfo(id, lectureId, lectureName, lectureRoom, lectureDay, lectureStartTime, lectureEndTime);
        listViewItemList.add(item);
    }
}
