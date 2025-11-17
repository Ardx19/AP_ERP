package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.MainFrame; 
import net.miginfocom.swing.MigLayout;

// Import all three panels
import edu.univ.erp.ui.admin.ManageUsersPanel;
import edu.univ.erp.ui.admin.ManageCoursesPanel;
import edu.univ.erp.ui.admin.ManageSectionsPanel;


import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JPanel {

    private MainFrame mainFrame; 

    public AdminDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame; 

        setLayout(new MigLayout("wrap 4", "[grow, fill]", "[sg]"));

        JLabel title = new JLabel("Administrator Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        add(title, "span 4, align center, gapbottom 20");

        // --- Create Buttons ---
        JButton manageUsers = new JButton("Manage Users (S/I)");
        JButton manageCourses = new JButton("Manage Courses");
        JButton manageSections = new JButton("Manage Sections"); // The button variable
        JButton toggleMaintenance = new JButton("Toggle Maintenance Mode");
        JButton viewLogs = new JButton("View System Logs");
        
        // --- Add Buttons to Panel ---
        add(manageUsers);
        add(manageCourses);
        add(manageSections);
        add(toggleMaintenance);
        add(viewLogs);

        // --- Action Listeners (All in one place) ---
        
        // 1. Manage Users
        manageUsers.addActionListener(e -> {
            mainFrame.showPanel(new ManageUsersPanel(mainFrame));
        });
        
        // 2. Manage Courses
        manageCourses.addActionListener(e -> {
            mainFrame.showPanel(new ManageCoursesPanel(mainFrame));
        });

        // 3. Manage Sections
        manageSections.addActionListener(e -> { 
            mainFrame.showPanel(new ManageSectionsPanel(mainFrame));
        });
        
        // 4. Placeholder
        toggleMaintenance.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Maintenance Mode feature not built yet.");
        });
    }
}