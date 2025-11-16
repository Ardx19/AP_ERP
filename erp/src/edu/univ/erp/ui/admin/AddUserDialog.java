package edu.univ.erp.ui.admin;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;

public class AddUserDialog extends JDialog {

    private JTextField loginNameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox; 
    private JButton okButton;
    private JButton cancelButton;

    private boolean succeeded = false; 

    public AddUserDialog(JFrame parent) {
        super(parent, "Add New User", true); 

        setLayout(new MigLayout("wrap 2", "[right][grow, fill]"));
        setSize(350, 200);
        setLocationRelativeTo(parent); 

        add(new JLabel("Login Name:"));
        loginNameField = new JTextField();
        add(loginNameField, "width 150:200:");

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Role:"));
        String[] roles = {"student", "instructor", "admin"};
        roleComboBox = new JComboBox<>(roles);
        add(roleComboBox);

        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        add(new JLabel(), "span 2, split 2, align right"); 
        add(cancelButton);
        add(okButton);

        okButton.addActionListener(e -> {
            succeeded = true;
            this.setVisible(false);
        });

        cancelButton.addActionListener(e -> {
            succeeded = false;
            this.setVisible(false);
        });
    }

    // --- Public methods to get the data ---

    public boolean isSucceeded() {
        return succeeded;
    }

    public String getLoginName() {
        return loginNameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public String getSelectedRole() {
        return (String) roleComboBox.getSelectedItem();
    }
}