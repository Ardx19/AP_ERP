package edu.univ.erp.model;

public class Course {
    private int courseId;
    private String courseCode;
    private String title;
    private int credits;

    // Constructor for new courses (ID is auto-generated)
    public Course(String courseCode, String title, int credits) {
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
    }

    // Constructor for reading from DB
    public Course(int courseId, String courseCode, String title, int credits) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.title = title;
        this.credits = credits;
    }

    // Getters
    public int getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
}