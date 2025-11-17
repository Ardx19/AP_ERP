package edu.univ.erp.model;

public class InstructorProfile {
    private int instructorId;
    private int userId;
    private String fullName;
    private String department;

    // Constructor
    public InstructorProfile(int instructorId, int userId, String fullName, String department) {
        this.instructorId = instructorId;
        this.userId = userId;
        this.fullName = fullName;
        this.department = department;
    }

    // Getters
    public int getInstructorId() { return instructorId; }
    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }
}