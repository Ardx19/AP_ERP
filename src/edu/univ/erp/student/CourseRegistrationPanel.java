package edu.univ.erp.student;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.EnrolledCourseDisplay;
import edu.univ.erp.model.SectionDisplay;
import edu.univ.erp.model.StudentProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A panel for students to enroll in and drop courses.
 */
public class CourseRegistrationPanel extends JPanel {

    private MainFrame mainFrame;
    private StudentService studentService;
    private StudentProfile profile; // We need this to get the student_id

    // Table for available sections
    private JTable availableSectionsTable;
    private DefaultTableModel availableTableModel;

    // Table for enrolled sections
    private JTable enrolledSectionsTable;
    private DefaultTableModel enrolledTableModel;

    public CourseRegistrationPanel(MainFrame mainFrame, StudentProfile profile, StudentService service) {
        this.mainFrame = mainFrame;
        this.profile = profile;
        this.studentService = service;

        setLayout(new BorderLayout(10, 10));

        // --- 1. Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Course Registration");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Main Content (two tables) ---
        // We'll use a JSplitPane to make them resizable
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5); // Even split

        // --- Panel 1: Available Sections ---
        JPanel availablePanel = new JPanel(new BorderLayout(5, 5));
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Sections"));
        
        String[] availableCols = {"ID", "Course", "Title", "Instructor", "Semester", "Year", "Cap"};
        availableTableModel = new DefaultTableModel(availableCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        availableSectionsTable = new JTable(availableTableModel);
        availablePanel.add(new JScrollPane(availableSectionsTable), BorderLayout.CENTER);

        JButton enrollButton = new JButton("Enroll in Selected Section");
        availablePanel.add(enrollButton, BorderLayout.SOUTH);

        splitPane.setTopComponent(availablePanel);

        // --- Panel 2: Enrolled Sections ---
        JPanel enrolledPanel = new JPanel(new BorderLayout(5, 5));
        enrolledPanel.setBorder(BorderFactory.createTitledBorder("My Enrolled Sections"));

        String[] enrolledCols = {"Enroll ID", "Course", "Title", "Instructor", "Status"};
        enrolledTableModel = new DefaultTableModel(enrolledCols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        enrolledSectionsTable = new JTable(enrolledTableModel);
        enrolledPanel.add(new JScrollPane(enrolledSectionsTable), BorderLayout.CENTER);

        JButton dropButton = new JButton("Drop Selected Section");
        enrolledPanel.add(dropButton, BorderLayout.SOUTH);
        
        splitPane.setBottomComponent(enrolledPanel);
        
        add(splitPane, BorderLayout.CENTER);

        // --- 3. Load Initial Data ---
        loadAllData();

        // --- 4. Action Listeners ---
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new StudentDashboard(mainFrame, profile.getUserId()));
        });

        // "Enroll" Button Action
        enrollButton.addActionListener(e -> {
            int selectedRow = availableSectionsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a section to enroll in.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int sectionId = (int) availableTableModel.getValueAt(selectedRow, 0); // ID is in col 0
            
            boolean success = studentService.enrollInSection(profile.getStudentId(), sectionId);
            
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "Successfully enrolled!");
                loadAllData(); // Refresh both tables
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Error: Could not enroll in section.\n(You may already be enrolled, or the class is full).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // "Drop" Button Action
        dropButton.addActionListener(e -> {
            int selectedRow = enrolledSectionsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a section to drop.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int enrollmentId = (int) enrolledTableModel.getValueAt(selectedRow, 0); // ID is in col 0
            
            int choice = JOptionPane.showConfirmDialog(mainFrame, 
                "Are you sure you want to drop this section?", 
                "Confirm Drop", JOptionPane.YES_NO_OPTION);

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = studentService.dropEnrolledSection(enrollmentId);
                
                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "Successfully dropped section.");
                    loadAllData(); // Refresh both tables
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Error: Could not drop section.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Helper method to clear and reload all data for both tables.
     */
    private void loadAllData() {
        // 1. Clear tables
        availableTableModel.setRowCount(0);
        enrolledTableModel.setRowCount(0);

        // 2. Load Available Sections
        List<SectionDisplay> available = studentService.getAvailableSections(profile.getStudentId());
        for (SectionDisplay s : available) {
            availableTableModel.addRow(new Object[] {
                s.getSectionId(),
                s.getCourseCode(),
                s.getCourseTitle(),
                s.getInstructorName(),
                s.getSemester(),
                s.getYear(),
                s.getCapacity()
            });
        }

        // 3. Load Enrolled Sections
        List<EnrolledCourseDisplay> enrolled = studentService.getEnrolledCourses(profile.getStudentId());
        for (EnrolledCourseDisplay e : enrolled) {
            enrolledTableModel.addRow(new Object[] {
                e.getEnrollmentId(),
                e.getCourseCode(),
                e.getTitle(),
                e.getInstructorName(),
                e.getStatus()
            });
        }
    }
}