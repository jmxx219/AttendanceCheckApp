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
import java.util.Arrays;

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
        TextView text = (TextView) convertView.findViewById(R.id.text2);

        LectureInfo listViewItem = listViewItemList.get(position);

        lectureName.setText(listViewItem.getLectureName());
        lectureRoom.setText(listViewItem.getLectureRoom());

        lectureDay.setText(listViewItem.getDayOfWeek());
        lectureStartTime.setText(listViewItem.getLectureStart());
        lectureEndTime.setText(listViewItem.getLectureEnd());

        String[] arr = listViewItem.getDayOfWeek().split("\n");
        for(int i=0; i< arr.length - 1; i++) text.setText(text.getText() + "\n" + "~");

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
