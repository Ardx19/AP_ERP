package edu.univ.erp.model;

public class EnrolledCourseDisplay {
    private int enrollmentId;
    private int sectionId;
    private String courseCode;
    private String title;
    private String instructorName;
    private String status;

    public EnrolledCourseDisplay(int enrollmentId, int sectionId, String courseCode, 
                                 String title, String instructorName, String status) {
        this.enrollmentId = enrollmentId;
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.title = title;
        this.instructorName = (instructorName != null) ? instructorName : "Not Assigned";
        this.status = status;
    }

    // Getters
    public int getEnrollmentId() { return enrollmentId; }
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public String getInstructorName() { return instructorName; }
    public String getStatus() { return status; }
}