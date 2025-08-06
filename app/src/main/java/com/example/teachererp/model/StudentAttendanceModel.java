package com.example.teachererp.model;

public class StudentAttendanceModel {
    private String studentName;
    private String studentClass;
    private String uid;
    private boolean isPresent;

    // Empty constructor required for Firestore
    public StudentAttendanceModel() {}

    public StudentAttendanceModel(String studentName, String studentClass, boolean isPresent) {
        this.studentName = studentName;
        this.studentClass = studentClass;
        this.isPresent = isPresent;
    }

    // Getters
    public String getStudentName() {
        return studentName;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public boolean isPresent() {
        return isPresent;
    }

    // Setters
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
