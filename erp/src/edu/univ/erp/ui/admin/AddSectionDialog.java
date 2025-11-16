package edu.univ.erp.ui.admin;

import edu.univ.erp.model.Course;
import edu.univ.erp.model.InstructorDisplay;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A custom JDialog for adding a new course section.
 */
public class AddSectionDialog extends JDialog {

    private JComboBox<Course> courseComboBox;
    private JComboBox<InstructorDisplay> instructorComboBox;
    private JTextField semesterField;
    private JSpinner yearSpinner;
    private JSpinner capacitySpinner;
    private JButton okButton;
    private JButton cancelButton;

    private boolean succeeded = false;

    public AddSectionDialog(JFrame parent, List<Course> courses, List<InstructorDisplay> instructors) {
        super(parent, "Add New Section", true); // Modal
        setLayout(new MigLayout("wrap 2", "[right]rel[grow,fill]"));
        setSize(450, 280);
        setLocationRelativeTo(parent);

        // --- 1. Course Dropdown ---
        add(new JLabel("Course:"));
        courseComboBox = new JComboBox<>(courses.toArray(new Course[0]));
        // Custom renderer to show course code and title
        courseComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Course) {
                    Course c = (Course) value;
                    setText(c.getCourseCode() + " - " + c.getTitle());
                }
                return this;
            }
        });
        add(courseComboBox, "width 200:300:");

        // --- 2. Instructor Dropdown ---
        add(new JLabel("Instructor:"));
        // We add a "null" option to allow unassigned instructors
        instructorComboBox = new JComboBox<>();
        instructorComboBox.addItem(null); // The "Not Assigned" option
        for (InstructorDisplay i : instructors) {
            instructorComboBox.addItem(i);
        }
        add(instructorComboBox);

        // --- 3. Semester ---
        add(new JLabel("Semester:"));
        semesterField = new JTextField("Fall"); // Default value
        add(semesterField);

        // --- 4. Year ---
        add(new JLabel("Year:"));
        // Spinner for the year, defaulting to 2024
        yearSpinner = new JSpinner(new SpinnerNumberModel(2024, 2020, 2030, 1));
        add(yearSpinner, "width 80::");

        // --- 5. Capacity ---
        add(new JLabel("Capacity:"));
        // Spinner for capacity, from 1 to 200
        capacitySpinner = new JSpinner(new SpinnerNumberModel(50, 1, 200, 1));
        add(capacitySpinner, "width 80::");

        // --- 6. Buttons ---
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        add(new JLabel(), "span 2, split 2, align right, gaptop 20"); // Spacer
        add(cancelButton);
        add(okButton);

        // --- Actions ---
        okButton.addActionListener(e -> {
            succeeded = true;
            setVisible(false);
        });
        cancelButton.addActionListener(e -> {
            succeeded = false;
            setVisible(false);
        });
    }

    // --- Public Getters to retrieve the data ---
    
    public boolean isSucceeded() {
        return succeeded;
    }

    public Course getSelectedCourse() {
        return (Course) courseComboBox.getSelectedItem();
    }

    public InstructorDisplay getSelectedInstructor() {
        // Can be null
        return (InstructorDisplay) instructorComboBox.getSelectedItem();
    }

    public String getSemester() {
        return semesterField.getText();
    }

    public int getYear() {
        return (Integer) yearSpinner.getValue();
    }

    public int getCapacity() {
        return (Integer) capacitySpinner.getValue();
    }
}