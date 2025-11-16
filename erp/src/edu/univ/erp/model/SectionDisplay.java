package edu.univ.erp.model;

public class SectionDisplay {
    private int sectionId;
    private String courseCode;
    private String courseTitle;
    private String instructorName;
    private String semester;
    private int year;
    private int capacity;

    public SectionDisplay(int sectionId, String courseCode, String courseTitle, 
                          String instructorName, String semester, int year, int capacity) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.instructorName = (instructorName != null) ? instructorName : "Not Assigned";
        this.semester = semester;
        this.year = year;
        this.capacity = capacity;
    }

    // Getters
    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public String getInstructorName() { return instructorName; }
    public String getSemester() { return semester; }
    public int getYear() { return year; }
    public int getCapacity() { return capacity; }
}
