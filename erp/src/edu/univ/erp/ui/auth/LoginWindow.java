package edu.univ.erp.ui.auth;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;

// --- NEW IMPORT ---
// Import the AuthService you just created
import edu.univ.erp.auth.AuthService; 

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {

    // --- NEW ---
    // Create an instance of the AuthService to handle login logic
    private AuthService authService;

    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginWindow() {
        // --- NEW ---
        // Initialize the AuthService
        this.authService = new AuthService();

        // --- 1. Set up the Look and Feel (FlatLaf) ---
        FlatLightLaf.setup();

        // --- 2. Basic Window Setup ---
        setTitle("University ERP - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        // --- 3. Set the Layout Manager (MigLayout) ---
        JPanel panel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]"));

        // --- 4. Create and Add Components ---
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField, "width 200:250:"); 

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField); 

        loginButton = new JButton("Login");
        panel.add(loginButton, "skip 1, span 1, align right"); 

        statusLabel = new JLabel("Please enter your credentials.");
        panel.add(statusLabel, "span 2, align center"); 

        // --- 5. Add Panel to Window ---
        add(panel);

        // --- 6. Add the Login Button's Action ---
        loginButton.addActionListener(this::performLogin);
    }

    /**
     * This method is called when the "Login" button is clicked.
     * It now uses the AuthService to check the database.
     */
    private void performLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // --- MODIFIED LOGIC ---
        // Call the AuthService to check the credentials
        String userRole = authService.login(username, password);

        if (userRole != null) {
            // Login was successful!
            statusLabel.setText("Login Successful! Role: " + userRole);

            // --- NEXT STEP ---
            // Here you will hide the login window:
            // this.setVisible(false);
            
            // And open the correct dashboard based on the role:
            // if (userRole.equals("admin")) {
            //     new AdminDashboard().setVisible(true);
            // } else if (userRole.equals("student")) {
            //     new StudentDashboard().setVisible(true);
            // } else if (userRole.equals("instructor")) {
            //     new InstructorDashboard().setVisible(true);
            // }
            
            // We will do this after we create the dashboards.

        } else {
            // Login failed (user not found or wrong password)
            statusLabel.setText("Invalid username or password.");
        }
    }


    /**
     * The main method to run this window by itself.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow login = new LoginWindow();
            login.setVisible(true);
        });
    }
}

