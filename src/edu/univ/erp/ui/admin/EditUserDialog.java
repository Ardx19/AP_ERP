package edu.univ.erp.ui.admin;

import edu.univ.erp.auth.AdminService;
import edu.univ.erp.model.InstructorProfile;
import edu.univ.erp.model.StudentProfile;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * A custom JDialog for editing a user's profile and resetting their password.
 */
public class EditUserDialog extends JDialog {

    private AdminService adminService;
    private String role;
    private int userId;

    // Profile objects
    private StudentProfile studentProfile;
    private InstructorProfile instructorProfile;

    // UI Components
    private JTextField fullNameField;
    private JTextField roleSpecificField; // For Program or Department
    private JLabel roleSpecificLabel;

    public EditUserDialog(JFrame parent, AdminService adminService, int userId, String loginName, String role) {
        super(parent, "Edit User: " + loginName, true); // Modal
        this.adminService = adminService;
        this.userId = userId;
        this.role = role;

        setLayout(new MigLayout("wrap 2", "[right]rel[grow,fill]"));
        setSize(450, 250);
        setLocationRelativeTo(parent);

        // --- 1. Profile Information Panel ---
        JPanel profilePanel = new JPanel(new MigLayout("wrap 2", "[right]rel[grow,fill]"));
        profilePanel.setBorder(BorderFactory.createTitledBorder("User Profile"));

        // Login Name (Read-only)
        profilePanel.add(new JLabel("Login Name:"));
        JTextField loginNameField = new JTextField(loginName);
        loginNameField.setEditable(false);
        profilePanel.add(loginNameField, "width 200:300:");

        // Full Name
        profilePanel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField();
        profilePanel.add(fullNameField);

        // Role-Specific Field (Program/Department)
        roleSpecificLabel = new JLabel("Program/Dept:");
        profilePanel.add(roleSpecificLabel);
        roleSpecificField = new JTextField();
        profilePanel.add(roleSpecificField);
        
        JButton saveProfileButton = new JButton("Save Profile Changes");
        profilePanel.add(saveProfileButton, "skip 1, span 1, align right, gaptop 10");
        
        add(profilePanel, "span 2, growx");

        // --- 2. Password Reset Panel ---
        JPanel passwordPanel = new JPanel(new MigLayout());
        passwordPanel.setBorder(BorderFactory.createTitledBorder("Security"));
        
        JButton resetPasswordButton = new JButton("Reset Password...");
        passwordPanel.add(resetPasswordButton);
        
        add(passwordPanel, "span 2, growx");

        // --- Load Data ---
        loadUserData();

        // --- Action Listeners ---
        saveProfileButton.addActionListener(e -> {
            saveProfileChanges();
        });

        resetPasswordButton.addActionListener(e -> {
            resetPassword();
        });
    }

    /**
     * Loads the user's profile data based on their role.
     */
    private void loadUserData() {
        if (role.equals("student")) {
            roleSpecificLabel.setText("Program:");
            studentProfile = adminService.getStudentProfile(userId);
            if (studentProfile != null) {
                fullNameField.setText(studentProfile.getFullName());
                roleSpecificField.setText(studentProfile.getProgram());
            }
        } else if (role.equals("instructor")) {
            roleSpecificLabel.setText("Department:");
            instructorProfile = adminService.getInstructorProfile(userId);
            if (instructorProfile != null) {
                fullNameField.setText(instructorProfile.getFullName());
                roleSpecificField.setText(instructorProfile.getDepartment());
            }
        } else if (role.equals("admin")) {
            // Admin has no erp_db profile
            roleSpecificLabel.setVisible(false);
            roleSpecificField.setVisible(false);
            fullNameField.setText("N/A (Admin)");
            fullNameField.setEditable(false);
        }
    }

    /**
     * Saves changes to the student/instructor profile.
     */
    private void saveProfileChanges() {
        String newName = fullNameField.getText();
        String newRoleSpec = roleSpecificField.getText();
        boolean success = false;

        if (role.equals("student") && studentProfile != null) {
            success = adminService.updateStudentProfile(studentProfile.getStudentId(), newName, newRoleSpec);
        } else if (role.equals("instructor") && instructorProfile != null) {
            success = adminService.updateInstructorProfile(instructorProfile.getInstructorId(), newName, newRoleSpec);
        } else if (role.equals("admin")) {
            JOptionPane.showMessageDialog(this, "Admin profile cannot be edited here.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Error: Could not update profile.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Prompts for and resets the user's password.
     */
    private void resetPassword() {
        String newPassword = JOptionPane.showInputDialog(this, 
                "Enter new password for user ID " + userId + ":", 
                "Reset Password", 
                JOptionPane.WARNING_MESSAGE);
        
        if (newPassword != null && !newPassword.isEmpty()) {
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = adminService.resetUserPassword(userId, newPassword);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Could not reset password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}