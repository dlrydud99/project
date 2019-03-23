package edu.skku.finalproject.studyhelper.dto;

public class Attendance {
    String attendance = "결석";

    public Attendance() {
    }

    public Attendance(String attendance) {
        this.attendance = attendance;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
