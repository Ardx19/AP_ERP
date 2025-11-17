package edu.univ.erp.student;

import edu.univ.erp.ui.MainFrame;
import edu.univ.erp.model.FeeItem;
import edu.univ.erp.model.StudentProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * A panel for students to view and "pay" their fees.
 */
public class StudentFeesPanel extends JPanel {

    private MainFrame mainFrame;
    private StudentService studentService;
    private StudentProfile profile;

    private JTable feesTable;
    private DefaultTableModel tableModel;
    private JLabel totalDueLabel;

    public StudentFeesPanel(MainFrame mainFrame, StudentProfile profile, StudentService service) {
        this.mainFrame = mainFrame;
        this.profile = profile;
        this.studentService = service;

        setLayout(new BorderLayout(10, 10));

        // --- 1. Title and Back Button ---
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("My Fees Portal");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        JButton backButton = new JButton("← Back to Dashboard");
        topPanel.add(title, BorderLayout.CENTER);
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // --- 2. Fees Table ---
        String[] columnNames = {"ID", "Description", "Amount", "Status", "Due Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        feesTable = new JTable(tableModel);
        
        add(new JScrollPane(feesTable), BorderLayout.CENTER);

        // --- 3. Bottom Panel (Total & Pay Button) ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        totalDueLabel = new JLabel("Total Amount Due: $0.00");
        totalDueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JButton payButton = new JButton("Pay Selected 'Pending' Item");
        
        bottomPanel.add(totalDueLabel, BorderLayout.WEST);
        bottomPanel.add(payButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- 4. Load Data ---
        loadFeeData();

        // --- 5. Action Listeners ---
        backButton.addActionListener(e -> {
            mainFrame.showPanel(new StudentDashboard(mainFrame, profile.getUserId()));
        });

        payButton.addActionListener(e -> {
            int selectedRow = feesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(mainFrame, "Please select a 'Pending' item to pay.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int feeId = (int) tableModel.getValueAt(selectedRow, 0);
            String status = (String) tableModel.getValueAt(selectedRow, 3);

            if (!status.equals("Pending")) {
                JOptionPane.showMessageDialog(mainFrame, "This item is not 'Pending'.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Simulate payment
            boolean success = studentService.payFeeItem(feeId);
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "Payment successful!");
                loadFeeData(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Payment failed.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Helper method to load fee data into the table and calculate total due.
     */
    private void loadFeeData() {
        tableModel.setRowCount(0); 
        List<FeeItem> fees = studentService.getFeeItems(profile.getStudentId());
        
        BigDecimal totalDue = BigDecimal.ZERO;

        for (FeeItem f : fees) {
            tableModel.addRow(new Object[]{
                    f.getFeeId(),
                    f.getDescription(),
                    f.getAmount(),
                    f.getStatus(),
                    f.getDueDate()
            });
            // Add to total only if pending
            if (f.getStatus().equals("Pending")) {
                totalDue = totalDue.add(f.getAmount());
            }
        }
        
        // Update the total due label
        totalDueLabel.setText(String.format("Total Amount Due: $%.2f", totalDue));
    }
}