package edu.univ.erp.student;

import edu.univ.erp.model.StudentProfile;
import edu.univ.erp.ui.MainFrame;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * A panel for the student to view and edit their own profile.
 */
public class StudentProfilePanel extends JPanel {

    private MainFrame mainFrame;
    private StudentService studentService;
    private StudentProfile profile; // Holds the student's data

    // UI Components
    private JTextField rollNumberField;
    private JTextField fullNameField;
    private JTextField programField;
    private JButton saveButton;
    private JButton backButton;

    public StudentProfilePanel(MainFrame mainFrame, StudentProfile profile, StudentService service) {
        this.mainFrame = mainFrame;
        this.profile = profile;
        this.studentService = service;

        setLayout(new BorderLayout(10, 10));

        // --- 1. Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        
        backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Profile Form ---
        JPanel formPanel = new JPanel(new MigLayout("wrap 2", "[right]rel[grow,fill]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Edit Your Information"));

        // Roll Number (Read-only)
        formPanel.add(new JLabel("Roll Number:"));
        rollNumberField = new JTextField(profile.getRollNumber());
        rollNumberField.setEditable(false);
        formPanel.add(rollNumberField, "width 200:300:");

        // Full Name (Editable)
        formPanel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField(profile.getFullName());
        formPanel.add(fullNameField);

        // Program (Editable)
        formPanel.add(new JLabel("Program:"));
        programField = new JTextField(profile.getProgram());
        formPanel.add(programField);

        // Save Button
        saveButton = new JButton("Save Changes");
        formPanel.add(saveButton, "skip 1, span 1, align right, gaptop 20");
        
        add(formPanel, BorderLayout.CENTER);

        // --- 3. Action Listeners ---
        
        backButton.addActionListener(e -> {
            // Go back to the student dashboard
            mainFrame.showPanel(new StudentDashboard(mainFrame, profile.getUserId()));
        });

        saveButton.addActionListener(e -> {
            // Get the new values from the text fields
            String newName = fullNameField.getText();
            String newProgram = programField.getText();

            // Call the service to update the database
            boolean success = studentService.updateStudentProfile(profile.getStudentId(), newName, newProgram);

            if (success) {
                // Update the local profile object so the changes are immediate
                this.profile = new StudentProfile(
                    profile.getStudentId(),
                    profile.getUserId(),
                    profile.getRollNumber(),
                    newName, // use new name
                    newProgram // use new program
                );
                
                JOptionPane.showMessageDialog(mainFrame, "Profile updated successfully!");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Error: Could not update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}