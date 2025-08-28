# 🚀 Advanced Level Project: Real-Time Chat Application using Socket Programming

This project implements a fully functional, real-time chat application using Java's socket programming. It demonstrates client-server communication, multithreading, and basic file transfer capabilities.

## Features 🌟

* **Client-Server Architecture:** A dedicated server handles communication between multiple clients.
* **Multi-user Chat:** Supports multiple clients connecting and chatting simultaneously.
* **Real-Time Messaging:** Messages are broadcast to all connected users instantly.
* **User Names:** Clients can choose a screen name upon connecting.
* **Timestamped Messages:** All messages are prefixed with a timestamp for better readability.
* **Private Messaging:** Users can send private messages to specific individuals using the `@username message` format.
* **File Sharing:** Clients can send files to the server, which then broadcasts the file to all other connected clients.
* **GUI Frontend:** The client application features a simple Swing-based graphical user interface.

## Technologies Used 🛠️

* **Java (JDK 17+)**: Core programming language.
* **Java Sockets**: For network communication (TCP/IP).
* **Multithreading**: To handle multiple client connections concurrently.
* **Java Swing**: For the client-side graphical user interface.
* **I/O Streams**: For handling message and file data transfer.

## How to Execute 🚀

This application consists of two main components: a **Chat Server** and a **Chat Client**. You need to run the server first, and then you can launch multiple client instances.

### Prerequisites

* **Java Development Kit (JDK) 17 or higher** installed.

### 1. Run the Chat Server

First, you need to start the server. This will listen for incoming client connections.

1.  **Navigate to the project directory:**
    ```bash
    cd Advanced Level
    ```
2.  **Compile the Java code:**
    ```bash
    javac ChatApp.java
    ```
3.  **Run the server application:**
    ```bash
    java ChatApp Server
    ```
    You will see a message in the console indicating that the server is running on port `9001`. Keep this console window open.

### 2. Run the Chat Client(s)

Once the server is running, you can launch one or more client applications. Each client will open a separate GUI window.

1.  **Open a new terminal or command prompt window.**
2.  **Navigate to the project directory:**
    ```bash
    cd Advanced Level/RealTimeChatApp
    ```
3.  **Compile the Java code (if you haven't already done so for the server):**
    ```bash
    javac ChatApp.java
    ```
4.  **Run a client application:**
    ```bash
    java ChatApp Client
    ```
5.  A GUI window will appear, prompting you to **enter a screen name**. Enter a unique name for each client.
6.  You can repeat steps 1-4 to launch multiple clients and chat between them.

### Sending Private Messages

To send a private message to a specific user, type `@username your_message_here` in the text field and press Enter. Replace `username` with the actual screen name of the recipient.

### Sending Files

To send a file:
1.  Click the **"Send File"** button in the client GUI.
2.  A file chooser dialog will appear. Select the file you wish to send.
3.  The file will be sent to the server and then broadcast to all other connected clients, who will save it to their Desktop.

---
````