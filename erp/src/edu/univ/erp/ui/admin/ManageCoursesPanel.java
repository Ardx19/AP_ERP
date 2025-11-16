package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.MainFrame;
import net.miginfocom.swing.MigLayout;

// Imports for course data
import edu.univ.erp.auth.AdminService;
import edu.univ.erp.model.Course;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * JPanel for the Admin's "Manage Courses" screen.
 */
public class ManageCoursesPanel extends JPanel {

    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    private AdminService adminService;

    public ManageCoursesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.adminService = new AdminService();
        setLayout(new BorderLayout(10, 10));

        // --- 1. Top Panel (Title and Back Button) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Manage Courses");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Table) ---
        String[] columnNames = {"Course ID", "Course Code", "Title", "Credits"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are not editable
            }
        };
        coursesTable = new JTable(tableModel);
        
        loadCourseData(); // Load data from DB

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Right Panel (Action Buttons) ---
        JPanel actionPanel = new JPanel(new MigLayout("wrap 1", "[grow, fill]"));
        
        JButton addCourseButton = new JButton("Add New Course...");
        JButton editCourseButton = new JButton("Edit Selected Course...");
        JButton deleteCourseButton = new JButton("Delete Selected Course");

        actionPanel.add(addCourseButton);
        actionPanel.add(editCourseButton);
        actionPanel.add(deleteCourseButton);
        
        add(actionPanel, BorderLayout.EAST);

        // --- 4. Action Listeners ---
        
        // "Back" Button Action
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new AdminDashboard(mainFrame));
        });

        // "Add New Course" Button Action
        addCourseButton.addActionListener(e -> {
            // For simplicity, we'll use a dialog with multiple inputs
            // We can build a custom JDialog later if needed
            
            JTextField courseCodeField = new JTextField();
            JTextField titleField = new JTextField();
            JTextField creditsField = new JTextField();

            JPanel panel = new JPanel(new MigLayout("wrap 2", "[right]rel[grow,fill]"));
            panel.add(new JLabel("Course Code:"));
            panel.add(courseCodeField, "width 150:200:");
            panel.add(new JLabel("Title:"));
            panel.add(titleField);
            panel.add(new JLabel("Credits:"));
            panel.add(creditsField, "width 50::");

            int result = JOptionPane.showConfirmDialog(mainFrame, panel, 
                    "Add New Course", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

           if (result == JOptionPane.OK_OPTION) {
        try {
            String code = courseCodeField.getText();
            String courseTitle = titleField.getText(); // <-- RENAMED
            int credits = Integer.parseInt(creditsField.getText());

            if (code.isEmpty() || courseTitle.isEmpty()) { // <-- UPDATED
                throw new Exception("Code and Title cannot be empty.");
            }

            Course newCourse = new Course(code, courseTitle, credits); // <-- UPDATED
            boolean success = adminService.createNewCourse(newCourse);

                    if (success) {
                        loadCourseData(); // Refresh table
                        JOptionPane.showMessageDialog(mainFrame, "Course created successfully.");
                    } else {
                        throw new Exception("Database error. Check for duplicate course code.");
                    }

                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(mainFrame, "Credits must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Could not create course: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // "Delete Selected Course" Button Action
        deleteCourseButton.addActionListener(e -> {
            int selectedRow = coursesTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a course to delete.", "No Course Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int courseId = (int) tableModel.getValueAt(selectedRow, 0);
            String courseCode = (String) tableModel.getValueAt(selectedRow, 1);

            int choice = JOptionPane.showConfirmDialog(mainFrame, 
                "Are you sure you want to delete this course?\n\n" + courseCode + "\n\n" +
                "This will fail if any sections are still linked to it.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = adminService.deleteCourse(courseId);

                if (success) {
                    loadCourseData(); // Refresh table
                    JOptionPane.showMessageDialog(mainFrame, "Course deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Error: Could not delete course.\n(Check if it is still used by sections or enrollments).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Fetches course data from AdminService and populates the table.
     */
    private void loadCourseData() {
        tableModel.setRowCount(0); // Clear old data
        List<Course> courses = adminService.getAllCourses();
        
        for (Course course : courses) {
            Object[] row = new Object[] {
                course.getCourseId(),
                course.getCourseCode(),
                course.getTitle(),
                course.getCredits()
            };
            tableModel.addRow(row);
        }
    }
}