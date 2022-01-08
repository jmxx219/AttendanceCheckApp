package com.example.attendancecheckapp;

import com.google.gson.annotations.SerializedName;

public class LectureAttendance {
    @SerializedName("id")
    public String id;

    @SerializedName("user_id")
    public String user_id;

    @SerializedName("week")
    public String week;

    @SerializedName("day_of_week")
    public String day_of_week;

    @SerializedName("is_attend")
    public String is_attend;



    public LectureAttendance(String id, String userId, String week, String lectureDay, String isAttend) {
        this.id = id;
        this.user_id = userId;
        this.week = week;
        this.day_of_week = lectureDay;
        this.is_attend = isAttend;
    }

    public String getId() { return id; }
    public String getUserId() {
        return user_id;
    }
    public String getWeek() { return week; }
    public String getDayOfWeek() {
        return day_of_week;
    }
    public String getIsAttend() {
        return is_attend;
    }

    public void setId(String id) { this.id = id; }
    public void setUserId(String lecture_id) {
        this.user_id = lecture_id;
    }
    public void setWeek(String lecture_name) {
        this.week = lecture_name;
    }
    public void setDayOfWeek(String day_of_week) {
        this.day_of_week = day_of_week;
    }
    public void setIsAttend(String lecture_start) {
        this.is_attend = lecture_start;
    }

}
