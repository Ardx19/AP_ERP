package edu.univ.erp.instructor;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.InstructorDisplay;
import edu.univ.erp.model.SectionDisplay;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InstructorDashboard extends JPanel {

    private MainFrame mainFrame;
    private InstructorService instructorService;
    private InstructorDisplay profile;
    private JTable sectionsTable;
    private DefaultTableModel tableModel;

    public InstructorDashboard(MainFrame mainFrame, int userId) {
        this.mainFrame = mainFrame;
        this.instructorService = new InstructorService();
        
        setLayout(new BorderLayout());

        // 1. Fetch Profile
        this.profile = instructorService.getInstructorProfile(userId);
        if (profile == null) {
            add(new JLabel("Error: Instructor profile not found."), BorderLayout.CENTER);
            return;
        }

        // 2. Top Bar (Welcome)
        JPanel topPanel = new JPanel(new MigLayout("fill", "[grow][]"));
        topPanel.setBackground(new Color(240, 248, 255)); // Light blue
        
        // FIX: Use getFullName() instead of getName()
        JLabel welcome = new JLabel("Welcome, " + profile.getFullName());
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        // FIX: Use getDepartment() instead of getEmail()
        JLabel dept = new JLabel("Dept: " + profile.getDepartment()); 
        
        topPanel.add(welcome);
        topPanel.add(dept, "wrap");
        add(topPanel, BorderLayout.NORTH);

        // 3. Center Panel (My Sections)
        JPanel centerPanel = new JPanel(new MigLayout("fill, insets 20", "[grow]", "[][grow][]"));
        
        centerPanel.add(new JLabel("My Assigned Sections"), "wrap, gapbottom 10, gaptop 10");

        // Table
        String[] cols = {"ID", "Course", "Enrollment", "Schedule"};
        tableModel = new DefaultTableModel(cols, 0) {
             @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        sectionsTable = new JTable(tableModel);
        
        // Hide ID column
        sectionsTable.getColumnModel().getColumn(0).setMinWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setWidth(0);
        
        centerPanel.add(new JScrollPane(sectionsTable), "grow, wrap");

        // Refresh Button
        JButton refreshBtn = new JButton("Refresh List");
        refreshBtn.addActionListener(e -> loadSections());
        
        JButton gradeBtn = new JButton("Enter Grades");
        gradeBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Grading feature coming next!"));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(refreshBtn);
        btnPanel.add(gradeBtn);
        centerPanel.add(btnPanel, "growx");

        add(centerPanel, BorderLayout.CENTER);

        // Initial Load
        loadSections();
    }

    private void loadSections() {
        tableModel.setRowCount(0);
        // FIX: Use getInstructorId() instead of getId()
        List<SectionDisplay> sections = instructorService.getAssignedSections(profile.getInstructorId()); 
        for (SectionDisplay s : sections) {
            tableModel.addRow(new Object[]{
                s.getId(),          // Works now (added to SectionDisplay)
                s.getCourseName(),  // Works now (added to SectionDisplay)
                s.getInstructorName(), // Holds "Enrolled: x/y"
                s.getSchedule()     // Works now (added to SectionDisplay)
            });
        }
    }
}