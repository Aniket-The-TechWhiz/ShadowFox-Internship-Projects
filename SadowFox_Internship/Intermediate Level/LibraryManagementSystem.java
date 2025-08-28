import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibraryManagementSystem {

    private static final String DATABASE_URL = "jdbc:sqlite:library.db";

    private JFrame loginFrame;
    private JFrame mainFrame;
    private JTabbedPane tabbedPane;
    private JTable bookTable, userTable, loanTable;
    private DefaultTableModel bookTableModel, userTableModel, loanTableModel;
    private JTextField loginUsernameField, loginPasswordField;
    private JLabel statusLabel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryManagementSystem app = new LibraryManagementSystem();
            app.initializeDatabase();
            app.showLoginScreen();
        });
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {

            String createBooksTable = "CREATE TABLE IF NOT EXISTS books ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "title TEXT NOT NULL,"
                    + "author TEXT NOT NULL,"
                    + "is_available INTEGER NOT NULL CHECK (is_available IN (0, 1))"
                    + ");";
            stmt.execute(createBooksTable);

            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "username TEXT NOT NULL UNIQUE,"
                    + "password TEXT NOT NULL"
                    + ");";
            stmt.execute(createUsersTable);

            String createLoansTable = "CREATE TABLE IF NOT EXISTS loans ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "book_id INTEGER NOT NULL,"
                    + "user_id INTEGER NOT NULL,"
                    + "issue_date TEXT NOT NULL,"
                    + "return_date TEXT,"
                    + "FOREIGN KEY (book_id) REFERENCES books(id),"
                    + "FOREIGN KEY (user_id) REFERENCES users(id)"
                    + ");";
            stmt.execute(createLoansTable);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users;");
            if (rs.getInt(1) == 0) {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?);");
                ps.setString(1, "admin");
                ps.setString(2, "password");
                ps.executeUpdate();
            }

            rs = stmt.executeQuery("SELECT COUNT(*) FROM books;");
            if (rs.getInt(1) == 0) {
                String insertBook1 = "INSERT INTO books (title, author, is_available) VALUES ('The Lord of the Rings', 'J.R.R. Tolkien', 1);";
                String insertBook2 = "INSERT INTO books (title, author, is_available) VALUES ('The Hobbit', 'J.R.R. Tolkien', 1);";
                String insertBook3 = "INSERT INTO books (title, author, is_available) VALUES ('Dune', 'Frank Herbert', 1);";
                stmt.executeUpdate(insertBook1);
                stmt.executeUpdate(insertBook2);
                stmt.executeUpdate(insertBook3);
            }
            
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database initialization failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLoginScreen() {
        loginFrame = new JFrame("Library Login");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setSize(400, 250);
        loginFrame.setLayout(new BorderLayout(10, 10));
        loginFrame.setLocationRelativeTo(null);

        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        loginPanel.add(new JLabel("Username:"));
        loginUsernameField = new JTextField();
        loginPanel.add(loginUsernameField);

        loginPanel.add(new JLabel("Password:"));
        loginPasswordField = new JPasswordField();
        loginPanel.add(loginPasswordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> attemptLogin());

        loginFrame.add(loginPanel, BorderLayout.CENTER);
        loginFrame.add(loginButton, BorderLayout.SOUTH);
        loginFrame.setVisible(true);
    }

    private void attemptLogin() {
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?;")) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                loginFrame.dispose();
                showMainApplication();
            } else {
                JOptionPane.showMessageDialog(loginFrame, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(loginFrame, "Login error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMainApplication() {
        mainFrame = new JFrame("Library Management System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Books", createBooksPanel());
        tabbedPane.addTab("Users", createUsersPanel());
        tabbedPane.addTab("Loans", createLoansPanel());
        
        mainFrame.add(tabbedPane, BorderLayout.CENTER);

        statusLabel = new JLabel("Status: Ready");
        mainFrame.add(statusLabel, BorderLayout.SOUTH);

        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        loadBookData();
        loadUserData();
        loadLoanData();

        mainFrame.setVisible(true);
    }

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        String[] columnNames = {"ID", "Title", "Author", "Available"};
        bookTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        JScrollPane scrollPane = new JScrollPane(bookTable);
        
        JPanel buttonPanel = new JPanel();
        JButton addBookButton = new JButton("Add New Book");
        JButton issueBookButton = new JButton("Issue Selected Book");
        JButton recommendButton = new JButton("Recommend Books");

        addBookButton.addActionListener(e -> showAddBookDialog());
        issueBookButton.addActionListener(e -> issueBook());
        recommendButton.addActionListener(e -> showRecommendations());

        buttonPanel.add(addBookButton);
        buttonPanel.add(issueBookButton);
        buttonPanel.add(recommendButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        String[] columnNames = {"ID", "Username"};
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        
        JPanel buttonPanel = new JPanel();
        JButton addUserButton = new JButton("Add New User");

        addUserButton.addActionListener(e -> showAddUserDialog());
        
        buttonPanel.add(addUserButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        
        String[] columnNames = {"Loan ID", "Book ID", "Book Title", "User ID", "Username", "Issue Date", "Return Date"};
        loanTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        loanTable = new JTable(loanTableModel);
        JScrollPane scrollPane = new JScrollPane(loanTable);

        JPanel buttonPanel = new JPanel();
        JButton returnBookButton = new JButton("Return Selected Book");
        
        returnBookButton.addActionListener(e -> returnBook());
        
        buttonPanel.add(returnBookButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadBookData() {
        bookTableModel.setRowCount(0);
        String sql = "SELECT * FROM books;";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                boolean isAvailable = rs.getInt("is_available") == 1;
                bookTableModel.addRow(new Object[]{id, title, author, isAvailable});
            }
        } catch (SQLException e) {
            statusLabel.setText("Status: Failed to load books.");
            e.printStackTrace();
        }
        statusLabel.setText("Status: Books loaded.");
    }
    
    private void loadUserData() {
        userTableModel.setRowCount(0);
        String sql = "SELECT id, username FROM users;";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                userTableModel.addRow(new Object[]{id, username});
            }
        } catch (SQLException e) {
            statusLabel.setText("Status: Failed to load users.");
            e.printStackTrace();
        }
        statusLabel.setText("Status: Users loaded.");
    }

    private void loadLoanData() {
        loanTableModel.setRowCount(0);
        String sql = "SELECT l.id, b.id as book_id, b.title, u.id as user_id, u.username, l.issue_date, l.return_date "
                   + "FROM loans l "
                   + "JOIN books b ON l.book_id = b.id "
                   + "JOIN users u ON l.user_id = u.id;";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = new Object[]{
                    rs.getInt("id"),
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("issue_date"),
                    rs.getString("return_date")
                };
                loanTableModel.addRow(row);
            }
        } catch (SQLException e) {
            statusLabel.setText("Status: Failed to load loans.");
            e.printStackTrace();
        }
        statusLabel.setText("Status: Loans loaded.");
    }
    
    private void showAddBookDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New Book", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(mainFrame);
        
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        
        dialog.add(new JLabel("Title:"));
        dialog.add(titleField);
        dialog.add(new JLabel("Author:"));
        dialog.add(authorField);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            if (title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and Author cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO books (title, author, is_available) VALUES (?, ?, 1);")) {
                ps.setString(1, title);
                ps.setString(2, author);
                ps.executeUpdate();
                statusLabel.setText("Status: Book added successfully.");
                dialog.dispose();
                loadBookData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding book: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(saveButton);
        dialog.setVisible(true);
    }
    
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(mainFrame, "Add New User", true);
        dialog.setLayout(new GridLayout(3, 2, 10, 10));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(mainFrame);
        
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        
        dialog.add(new JLabel("Username:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Password:"));
        dialog.add(passwordField);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username and Password cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try (Connection conn = DriverManager.getConnection(DATABASE_URL);
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?);")) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.executeUpdate();
                statusLabel.setText("Status: User added successfully.");
                dialog.dispose();
                loadUserData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding user: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(saveButton);
        dialog.setVisible(true);
    }
    
    private void issueBook() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a book to issue.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (int) bookTableModel.getValueAt(selectedRow, 0);
        boolean isAvailable = (boolean) bookTableModel.getValueAt(selectedRow, 3);
        
        if (!isAvailable) {
            JOptionPane.showMessageDialog(mainFrame, "Selected book is not available.", "Action Forbidden", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int selectedUserRow = userTable.getSelectedRow();
        if (selectedUserRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a user to issue the book to.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int userId = (int) userTableModel.getValueAt(selectedUserRow, 0);

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET is_available = 0 WHERE id = ?;");
             PreparedStatement insertLoan = conn.prepareStatement("INSERT INTO loans (book_id, user_id, issue_date) VALUES (?, ?, ?);")) {

            conn.setAutoCommit(false);

            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            insertLoan.setInt(1, bookId);
            insertLoan.setInt(2, userId);
            insertLoan.setString(3, LocalDate.now().toString());
            insertLoan.executeUpdate();

            conn.commit();
            statusLabel.setText("Status: Book issued successfully.");

            loadBookData();
            loadLoanData();

        } catch (SQLException ex) {
            statusLabel.setText("Status: Failed to issue book.");
            JOptionPane.showMessageDialog(mainFrame, "Error issuing book: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void returnBook() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a loan to return.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int loanId = (int) loanTableModel.getValueAt(selectedRow, 0);
        int bookId = (int) loanTableModel.getValueAt(selectedRow, 1);
        String returnDate = (String) loanTableModel.getValueAt(selectedRow, 6);
        
        if (returnDate != null) {
            JOptionPane.showMessageDialog(mainFrame, "This book has already been returned.", "Action Forbidden", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement updateLoan = conn.prepareStatement("UPDATE loans SET return_date = ? WHERE id = ?;");
             PreparedStatement updateBook = conn.prepareStatement("UPDATE books SET is_available = 1 WHERE id = ?;")) {

            conn.setAutoCommit(false);
            
            updateLoan.setString(1, LocalDate.now().toString());
            updateLoan.setInt(2, loanId);
            updateLoan.executeUpdate();

            updateBook.setInt(1, bookId);
            updateBook.executeUpdate();

            conn.commit();
            statusLabel.setText("Status: Book returned successfully.");
            
            loadBookData();
            loadLoanData();
            
        } catch (SQLException ex) {
            statusLabel.setText("Status: Failed to return book.");
            JOptionPane.showMessageDialog(mainFrame, "Error returning book: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRecommendations() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainFrame, "Please select a book to get recommendations.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String author = (String) bookTableModel.getValueAt(selectedRow, 2);
        
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement ps = conn.prepareStatement("SELECT title FROM books WHERE author = ? AND id != ?;")) {
            
            int selectedBookId = (int) bookTableModel.getValueAt(selectedRow, 0);
            ps.setString(1, author);
            ps.setInt(2, selectedBookId);
            
            ResultSet rs = ps.executeQuery(); 
            List<String> recommendedBooks = new ArrayList<>();
            while(rs.next()) {
                recommendedBooks.add(rs.getString("title"));
            }

            if (recommendedBooks.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "No other books found by this author.", "Recommendations", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder("You might also like books by " + author + ":\n");
                for (String title : recommendedBooks) {
                    message.append("- ").append(title).append("\n");
                }
                JOptionPane.showMessageDialog(mainFrame, message.toString(), "Book Recommendations", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error getting recommendations: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
