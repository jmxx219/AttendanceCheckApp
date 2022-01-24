package com.example.attendancecheckapp;

public class ManualCheck {
    private String name;

    private String schoolNumber;

    public String isAttend;

    private String attendanceId;

    public ManualCheck(String name, String schoolNumber, String isAttend, String studentId) {
        this.name = name;
        this.schoolNumber = schoolNumber;
        this.isAttend = isAttend;
        this.attendanceId = studentId;
    }

    public ManualCheck() {

    }

    public String getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchoolNumber() {
        return schoolNumber;
    }

    public void setSchoolNumber(String schoolNumber) {
        this.schoolNumber = schoolNumber;
    }

    public String getIsAttend() {
        return isAttend;
    }

    public void setIsAttend(String isAttend) {
        this.isAttend = isAttend;
    }

    @Override
    public String toString() {
        return "ManualCheck{" +
                "name='" + name + '\'' +
                ", schoolNumber='" + schoolNumber + '\'' +
                ", isAttend='" + isAttend + '\'' +
                ", attendanceId='" + attendanceId + '\'' +
                '}';
    }
}
