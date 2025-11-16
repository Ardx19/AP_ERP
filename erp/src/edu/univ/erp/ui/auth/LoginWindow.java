package edu.univ.erp.ui.auth;

import com.formdev.flatlaf.FlatLightLaf;
import net.miginfocom.swing.MigLayout;
import edu.univ.erp.auth.AuthService; 
import edu.univ.erp.ui.MainFrame; // Import MainFrame
import edu.univ.erp.auth.AuthService.User; // Import the new inner class
import javax.swing.*;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {

    private AuthService authService;

    // UI components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginWindow() {
        this.authService = new AuthService();
        FlatLightLaf.setup();

        setTitle("University ERP - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 

        JPanel panel = new JPanel(new MigLayout("wrap 2", "[right][grow, fill]"));

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

        add(panel);
        loginButton.addActionListener(this::performLogin);
    }

    /**
     * This method is called when the "Login" button is clicked.
     * It now opens the MainFrame on success.
     */
    private void performLogin(ActionEvent e) {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());

    User user = authService.login(username, password); // Returns a User object

    if (user != null) {
        // --- LOGIN SUCCESS ---
        MainFrame mainFrame = new MainFrame();
        // Pass both the role AND the userId
        mainFrame.showDashboard(user.role, user.userId); 
        mainFrame.setVisible(true);

        this.setVisible(false);
        this.dispose();

    } else {
        // Login failed
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