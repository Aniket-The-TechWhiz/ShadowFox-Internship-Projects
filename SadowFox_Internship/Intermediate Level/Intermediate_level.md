## ⚙️ Intermediate Level Projects

This section outlines the intermediate-level projects completed during the internship, focusing on GUI development, database interaction, and robust unit testing.

-----

### 1\. Bank Account Management System with JUnit Testing

This project implements the core logic for a bank account, including deposits, withdrawals, interest calculations, and overdraft protection. It emphasizes **Test-Driven Development (TDD)** by providing a comprehensive suite of **JUnit 5 test cases** to ensure the reliability and correctness of the banking operations.

#### Features 🌟

  * **Deposits & Withdrawals:** Handle standard money transactions.
  * **Overdraft Logic:** Implements a defined overdraft limit (`-500.0`).
  * **Interest Calculation:** Applies monthly interest to positive balances.
  * **Transaction History:** Tracks all account activities in a list.
  * **Unit Testing:** Extensive JUnit 5 test cases (at least 5 per method) validate all functionalities.

#### How to Execute 🚀

This project is console-based and focuses on testing. You'll primarily interact with it by running its JUnit tests.

1.  **Navigate to the project directory:**

    ```bash
    cd Intermediate
    ```

2.  **Ensure JUnit 5 JARs are present:** Make sure you have the following JAR files in your `BankAccountManagement` folder (or in a `lib` subfolder if you configured `settings.json` accordingly):

      * `junit-jupiter-api-5.10.2.jar` (or the latest version)
      * `junit-platform-console-standalone-1.10.2.jar` (or the latest version)

3.  **Compile the Java code:**

    ```bash
    javac -cp ".;junit-platform-console-standalone-1.10.2.jar" BankAccount.java BankAccountTest.java
    ```

      * This command compiles both `BankAccount.java` (your main logic) and `BankAccountTest.java` (your tests), using the JUnit standalone JAR for its dependencies.

4.  **Run the JUnit tests:**

    ```bash
    java -jar junit-platform-console-standalone-1.10.2.jar -cp ".;junit-platform-console-standalone-1.10.2.jar" --scan-classpath
    ```

      * This command executes the JUnit test runner, which will discover and run all tests in `BankAccountTest.java`. The results (passed/failed tests) will be displayed in your console.

-----

### 3\. Library Management System with SQLite

This project develops a GUI-based application for managing a library's operations. It utilizes **SQLite** as an embedded database for data persistence, allowing you to store and retrieve information about books, users, and loan records. The system includes a user login, book issuance and return tracking, and a simple recommendation feature.

#### Features 🌟

  * **User Login System:** Secure access with username and password authentication.
  * **Database Persistence:** Stores all data (books, users, loans) in an `SQLite` database (`library.db`).
  * **GUI Interface:** A user-friendly Swing-based interface with tabbed panels for different management sections.
  * **Book Management:** Add new books with title and author.
  * **User Management:** Add new users to the system.
  * **Loan Tracking:** Issue books to users, record issue dates, and track returns with return dates.
  * **Book Recommendations:** Suggests other books by the same author as a selected book.
  * **Data Validation:** Basic input validation for forms.

#### How to Execute 🚀

1.  **Navigate to the project directory:**

    ```bash
    cd Intermediate/LibraryManagementSystem
    ```

2.  **Ensure SQLite JDBC driver is present:** Download the `sqlite-jdbc-x.x.x.x.jar` (e.g., `sqlite-jdbc-3.50.3.0.jar`) and place it directly in this `LibraryManagementSystem` folder. You can download it from [https://github.com/xerial/sqlite-jdbc/releases](https://github.com/xerial/sqlite-jdbc/releases).

3.  **Compile the Java code:**

    ```bash
    javac -cp ".;sqlite-jdbc-x.x.x.x.jar" LibraryManagementSystem.java
    ```

      * Replace `x.x.x.x` with the actual version number of your downloaded SQLite JDBC driver.

4.  **Run the application:**

    ```bash
    java -cp ".;sqlite-jdbc-x.x.x.x.jar" LibraryManagementSystem
    ```

      * Replace `x.x.x.x` with the actual version number of your downloaded SQLite JDBC driver.
      * The application will first display a login screen. Use **Username: `admin`** and **Password: `password`** to log in.
      * The SQLite database file (`library.db`) will be automatically created in the project directory if it doesn't exist, and initial data (an admin user and some books) will be populated.

-----