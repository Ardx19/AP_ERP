package edu.univ.erp.instructor;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.InstructorProfile;
import edu.univ.erp.model.SectionDisplay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A panel for instructors to view their assigned sections.
 */
public class MySectionsPanel extends JPanel {

    private MainFrame mainFrame;
    private InstructorService instructorService;
    private InstructorProfile profile;

    private JTable sectionsTable;
    private DefaultTableModel tableModel;

    public MySectionsPanel(MainFrame mainFrame, InstructorProfile profile, InstructorService service) {
        this.mainFrame = mainFrame;
        this.profile = profile;
        this.instructorService = service;

        setLayout(new BorderLayout(10, 10));

        // --- 1. Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("My Assigned Sections");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Sections Table ---
        String[] columnNames = {"Section ID", "Course Code", "Course Title", "Semester", "Year", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        sectionsTable = new JTable(tableModel);
        
        add(new JScrollPane(sectionsTable), BorderLayout.CENTER);
        
        // --- 3. Bottom Panel (View Roster Button) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton viewRosterButton = new JButton("View/Grade Roster for Selected Section");
        bottomPanel.add(viewRosterButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. Load Data ---
        loadSectionData();

        // --- 5. Action Listeners ---
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new InstructorDashboard(mainFrame, profile.getUserId()));
        });
        
        // Placeholder for "View Roster"
        // "View/Grade Roster" Button Action
    viewRosterButton.addActionListener(e -> {
        int selectedRow = sectionsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a section first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the data from the selected row
        int sectionId = (int) tableModel.getValueAt(selectedRow, 0);
        String courseCode = (String) tableModel.getValueAt(selectedRow, 1);
        String courseTitle = (String) tableModel.getValueAt(selectedRow, 2);
        String sectionName = courseCode + " - " + courseTitle;

        // Open the GradeRosterPanel, passing the section info
        mainFrame.showPanel(new GradeRosterPanel(mainFrame, profile, instructorService, sectionId, sectionName));
    });
    }

    /**
     * Helper method to load section data into the table.
     */
    private void loadSectionData() {
        tableModel.setRowCount(0); // Clear old data

        List<SectionDisplay> sections = instructorService.getAssignedSections(profile.getInstructorId());

        if (sections.isEmpty()) {
            tableModel.addRow(new Object[]{"No sections", "are", "assigned", "to you", "at", "this time."});
        } else {
            for (SectionDisplay s : sections) {
                tableModel.addRow(new Object[]{
                        s.getSectionId(),
                        s.getCourseCode(),
                        s.getCourseTitle(),
                        s.getSemester(),
                        s.getYear(),
                        s.getCapacity()
                });
            }
        }
    }
}