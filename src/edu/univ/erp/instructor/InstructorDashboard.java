package edu.univ.erp.instructor;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.InstructorProfile;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class InstructorDashboard extends JPanel {

    private MainFrame mainFrame;
    private InstructorService instructorService;
    private InstructorProfile profile;
    private int loggedInUserId;

    // UI Components for profile
    private JLabel nameLabel;
    private JLabel departmentLabel;

    public InstructorDashboard(MainFrame mainFrame, int userId) {
        this.mainFrame = mainFrame;
        this.loggedInUserId = userId;
        this.instructorService = new InstructorService();

        // --- Load Profile Data ---
        this.profile = instructorService.getInstructorProfile(loggedInUserId);

        // --- Setup Layout ---
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Instructor Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // --- 1. Left Panel (Navigation) ---
        JPanel navigationPanel = new JPanel(new MigLayout("wrap 1", "[grow, fill]"));
        navigationPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        navigationPanel.add(new JButton("My Profile"), "sg button");
        JButton sectionsButton = new JButton("My Sections");
        navigationPanel.add(sectionsButton, "sg button");
        sectionsButton.addActionListener(e -> {
            if (profile != null) {
                mainFrame.showPanel(new MySectionsPanel(mainFrame, profile, instructorService));
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Could not load profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        navigationPanel.add(new JButton("Enter Grades"), "sg button");

        add(navigationPanel, BorderLayout.WEST);

        // --- 2. Center Panel (Welcome & Profile Info) ---
        JPanel centerPanel = new JPanel(new MigLayout("wrap 1"));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Welcome"));

        // Initialize labels
        nameLabel = new JLabel();
        departmentLabel = new JLabel();

        // Populate labels with profile data
        if (this.profile != null) {
            nameLabel.setText("Name: " + profile.getFullName());
            departmentLabel.setText("Department: " + profile.getDepartment());
        } else {
            nameLabel.setText("Error: Could not load instructor profile.");
        }

        // Style them
        Font profileFont = new Font("Arial", Font.PLAIN, 16);
        nameLabel.setFont(profileFont);
        departmentLabel.setFont(profileFont);
        
        centerPanel.add(nameLabel);
        centerPanel.add(departmentLabel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}