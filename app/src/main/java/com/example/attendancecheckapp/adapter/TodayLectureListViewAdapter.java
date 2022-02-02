package com.example.attendancecheckapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.attendancecheckapp.R;
import com.example.attendancecheckapp.data.LectureInfo;

import java.util.ArrayList;

public class TodayLectureListViewAdapter extends BaseAdapter {
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
            convertView = inflater.inflate(R.layout.listview_today_lecture, parent, false);
        }

        TextView lectureName = (TextView) convertView.findViewById(R.id.todayLectureName);
        TextView lectureStartTime = (TextView) convertView.findViewById(R.id.todayLectureStartTime) ;
        TextView lectureEndTime = (TextView) convertView.findViewById(R.id.todayLectureEndTime) ;

        LectureInfo listViewItem = listViewItemList.get(position);

        lectureName.setText(listViewItem.getLectureName());
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
