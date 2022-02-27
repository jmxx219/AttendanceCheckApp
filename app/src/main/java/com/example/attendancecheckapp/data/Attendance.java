package com.example.attendancecheckapp.data;

import com.google.gson.annotations.SerializedName;

public class Attendance implements Comparable<Attendance>{
    private String[] days = {"월", "화", "수", "목", "금", "토", "일"};

    private int int_day_of_week;

    @SerializedName("id")
    public String id;

    @SerializedName("week")
    public String week;

    @SerializedName("day_of_week")
    public String day_of_week;

    @SerializedName("is_attend")
    public String is_attend;


    public Attendance(String id, String week, String lectureDay, String isAttend) {
        this.id = id;
        this.week = week;
        this.day_of_week = lectureDay;
        this.is_attend = isAttend;

        for(int i=0; i<7; i++){
            if(day_of_week.equals(days[i])) int_day_of_week = i;
        }
    }

    public Attendance() {

    }

    public String getId() { return id; }
    public String getWeek() { return week; }
    public String getDayOfWeek() {
        return day_of_week;
    }
    public String getIsAttend() {
        return is_attend;
    }
    private int getIntDayOfWeek() {return int_day_of_week;}

    public void setId(String id) { this.id = id; }
    public void setWeek(String lecture_name) {
        this.week = lecture_name;
    }
    public void setDayOfWeek(String day_of_week) {
        this.day_of_week = day_of_week;
    }
    public void setIsAttend(String lecture_start) {
        this.is_attend = lecture_start;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id='" + id + '\'' +
                ", week='" + week + '\'' +
                ", day_of_week='" + day_of_week + '\'' +
                ", is_attend='" + is_attend + '\'' +
                '}';
    }

    @Override
    public int compareTo(Attendance s) {
        if(Integer.valueOf(this.week) == Integer.valueOf(s.getWeek())) {
            return Integer.compare(this.int_day_of_week, s.getIntDayOfWeek() * -1);
        }

        return Integer.valueOf(this.week).compareTo(Integer.valueOf(s.getWeek()));
    }
}
