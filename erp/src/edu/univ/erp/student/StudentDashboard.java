package edu.univ.erp.student;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.ui.student.RegisterCoursePanel; // Import the new panel
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

        // 1A. Create Buttons with Variables
        JButton profileBtn = new JButton("My Profile");
        JButton registerBtn = new JButton("Course Registration");
        JButton gradesBtn = new JButton("View Grades");
        JButton timetableBtn = new JButton("View Timetable");

        // 1B. Add Action Listeners (The missing part!)
        registerBtn.addActionListener(e -> openRegistrationDialog());
        
        gradesBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Grades feature coming soon!"));
        timetableBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, "Timetable feature coming soon!"));

        // 1C. Add to Panel
        navigationPanel.add(profileBtn, "sg button");
        navigationPanel.add(registerBtn, "sg button");
        navigationPanel.add(gradesBtn, "sg button");
        navigationPanel.add(timetableBtn, "sg button");

        add(navigationPanel, BorderLayout.WEST);

        // --- 2. Center Panel (Welcome & Profile Info) ---
        JPanel centerPanel = new JPanel(new MigLayout("wrap 1"));
        centerPanel.setBorder(BorderFactory.createTitledBorder("Welcome"));

        nameLabel = new JLabel();
        rollNumberLabel = new JLabel();
        programLabel = new JLabel();

        if (this.profile != null) {
            nameLabel.setText("Name: " + profile.getFullName());
            rollNumberLabel.setText("Roll Number: " + profile.getRollNumber());
            programLabel.setText("Program: " + profile.getProgram());
        } else {
            nameLabel.setText("Error: Could not load student profile.");
        }

        Font profileFont = new Font("Arial", Font.PLAIN, 16);
        nameLabel.setFont(profileFont);
        rollNumberLabel.setFont(profileFont);
        programLabel.setFont(profileFont);
        
        centerPanel.add(nameLabel);
        centerPanel.add(rollNumberLabel);
        centerPanel.add(programLabel);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Opens the Course Registration Panel in a popup dialog.
     */
    private void openRegistrationDialog() {
        if (profile == null) {
             JOptionPane.showMessageDialog(this, "Profile not loaded. Cannot register.");
             return;
        }

        // Find the parent window to center the dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(parentWindow != null ? (Frame) parentWindow : null, "Course Registration", true);
        
        // Pass 'dialog::dispose' so the panel can close the dialog
        dialog.setContentPane(new RegisterCoursePanel(profile.getStudentId(), dialog::dispose));
        
        dialog.pack();
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}