package com.example.attendancecheckapp;

import com.google.gson.annotations.SerializedName;

public class Lecture {
    @SerializedName("id")
    public String id;

    @SerializedName("lecture_id")
    public String lecture_id;

    @SerializedName("lecture_name")
    public String lecture_name;

    @SerializedName("lecture_room")
    public String lecture_room;

    @SerializedName("day_of_week")
    public String day_of_week;

    @SerializedName("lecture_start")
    public String lecture_start;

    @SerializedName("lecture_end")
    public String lecture_end;


    public Lecture(String id, String lectureId, String lectureName, String lectureRoom, String lectureDay, String lectureStartTime, String lectureEndTime) {
        this.id = id;
        this.lecture_id = lectureId;
        this.lecture_name = lectureName;
        this.lecture_room = lectureRoom;
        this.day_of_week = lectureDay;
        this.lecture_start = lectureStartTime;
        this.lecture_end = lectureEndTime;
    }

    public String getId() {
        return id;
    }
    public String getLectureId() {
        return lecture_id;
    }
    public String getLectureName() { return lecture_name; }
    public String getLectureRoom() { return lecture_room; }
    public String getDayOfWeek() {
        return day_of_week;
    }
    public String getLectureStart() {
        return lecture_start;
    }
    public String getLectureEnd() {
        return lecture_end;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setLectureId(String lecture_id) {
        this.lecture_id = lecture_id;
    }
    public void setLectureName(String lecture_name) {
        this.lecture_name = lecture_name;
    }
    public void setLectureRoom(String lecture_room) {
        this.lecture_room = lecture_room;
    }
    public void setDayOfWeek(String day_of_week) {
        this.day_of_week = day_of_week;
    }
    public void setLectureStart(String lecture_start) {
        this.lecture_start = lecture_start;
    }
    public void setLectureEnd(String lecture_end) {
        this.lecture_end = lecture_end;
    }

}
