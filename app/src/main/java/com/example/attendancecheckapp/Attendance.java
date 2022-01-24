package com.example.attendancecheckapp;

import com.google.gson.annotations.SerializedName;

public class Attendance {
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
}
