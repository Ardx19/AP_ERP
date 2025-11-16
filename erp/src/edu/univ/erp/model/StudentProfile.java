package edu.univ.erp.model;

public class StudentProfile {
    private int studentId;
    private int userId;
    private String rollNumber;
    private String fullName;
    private String program;

    // Constructor
    public StudentProfile(int studentId, int userId, String rollNumber, String fullName, String program) {
        this.studentId = studentId;
        this.userId = userId;
        this.rollNumber = rollNumber;
        this.fullName = fullName;
        this.program = program;
    }

    // Getters
    public int getStudentId() { return studentId; }
    public int getUserId() { return userId; }
    public String getRollNumber() { return rollNumber; }
    public String getFullName() { return fullName; }
    public String getProgram() { return program; }
}