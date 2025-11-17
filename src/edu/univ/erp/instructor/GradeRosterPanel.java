package edu.univ.erp.instructor;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.InstructorProfile;
import edu.univ.erp.model.StudentRosterDisplay;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A panel for instructors to view a section's roster and enter/edit grades.
 */
public class GradeRosterPanel extends JPanel {

    private MainFrame mainFrame;
    private InstructorService instructorService;
    private InstructorProfile profile;
    private int sectionId; // The section we are grading
    private String sectionName;

    private JTable rosterTable;
    private DefaultTableModel tableModel;

    // Grade Entry Fields
    private JTextField componentField;
    private JTextField scoreField;
    private JTextField finalGradeField;

    public GradeRosterPanel(MainFrame mainFrame, InstructorProfile profile, InstructorService service,
                            int sectionId, String sectionName) {
        this.mainFrame = mainFrame;
        this.profile = profile;
        this.instructorService = service;
        this.sectionId = sectionId;
        this.sectionName = sectionName; // e.g., "CS101"

        setLayout(new BorderLayout(10, 10));

        // --- 1. Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Grade Roster: " + this.sectionName);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to My Sections");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Roster Table ---
        String[] columnNames = {"Enroll ID", "Roll Number", "Student Name", "Component", "Score", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        rosterTable = new JTable(tableModel);
        
        // Add a listener to auto-fill the form when a student is clicked
        rosterTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillGradeFieldsFromTable();
            }
        });

        add(new JScrollPane(rosterTable), BorderLayout.CENTER);
        
        // --- 3. Bottom Panel (Grade Entry Form) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Submit/Update Grade"));

        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Component:"));
        componentField = new JTextField("Final", 10);
        formPanel.add(componentField);

        formPanel.add(new JLabel("Score (0-100):"));
        scoreField = new JTextField("0.0", 5);
        formPanel.add(scoreField);

        formPanel.add(new JLabel("Final Grade (e.g., A):"));
        finalGradeField = new JTextField("N/A", 4);
        formPanel.add(finalGradeField);
        
        JButton submitGradeButton = new JButton("Submit Grade for Selected Student");

        bottomPanel.add(formPanel, BorderLayout.CENTER);
        bottomPanel.add(submitGradeButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. Load Data ---
        loadRosterData();

        // --- 5. Action Listeners ---
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new MySectionsPanel(mainFrame, profile, instructorService));
        });

        submitGradeButton.addActionListener(e -> {
            submitGrade();
        });
    }

    /**
     * Helper method to load roster data into the table.
     */
    private void loadRosterData() {
        tableModel.setRowCount(0); // Clear old data

        List<StudentRosterDisplay> roster = instructorService.getSectionRoster(sectionId);

        if (roster.isEmpty()) {
            tableModel.addRow(new Object[]{"No students", "are", "enrolled", "in", "this", "section."});
        } else {
            for (StudentRosterDisplay s : roster) {
                tableModel.addRow(new Object[]{
                        s.getEnrollmentId(),
                        s.getStudentRollNumber(),
                        s.getStudentName(),
                        s.getGradeComponent(),
                        s.getScore(),
                        s.getFinalGrade()
                });
            }
        }
    }

    /**
     * Fills the grade entry fields based on the selected table row.
     */
    private void fillGradeFieldsFromTable() {
        int selectedRow = rosterTable.getSelectedRow();
        if (selectedRow == -1) return;

        String component = (String) tableModel.getValueAt(selectedRow, 3);
        double score = (double) tableModel.getValueAt(selectedRow, 4);
        String finalGrade = (String) tableModel.getValueAt(selectedRow, 5);

        componentField.setText(component.equals("N/A") ? "Final" : component);
        scoreField.setText(String.valueOf(score));
        finalGradeField.setText(finalGrade.equals("N/A") ? "" : finalGrade);
    }

    /**
     * Validates input and calls the service to submit the grade.
     */
    private void submitGrade() {
        int selectedRow = rosterTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the roster.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int enrollmentId = (int) tableModel.getValueAt(selectedRow, 0);
            String component = componentField.getText();
            double score = Double.parseDouble(scoreField.getText());
            String finalGrade = finalGradeField.getText();

            if (component.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Component field cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Call the service
            boolean success = instructorService.submitGrade(enrollmentId, component, score, finalGrade);

            if (success) {
                JOptionPane.showMessageDialog(this, "Grade submitted successfully!");
                loadRosterData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not submit grade.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Score must be a valid number (e.g., 85.5).", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}