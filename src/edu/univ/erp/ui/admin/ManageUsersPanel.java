package edu.univ.erp.ui.admin;

import edu.univ.erp.ui.MainFrame;
import net.miginfocom.swing.MigLayout;

import edu.univ.erp.auth.AdminService;
import edu.univ.erp.model.UserDisplay;
import java.util.List;
import edu.univ.erp.ui.admin.EditUserDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageUsersPanel extends JPanel {

    private JTable usersTable;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;
    private AdminService adminService;

    public ManageUsersPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.adminService = new AdminService();
        
        setLayout(new BorderLayout(10, 10)); 

        // --- 1. Top Panel (Title and Back Button) ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Manage Users (Students & Instructors)");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Center Panel (Table) ---
        String[] columnNames = {"User ID", "Login Name", "Role"};
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        usersTable = new JTable(tableModel);
        
        loadUserData(); // Load data from DB

        JScrollPane scrollPane = new JScrollPane(usersTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Right Panel (Action Buttons) ---
        JPanel actionPanel = new JPanel(new MigLayout("wrap 1", "[grow, fill]"));
        
        JButton addNewUserButton = new JButton("Add New User...");
        JButton editUserButton = new JButton("Edit Selected User...");
        editUserButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a user to edit.", "No User Selected", JOptionPane.WARNING_MESSAGE);
                return;
            } 
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String loginName = (String) tableModel.getValueAt(selectedRow, 1);
            String role = (String) tableModel.getValueAt(selectedRow, 2);
            EditUserDialog editDialog = new EditUserDialog(mainFrame, adminService, userId, loginName, role);
            editDialog.setVisible(true);   
            loadUserData();
        });    
        JButton deleteUserButton = new JButton("Delete Selected User");
        JButton resetPasswordButton = new JButton("Reset Password...");

        actionPanel.add(addNewUserButton);
        actionPanel.add(editUserButton);
        actionPanel.add(deleteUserButton);
        actionPanel.add(resetPasswordButton);
        
        add(actionPanel, BorderLayout.EAST);

        // --- 4. Action Listeners ---
        
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new AdminDashboard(mainFrame));
        });

        addNewUserButton.addActionListener(e -> {
            AddUserDialog dialog = new AddUserDialog(mainFrame);
            dialog.setVisible(true);

            if (dialog.isSucceeded()) {
                String loginName = dialog.getLoginName();
                String password = dialog.getPassword();
                String role = dialog.getSelectedRole();

                if (loginName.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Login Name and Password cannot be empty.", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (adminService.isLoginNameTaken(loginName)) {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "The login name '" + loginName + "' is already taken.", 
                        "Duplicate User", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = adminService.createNewUser(loginName, password, role);

                if (success) {
                    loadUserData(); // Reload the table
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Successfully created user: " + loginName);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Error: Could not create user.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        deleteUserButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "Please select a user from the table to delete.", 
                    "No User Selected", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String loginName = (String) tableModel.getValueAt(selectedRow, 1);
            String role = (String) tableModel.getValueAt(selectedRow, 2);

            if (loginName.equals("admin")) {
                JOptionPane.showMessageDialog(mainFrame, 
                    "You cannot delete the 'admin' user.", 
                    "Action Not Allowed", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            int choice = JOptionPane.showConfirmDialog(mainFrame, 
                "Are you sure you want to delete this user?\n\n" +
                "User: " + loginName + " (ID: " + userId + ")\n\n" +
                "This action cannot be undone.",
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                boolean success = adminService.deleteUser(userId, role);

                if (success) {
                    JOptionPane.showMessageDialog(mainFrame, "User deleted successfully.");
                    loadUserData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(mainFrame, 
                        "Error: Could not delete user.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Fetches user data from the AdminService and populates the table.
     */
    private void loadUserData() {
        tableModel.setRowCount(0); // Clear old data
        List<UserDisplay> users = adminService.getAllUsers();
        
        for (UserDisplay user : users) {
            Object[] row = new Object[] {
                user.getUserId(),
                user.getLoginName(),
                user.getUserRole()
            };
            tableModel.addRow(row);
        }
    }
}