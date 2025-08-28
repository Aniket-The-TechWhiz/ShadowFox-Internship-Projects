import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatApp {

    public static void main(String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("Server")) {
            new ChatServer().start();
        } else if (args[0].equalsIgnoreCase("Client")) {
            SwingUtilities.invokeLater(() -> new ChatClient("localhost").setVisible(true));
        } else {
            System.out.println("Invalid argument. Use 'Server' or 'Client'.");
        }
    }

    private static class ChatServer {
        private static final int PORT = 9001;
        private Set<String> userNames = new HashSet<>();
        private ConcurrentHashMap<String, PrintWriter> writers = new ConcurrentHashMap<>();

        public void start() {
            System.out.println("The chat server is running on port " + PORT + ".");
            try (ServerSocket listener = new ServerSocket(PORT)) {
                while (true) {
                    new Handler(listener.accept()).start();
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }

        private class Handler extends Thread {
            private String userName;
            private Socket socket;
            private BufferedReader in;
            private PrintWriter out;
            private InputStream fileIn;

            public Handler(Socket socket) {
                this.socket = socket;
            }

            public void run() {
                System.out.println("New client connected.");
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    fileIn = socket.getInputStream();

                    while (true) {
                        out.println("SUBMITNAME");
                        userName = in.readLine();
                        if (userName == null || userName.trim().isEmpty()) {
                            continue;
                        }
                        synchronized (userNames) {
                            if (!userNames.contains(userName)) {
                                userNames.add(userName);
                                break;
                            }
                        }
                    }

                    out.println("NAMEACCEPTED " + userName);
                    writers.put(userName, out);
                    broadcastMessage(userName + " has joined the chat.", true);
                    listAllUsers(userName);

                    String message;
                    while ((message = in.readLine()) != null) {
                        if (message.startsWith("FILETRANSFER:")) {
                            handleFileTransfer(message);
                        } else if (message.startsWith("@")) {
                             handlePrivateMessage(message);
                        } else {
                            broadcastMessage(userName + ": " + message, false);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Handler error for " + userName + ": " + e.getMessage());
                } finally {
                    if (userName != null) {
                        System.out.println(userName + " is leaving.");
                        userNames.remove(userName);
                        writers.remove(userName);
                    }
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Could not close socket: " + e.getMessage());
                    }
                    if (userName != null) {
                        broadcastMessage(userName + " has left the chat.", true);
                    }
                }
            }

            // private void handlePrivateMessage(String message)
             private void handlePrivateMessage(String message) {
                int spaceIndex = message.indexOf(' ');
                if (spaceIndex > 0) {
                    String recipientName = message.substring(1, spaceIndex);
                    String privateMessage = message.substring(spaceIndex + 1);

                    PrintWriter recipientWriter = writers.get(recipientName);
                    if (recipientWriter != null) {
                        recipientWriter.println(formatMessage("[Private from " + userName + "]: " + privateMessage, false));
                        out.println(formatMessage("[Private to " + recipientName + "]: " + privateMessage, false));
                    } else {
                       out.println(formatMessage("ERROR The user '" + recipientName + "' is not available or does not exist.", true));
                    }
                }
            }

            // private void handleFileTransfer(String message)
            private void handleFileTransfer(String message) {
                try {
                    String[] parts = message.split(":");
                    if (parts.length != 3) {
                        System.err.println("Invalid file transfer protocol message: " + message);
                        return;
                    }

                    String fileName = parts[1];
                    long fileSize = Long.parseLong(parts[2]);
                    
                    System.out.println("Receiving file from " + userName + ": " + fileName + " (" + fileSize + " bytes)");

                    broadcastMessage(userName + " is sending file: " + fileName, true);

                    byte[] buffer = new byte[8192];
                    long bytesRead = 0;
                    int count;

                    for (PrintWriter writer : writers.values()) {
                        writer.println("INCOMINGFILE:" + fileName);
                    }

                    try (OutputStream serverOut = new SocketOutputStream(writers.values().toArray(new PrintWriter[0]))) {
                        while (bytesRead < fileSize && (count = fileIn.read(buffer, 0, (int) Math.min(buffer.length, fileSize - bytesRead))) > 0) {
                            serverOut.write(buffer, 0, count);
                            bytesRead += count;
                        }
                        serverOut.flush();
                    }
                    
                    broadcastMessage(userName + "'s file transfer completed for " + fileName + ".", true);
                    System.out.println("File transfer completed for " + fileName + " from " + userName);

                } catch (IOException e) {
                    System.err.println("Error handling file transfer for " + userName + ": " + e.getMessage());
                }
            }
            
            // private class SocketOutputStream extends OutputStream
            private class SocketOutputStream extends OutputStream {
                private final PrintWriter[] writers;
                public SocketOutputStream(PrintWriter[] writers) {
                    this.writers = writers;
                }
                
                @Override
                public void write(int b) throws IOException {
                    for (PrintWriter writer : writers) {
                        writer.write(b);
                    }
                }
                
                @Override
                public void write(byte[] b, int off, int len) throws IOException {
                    for (PrintWriter writer : writers) {
                        writer.write(new String(b, off, len));
                    }
                }
                
                @Override
                public void flush() throws IOException {
                    for (PrintWriter writer : writers) {
                        writer.flush();
                    }
                }
            }

            // private void broadcastMessage(String message, boolean isSystemMessage)
            private void broadcastMessage(String message, boolean isSystemMessage) {
                String formattedMessage = formatMessage(message, isSystemMessage);
                System.out.println("Broadcasting: " + formattedMessage);
                for (PrintWriter writer : writers.values()) {
                    writer.println(formattedMessage);
                }
            }
            
            // private void listAllUsers(String requestingUser)
            private void listAllUsers(String requestingUser) {
                StringBuilder userList = new StringBuilder("Current users: ");
                for (String user : userNames) {
                    userList.append(user).append(", ");
                }
                String finalUserList = userList.substring(0, userList.length() - 2);
                writers.get(requestingUser).println(formatMessage(finalUserList, true));
            }
            
            // private String formatMessage(String message, boolean isSystemMessage)
            private String formatMessage(String message, boolean isSystemMessage) {
                String timestamp = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]").format(new Date());
                return timestamp + " " + (isSystemMessage ? "[SYSTEM] " : "") + message;
            }
        }
    }

    private static class ChatClient extends JFrame {
        private BufferedReader in;
        private PrintWriter out;
        private JTextField textField;
        private JTextArea messageArea;
        private Socket socket;
        private String serverAddress;
        private static final int PORT = 9001;

        public ChatClient(String serverAddress) {
            this.serverAddress = serverAddress;
            initializeGUI();
            try {
                connectToServer();
            } catch (IOException e) {
                showErrorDialog("Connection Error", "Could not connect to the server.");
            }
        }

        // private void initializeGUI()
        private void initializeGUI() {
            setTitle("Chat App - Client");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 400);
            setLocationRelativeTo(null);

            messageArea = new JTextArea();
            messageArea.setEditable(false);
            messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(messageArea);
            add(scrollPane, BorderLayout.CENTER);

            JPanel southPanel = new JPanel(new BorderLayout());
            
            textField = new JTextField();
            textField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (out != null) {
                        out.println(textField.getText());
                        textField.setText("");
                    }
                }
            });
            southPanel.add(textField, BorderLayout.CENTER);

            JButton fileButton = new JButton("Send File");
            fileButton.addActionListener(e -> sendFile());
            southPanel.add(fileButton, BorderLayout.EAST);
            
            add(southPanel, BorderLayout.SOUTH);
        }

        // private void sendFile()
        private void sendFile() {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select a file to send");
            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSend = fileChooser.getSelectedFile();
                if (fileToSend.exists()) {
                    try {
                        out.println("FILETRANSFER:" + fileToSend.getName() + ":" + fileToSend.length());
                        out.flush();
                        
                        try (FileInputStream fis = new FileInputStream(fileToSend)) {
                            byte[] buffer = new byte[8192];
                            int count;
                            while ((count = fis.read(buffer)) > 0) {
                                socket.getOutputStream().write(buffer, 0, count);
                                socket.getOutputStream().flush();
                            }
                        }
                        messageArea.append("[SYSTEM] File '" + fileToSend.getName() + "' sent successfully.\n");
                    } catch (IOException ex) {
                        showErrorDialog("File Send Error", "Could not send the file: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "File does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        // private void connectToServer()
        private void connectToServer() throws IOException {
            socket = new Socket(serverAddress, PORT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                try {
                    while (true) {
                        String line = in.readLine();
                        if (line == null) {
                            continue;
                        }
                        
                        if (line.startsWith("SUBMITNAME")) {
                            String userName = getUserName();
                            out.println(userName);
                        } else if (line.startsWith("NAMEACCEPTED")) {
                            textField.setEditable(true);
                            this.setTitle("Chat App - " + line.substring(13));
                        } else if (line.startsWith("INCOMINGFILE:")) {
                            String fileName = line.substring(13);
                            messageArea.append("[SYSTEM] Receiving file: " + fileName + "\n");
                            receiveFile(fileName);
                        } else {
                            messageArea.append(line + "\n");
                            messageArea.setCaretPosition(messageArea.getDocument().getLength());
                        }
                    }
                } catch (IOException e) {
                    showErrorDialog("Connection Lost", "Disconnected from the server.");
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Could not close socket: " + e.getMessage());
                    }
                }
            }).start();
        }

        // private void receiveFile(String fileName)
        private void receiveFile(String fileName) {
            try {
                String userHome = System.getProperty("user.home");
                File saveDir = new File(userHome, "Desktop");
                if (!saveDir.exists()) {
                    saveDir = new File(userHome);
                }
                File fileToSave = new File(saveDir, fileName);

                try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = socket.getInputStream().read(buffer)) > 0) {
                        fos.write(buffer, 0, count);
                        if (count < buffer.length) break;
                    }
                }
                messageArea.append("[SYSTEM] File '" + fileName + "' received and saved to your Desktop.\n");
            } catch (IOException e) {
                messageArea.append("[SYSTEM] Error receiving file: " + e.getMessage() + "\n");
            }
        }
        
        // private String getUserName()
        private String getUserName() {
            String name = null;
            while (name == null || name.trim().isEmpty()) {
                name = (String) JOptionPane.showInputDialog(
                        this,
                        "Enter your screen name:",
                        "Screen name selection",
                        JOptionPane.PLAIN_MESSAGE
                );
            }
            return name;
        }
        
        // private void showErrorDialog(String title, String message)
        private void showErrorDialog(String title, String message) {
            JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
            if (title.equals("Connection Lost")) {
                System.exit(0);
            }
        }
    }
}
