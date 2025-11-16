package edu.univ.erp.student;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.StudentProfile;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JPanel {

    private MainFrame mainFrame;
    private StudentService studentService;
    private StudentProfile profile;
    private int loggedInUserId;

    // UI Components for profile
    private JLabel nameLabel;
    private JLabel rollNumberLabel;
    private JLabel programLabel;

    public StudentDashboard(MainFrame mainFrame, int userId) {
        this.mainFrame = mainFrame;
        this.loggedInUserId = userId;
        this.studentService = new StudentService();

        // --- Load Profile Data ---
        // We load the student's profile as soon as the dashboard is created
        this.profile = studentService.getStudentProfile(loggedInUserId);

        // --- Setup Layout ---
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        // --- 1. Left Panel (Navigation) ---
        JPanel navigationPanel = new JPanel(new MigLayout("wrap 1", "[grow, fill]"));
        navigationPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));

        navigationPanel.add(new JButton("My Profile"), "sg button");
        navigationPanel.add(new JButton("Course Registration"), "sg button");
        navigationPanel.add(new JButton("View Grades"), "sg button");
        navigationPanel.add(new JButton("View Timetable"), "sg button");

        add(navigationPanel, BorderLayout.WEST);

        // --- 2. Center Panel (Welcome & Profile Info) ---
        JPanel centerPanel = new JPanel(new MigLayout("wrap 1"));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Welcome"));

        // Initialize labels
        nameLabel = new JLabel();
        rollNumberLabel = new JLabel();
        programLabel = new JLabel();

        // Populate labels with profile data
        if (this.profile != null) {
            nameLabel.setText("Name: " + profile.getFullName());
            rollNumberLabel.setText("Roll Number: " + profile.getRollNumber());
            programLabel.setText("Program: " + profile.getProgram());
        } else {
            // This happens if the student user isn't linked to a student profile
            nameLabel.setText("Error: Could not load student profile.");
        }

        // Style them
        Font profileFont = new Font("Arial", Font.PLAIN, 16);
        nameLabel.setFont(profileFont);
        rollNumberLabel.setFont(profileFont);
        programLabel.setFont(profileFont);
        
        centerPanel.add(nameLabel);
        centerPanel.add(rollNumberLabel);
        centerPanel.add(programLabel);
        
        add(centerPanel, BorderLayout.CENTER);
    }
}