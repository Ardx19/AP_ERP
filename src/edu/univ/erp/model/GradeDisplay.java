package edu.univ.erp.model;

public class GradeDisplay {
    private String courseCode;
    private String courseTitle;
    private String component; // e.g., "Midterm", "Final"
    private double score;
    private String finalGrade; // e.g., "A", "B+"

    public GradeDisplay(String courseCode, String courseTitle, String component, double score, String finalGrade) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.component = component;
        this.score = score;
        this.finalGrade = (finalGrade != null) ? finalGrade : "N/A";
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getComponent() { return component; }
    public double getScore() { return score; }
    public String getFinalGrade() { return finalGrade; }
}