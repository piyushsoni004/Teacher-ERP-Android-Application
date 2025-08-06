package com.example.teachererp.model;

public class ClassScheduleModel {
    private String day;
    private String timeSlot;
    private String subjectName;
    private String teacherName;

    public ClassScheduleModel() {} // Needed for Firestore

    public ClassScheduleModel(String day, String timeSlot, String subjectName, String teacherName) {
        this.day = day;
        this.timeSlot = timeSlot;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
    }

    public String getDay() { return day; }
    public String getTimeSlot() { return timeSlot; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
}
