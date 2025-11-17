package edu.univ.erp.model;

public class InstructorDisplay {
    private int instructorId;
    private String fullName;
    private String department;

    public InstructorDisplay(int instructorId, String fullName, String department) {
        this.instructorId = instructorId;
        this.fullName = fullName;
        this.department = department;
    }

    public int getInstructorId() { return instructorId; }
    public String getFullName() { return fullName; }
    public String getDepartment() { return department; }

    // This is the magic method that JComboBox uses to display the name
    @Override
    public String toString() {
        return fullName + " (ID: " + instructorId + ")";
    }
}