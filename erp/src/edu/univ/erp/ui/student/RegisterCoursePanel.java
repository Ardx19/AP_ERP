package edu.univ.erp.ui.student;

import edu.univ.erp.model.SectionDisplay;
import edu.univ.erp.student.StudentService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class RegisterCoursePanel extends JPanel {

    private StudentService studentService;
    private int studentId;
    private JTable table;
    private DefaultTableModel tableModel;
    private Runnable onClose;

    public RegisterCoursePanel(int studentId, Runnable onClose) {
        this.studentService = new StudentService();
        this.studentId = studentId;
        this.onClose = onClose;

        setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow][]"));
        
        // Header
        JLabel title = new JLabel("Register for Courses");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, "wrap");

        // Table Setup
        String[] columns = {"ID", "Course", "Instructor", "Seats & Schedule"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        add(new JScrollPane(table), "grow, wrap, h 300!");

        // Buttons
        JButton refreshBtn = new JButton("Refresh List");
        JButton registerBtn = new JButton("Register Selected");
        JButton closeBtn = new JButton("Close");

        refreshBtn.addActionListener(e -> loadData());
        closeBtn.addActionListener(e -> onClose.run());
        registerBtn.addActionListener(e -> registerAction());

        JPanel btnPanel = new JPanel(new MigLayout("insets 0"));
        btnPanel.add(refreshBtn);
        btnPanel.add(registerBtn, "gapleft 10");
        btnPanel.add(closeBtn, "gapleft 20");
        add(btnPanel, "right");

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<SectionDisplay> sections = studentService.getAvailableSections(studentId);
        
        if (sections.isEmpty()) {
            // Optional: You can comment this out if it's annoying on load
            // JOptionPane.showMessageDialog(this, "No available courses found.");
        }

        for (SectionDisplay s : sections) {
            tableModel.addRow(new Object[]{
                s.getId(), 
                s.getCourseName(), 
                s.getInstructorName(), 
                s.getSchedule()
            });
        }
    }

    private void registerAction() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a course to register.");
            return;
        }

        int sectionId = (int) tableModel.getValueAt(row, 0);
        String courseName = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Register for " + courseName + "?", "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String result = studentService.registerStudent(studentId, sectionId);
            JOptionPane.showMessageDialog(this, result);
            if (result.equals("Success")) {
                loadData(); // Refresh to remove the registered course
            }
        }
    }
}