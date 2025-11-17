package edu.univ.erp.model;

public class StudentRosterDisplay {
    private int enrollmentId;
    private int studentId;
    private String studentName;
    private String studentRollNumber;
    private String gradeComponent; // e.g., "Final"
    private double score;
    private String finalGrade;

    public StudentRosterDisplay(int enrollmentId, int studentId, String studentName, String studentRollNumber,
                                String gradeComponent, double score, String finalGrade) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.studentName = (studentName != null) ? studentName : "N/A";
        this.studentRollNumber = studentRollNumber;
        this.gradeComponent = (gradeComponent != null) ? gradeComponent : "N/A";
        this.score = score;
        this.finalGrade = (finalGrade != null) ? finalGrade : "N/A";
    }

    // Getters
    public int getEnrollmentId() { return enrollmentId; }
    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentRollNumber() { return studentRollNumber; }
    public String getGradeComponent() { return gradeComponent; }
    public double getScore() { return score; }
    public String getFinalGrade() { return finalGrade; }
}