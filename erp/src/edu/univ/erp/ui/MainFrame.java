package edu.univ.erp.ui;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.ui.auth.LoginWindow; 
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.student.StudentDashboard;
import edu.univ.erp.instructor.InstructorDashboard; // <--- 1. ADD THIS IMPORT

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private int loggedInUserId;
    private JPanel mainContentPanel; 
    private JMenuBar menuBar;
    private JLabel statusBar;

    public MainFrame() {
        FlatLightLaf.setup();
        setTitle("University ERP - Main Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        // --- Create Menu Bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- Main Content Area ---
        mainContentPanel = new JPanel(new BorderLayout());
        add(mainContentPanel, BorderLayout.CENTER);

        // --- Status Bar ---
        statusBar = new JLabel("Logged in as: [User]");
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * This method shows the correct dashboard and menus
     * based on the user's role.
     */
    public void showDashboard(String role, int userId) {
        JPanel dashboard = null;
        this.loggedInUserId = userId;

        // 1. Clear any old, role-specific menus
        menuBar.removeAll();

        // 2. Add the default "File" menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem logoutItem = new JMenuItem("Logout");
        fileMenu.add(logoutItem);
        menuBar.add(fileMenu);

        logoutItem.addActionListener(e -> {
            this.setVisible(false);
            new LoginWindow().setVisible(true);
            this.dispose();
        });

        // 3. Build Role-Aware Dashboard & Menus
        // NOTE: We use equalsIgnoreCase to match "ADMIN", "Admin", or "admin" safely.
        if (role.equalsIgnoreCase("admin")) {
            dashboard = new AdminDashboard(this); 
            statusBar.setText("Logged in as: Admin");

            JMenu adminMenu = new JMenu("Admin");
            adminMenu.add(new JMenuItem("Manage Users"));
            adminMenu.add(new JMenuItem("Manage Courses"));
            menuBar.add(adminMenu);

        } else if (role.equalsIgnoreCase("student")) {
            dashboard = new StudentDashboard(this, this.loggedInUserId); 
            statusBar.setText("Logged in as: Student (ID: " + this.loggedInUserId + ")");

            JMenu studentMenu = new JMenu("My Academics");
            studentMenu.add(new JMenuItem("View Timetable"));
            studentMenu.add(new JMenuItem("View Grades"));
            menuBar.add(studentMenu);

        } else if (role.equalsIgnoreCase("instructor")) {
            // <--- 2. ENABLED INSTRUCTOR DASHBOARD HERE
            dashboard = new InstructorDashboard(this, this.loggedInUserId);
            statusBar.setText("Logged in as: Instructor (ID: " + this.loggedInUserId + ")");

            JMenu instructorMenu = new JMenu("My Sections");
            instructorMenu.add(new JMenuItem("Enter Grades"));
            menuBar.add(instructorMenu);
        }

        // 4. Show the new dashboard panel
        mainContentPanel.removeAll();
        if (dashboard != null) {
            mainContentPanel.add(dashboard, BorderLayout.CENTER);
        } else {
            mainContentPanel.add(new JLabel("Welcome, " + role + "! Dashboard is under construction.", SwingConstants.CENTER));
        }

        mainContentPanel.revalidate();
        mainContentPanel.repaint();
        menuBar.revalidate();
        menuBar.repaint();
    }

    public void showPanel(JPanel panel) {
        mainContentPanel.removeAll();
        mainContentPanel.add(panel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }
}