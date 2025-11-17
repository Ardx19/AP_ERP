package edu.univ.erp.student;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.GradeDisplay;
import edu.univ.erp.model.StudentProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * A panel for students to view their grades.
 */
public class ViewGradesPanel extends JPanel {

    private MainFrame mainFrame;
    private StudentService studentService;
    private StudentProfile profile;

    private JTable gradesTable;
    private DefaultTableModel tableModel;

    public ViewGradesPanel(MainFrame mainFrame, StudentProfile profile, StudentService service) {
        this.mainFrame = mainFrame;
        this.profile = profile;
        this.studentService = service;

        setLayout(new BorderLayout(10, 10));

        // --- 1. Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("My Grades");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Grades Table ---
        String[] columnNames = {"Course Code", "Course Title", "Component", "Score", "Final Grade"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        gradesTable = new JTable(tableModel);
        
        add(new JScrollPane(gradesTable), BorderLayout.CENTER);

        // --- 3. Load Data ---
        loadGradeData();

        // --- 4. Action Listeners ---
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new StudentDashboard(mainFrame, profile.getUserId()));
        });
    }

    /**
     * Helper method to load grade data into the table.
     */
    private void loadGradeData() {
        tableModel.setRowCount(0); // Clear old data

        List<GradeDisplay> grades = studentService.getGrades(profile.getStudentId());

        if (grades.isEmpty()) {
            // Show a message if no grades are found
            tableModel.addRow(new Object[]{"No grades", "available", "at", "this", "time."});
        } else {
            for (GradeDisplay g : grades) {
                tableModel.addRow(new Object[]{
                        g.getCourseCode(),
                        g.getCourseTitle(),
                        g.getComponent(),
                        g.getScore(),
                        g.getFinalGrade()
                });
            }
        }
    }
}