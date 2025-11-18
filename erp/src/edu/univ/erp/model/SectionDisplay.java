package edu.univ.erp.model;

public class SectionDisplay {
    private int sectionId;
    private String courseCode;
    private String courseTitle;
    private String instructorName;
    private String semester;
    private int year;
    private int capacity;
    
    // New fields for Student/Instructor views (to store pre-formatted strings)
    private String combinedCourseName;
    private String scheduleInfo; 

    // --- Constructor 1: Used by Admin Panel (Existing) ---
    public SectionDisplay(int sectionId, String courseCode, String courseTitle, 
                          String instructorName, String semester, int year, int capacity) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.instructorName = (instructorName != null) ? instructorName : "Not Assigned";
        this.semester = semester;
        this.year = year;
        this.capacity = capacity;
        
        // Auto-generate the "combined" fields so getters work for everyone
        this.combinedCourseName = courseCode + " - " + courseTitle;
        this.scheduleInfo = semester + " " + year;
    }

    // --- Constructor 2: Used by Student & Instructor Services (New) ---
    // This matches the: new SectionDisplay(id, "Code-Title", "Instructor", "Time/Seats") calls
    public SectionDisplay(int sectionId, String combinedCourseName, String instructorName, String scheduleInfo) {
        this.sectionId = sectionId;
        this.combinedCourseName = combinedCourseName;
        this.instructorName = instructorName;
        this.scheduleInfo = scheduleInfo;
        
        // Set defaults for the other fields to avoid null pointer errors
        this.courseCode = "";
        this.courseTitle = "";
        this.semester = "";
        this.year = 0;
        this.capacity = 0;
    }

    // --- Standard Getters ---
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getInstructorName() { return instructorName; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
    public int getCapacity() { return capacity; }

    // --- Compatibility Getters (Fixes the compilation errors) ---
    public int getId() { return sectionId; } 
    public String getCourseName() { return combinedCourseName; } 
    public String getSchedule() { return scheduleInfo; } 
    
    @Override
    public String toString() {
        return combinedCourseName;
    }
}