import net.proteanit.sql.DbUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Student {
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField textSName;
    private JTextField txtSId;
    private JTextField textBook;
    private JPanel Main;
    private JTextField textSearch;
    private JTable table1;
    private DbUtils Dbutils;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Student");
        frame.setContentPane(new Student().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
        Connection con;
        PreparedStatement pst;
    public void connect() {

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");

            // Establish the connection
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/data_student", "root", "1111");
            System.out.println("Connection established successfully.");
        } catch (ClassNotFoundException ex) {
            System.err.println("JDBC Driver not found.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("SQL Exception: Unable to establish connection.");
            ex.printStackTrace();
        }
    }

    void tableLoad()
    {
        try
        {
            pst = con.prepareStatement("select * from student_table");
            ResultSet rs=pst.executeQuery();

            table1.setModel(Dbutils.resultSetToTableModel(rs));
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }


    public Student() {
        connect();
        tableLoad();
        Main.setBackground(new Color(173, 216, 230));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get text from text fields
                String Student_Name = textSName.getText();
                String Student_ID = txtSId.getText();
                String Book_Name = textBook.getText();

                // Check for empty fields (optional but recommended)
                if (Student_Name.isEmpty() || Student_ID.isEmpty() || Book_Name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled!");
                    return;
                }

                try {
                    // Prepare SQL statement
                    pst = con.prepareStatement("INSERT INTO student_table(Student_Name, Student_ID, Book_Name) VALUES (?, ?, ?)");
                    pst.setString(1, Student_Name);
                    pst.setString(2, Student_ID);
                    pst.setString(3, Book_Name);

                    // Execute update
                    int rowsAffected = pst.executeUpdate();

                    // Check if insert was successful
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Record added!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to add record!");
                    }

                    // Clear text fields
                    textSName.setText("");
                    txtSId.setText("");
                    textBook.setText("");
                    // Optionally reload the table or refresh the UI
                    tableLoad();

                } catch (SQLException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage());
                }
            }

        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String stdId = textSearch.getText();

                    pst = con.prepareStatement("SELECT Student_Name, Student_ID, Book_Name FROM student_table WHERE Student_ID = ?");
                    pst.setString(1, stdId);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        String Student_Name = rs.getString("Student_Name");
                        String Student_ID = rs.getString("Student_ID");
                        String Book_Name = rs.getString("Book_Name");

                        // Create the message to display in the dialog box
                        String message = "Name: " + Student_Name + "\n" +
                                "Id: " + Student_ID + "\n" +
                                "Book: " + Book_Name;

                        // Display the message in a dialog box
                        JOptionPane.showMessageDialog(null, message, "Student Details", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        // Clear text fields if student is not found
                        textSName.setText("");
                        txtSId.setText("");
                        textBook.setText("");

                        // Display the "Invalid Student" message in a dialog box
                        JOptionPane.showMessageDialog(null, "Invalid Student", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                catch(SQLException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Id = txtSId.getText();

                if (Id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter an ID to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        pst = con.prepareStatement("DELETE FROM student_table WHERE Student_ID = ?");
                        pst.setString(1, Id);
                        int rowsAffected = pst.executeUpdate();

                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Record Deleted!");
                            tableLoad();
                            textSName.setText("");
                            txtSId.setText("");
                            textBook.setText("");
                            txtSId.requestFocus();
                        } else {
                            JOptionPane.showMessageDialog(null, "No record found with the given Id.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String Id, Book;

                Id = txtSId.getText();
                Book = textBook.getText();

                try {
                    // Update only the Book column where the Id matches
                    pst = con.prepareStatement("UPDATE student_table SET Book_Name = ? WHERE Student_ID = ?");
                    pst.setString(1, Book);
                    pst.setString(2, Id);

                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Book Updated!");
                        tableLoad();
                        textSName.setText("");
                        txtSId.setText("");
                        textBook.setText("");
                        txtSId.requestFocus();
                    } else {
                        JOptionPane.showMessageDialog(null, "No record found with the given Id.");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
