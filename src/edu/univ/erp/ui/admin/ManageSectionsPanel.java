package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.MainFrame;
import net.miginfocom.swing.MigLayout;

// Import all our models and the new dialog
import edu.univ.erp.auth.AdminService;
import edu.univ.erp.model.Course;
import edu.univ.erp.model.InstructorDisplay;
import edu.univ.erp.model.SectionDisplay;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * JPanel for the Admin's "Manage Sections" screen.
 */
public class ManageSectionsPanel extends JPanel {

    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    private AdminService adminService;

    // We cache these lists to pass to the dialog
    private List<Course> courseList;
    private List<InstructorDisplay> instructorList;

    public ManageSectionsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.adminService = new AdminService();
        setLayout(new BorderLayout(10, 10));

        // --- 1. Top Panel (Title and Back Button) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Manage Sections");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Table) ---
        String[] columnNames = {"ID", "Course", "Title", "Instructor", "Semester", "Year", "Capacity"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sectionsTable = new JTable(tableModel);
        
        loadSectionData(); // Load data from DB

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Right Panel (Action Buttons) ---
        JPanel actionPanel = new JPanel(new MigLayout("wrap 1", "[grow, fill]"));
        
        JButton addSectionButton = new JButton("Add New Section...");
        JButton editSectionButton = new JButton("Edit Selected Section...");
        JButton deleteSectionButton = new JButton("Delete Selected Section");

        actionPanel.add(addSectionButton);
        actionPanel.add(editSectionButton);
        actionPanel.add(deleteSectionButton);
        
        add(actionPanel, BorderLayout.EAST);

        // --- 4. Action Listeners ---
        
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new AdminDashboard(mainFrame));
        });

        // "Add New Section" Button Action
        addSectionButton.addActionListener(e -> {
            // Create and show the custom dialog
            AddSectionDialog dialog = new AddSectionDialog(mainFrame, courseList, instructorList);
            dialog.setVisible(true);

            if (dialog.isSucceeded()) {
                // Get data from the dialog
                Course course = dialog.getSelectedCourse();
                InstructorDisplay instructor = dialog.getSelectedInstructor();
                String semester = dialog.getSemester();
                int year = dialog.getYear();
                int capacity = dialog.getCapacity();

                if (course == null || semester.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, "Course and Semester are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get the IDs from the selected objects
                int courseId = course.getCourseId();
                Integer instructorId = (instructor != null) ? instructor.getInstructorId() : null;

                // Call the service
                boolean success = adminService.createNewSection(courseId, instructorId, semester, year, capacity);

                if (success) {
                    loadSectionData(); // Refresh table
                    JOptionPane.showMessageDialog(mainFrame, "Section created successfully.");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Could not create section.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // "Delete Selected Section" Button Action
        deleteSectionButton.addActionListener(e -> {
            int selectedRow = sectionsTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a section to delete.", "No Section Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int sectionId = (int) tableModel.getValueAt(selectedRow, 0); // ID is in column 0
            String courseCode = (String) tableModel.getValueAt(selectedRow, 1);

            int choice = JOptionPane.showConfirmDialog(mainFrame, 
                "Are you sure you want to delete this section?\n\n" + courseCode + " (ID: " + sectionId + ")\n\n" +
                "This will fail if any students are enrolled in it.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = adminService.deleteSection(sectionId);

                if (success) {
                    loadSectionData(); // Refresh table
                    JOptionPane.showMessageDialog(mainFrame, "Section deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Error: Could not delete section.\n(Check if it has student enrollments).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Fetches all data needed (sections, courses, instructors) and populates the table.
     */
    private void loadSectionData() {
        // --- 1. Load data for the dropdowns ---
        // We must load these *before* showing the "Add" dialog
        this.courseList = adminService.getAllCourses();
        this.instructorList = adminService.getAllInstructors();

        // --- 2. Load data for the main table ---
        tableModel.setRowCount(0); // Clear old data
        List<SectionDisplay> sections = adminService.getAllSections();
        
        for (SectionDisplay section : sections) {
            Object[] row = new Object[] {
                section.getSectionId(),
                section.getCourseCode(),
                section.getCourseTitle(),
                section.getInstructorName(),
                section.getSemester(),
                section.getYear(),
                section.getCapacity()
            };
            tableModel.addRow(row);
        }
    }
}