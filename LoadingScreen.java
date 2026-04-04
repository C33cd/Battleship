import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class LoadingScreen extends JFrame{
    private static final int DEFAULT_ONLINE_PORT = 5050;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static volatile Socket activeOnlineSocket;
    private static volatile ServerSocket activeOnlineServerSocket;
    private static volatile String activeRoomCode;

    public LoadingScreen(){
        ScreenScaler.initialize();
        this.setTitle("Battleship");
        this.setBounds(ScreenScaler.scaleX(100),ScreenScaler.scaleY(100),ScreenScaler.scaleX(500),ScreenScaler.scaleY(500));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(Color.BLACK);

        JLabel l = new JLabel("Battleship", SwingConstants.CENTER);
        l.setFont(new Font("Blackadder ITC", Font.PLAIN, 50));//for font
        l.setForeground(Color.WHITE);
        l.setBounds(0,50,this.getWidth(),50);
        this.add(l);

        JButton play = new JButton("Play");
        play.setBounds(ScreenScaler.scaleX(200),ScreenScaler.scaleY(120),ScreenScaler.scaleX(100),ScreenScaler.scaleY(50));
        this.add(play);
        LoadingScreen f = this;
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                if (!GameMain.ensureLocalSecondPlayerName()) {
                    return;
                }
                new Player_ChoiceScreen(GameMain.p1);
                f.dispose();
            }
        });

        JButton online = new JButton("Play Online");
        online.setBounds(ScreenScaler.scaleX(200),ScreenScaler.scaleY(190),ScreenScaler.scaleX(100),ScreenScaler.scaleY(50));
        this.add(online);
        online.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                showOnlineSetup(f);
            }
        });

        JButton rules = new JButton("Rules");
        rules.setBounds(ScreenScaler.scaleX(200),ScreenScaler.scaleY(260),ScreenScaler.scaleX(100),ScreenScaler.scaleY(50));
        this.add(rules);
        rules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                JFrame rules_popup = new JFrame("Rules of the game");
                rules_popup.setBounds(ScreenScaler.scaleX(800),ScreenScaler.scaleY(100),ScreenScaler.scaleX(515),ScreenScaler.scaleY(600));
                rules_popup.setVisible(true);
                rules_popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                rules_popup.setLayout(new BorderLayout());
                rules_popup.getContentPane().setBackground(Color.BLACK);

                JLabel header  = new JLabel("Rules", SwingConstants.CENTER);
                header.setFont(new Font("Blackadder ITC", Font.BOLD, 50));
                header.setForeground(Color.WHITE);
                header.setPreferredSize(new Dimension(500, 60));
                rules_popup.add(header, BorderLayout.NORTH);
                
                JTextArea t1 = new JTextArea();
                t1.setBackground(Color.BLACK);
                t1.setForeground(Color.WHITE);
                t1.setText("1. Battleship is a 2-player game\r\n" + //
                                        "2. Each player gets 5 ships\r\n" + //
                                        "3. the ships are of the following types:\r\n" + //
                                        "    (a) Patrol Boat (occupies 2 grid spaces)\r\n" + //
                                        "    (b) Submarine (occupies 3 grid spaces)\r\n" + //
                                        "    (c) Destroyer (occupies 3 grid spaces)\r\n" + //
                                        "    (d) Battleship (occupies 4 grid spaces)\r\n" + //
                                        "    (e) Carrier (occupies 5 grid spaces)\r\n" + //
                                        "4. Each player gets one ship of each kind to place on the grid.\r\n" + //
                                        "5. The ships are arranged on a 10x10 grid with columns labelled 1-10 and rows labelled A-J\r\n" + //
                                        "6. Each player has to keep his/her arrangement of ships secret from the other player\r\n" + //
                                        "7. After the grids are arranged, players take turns to shoot at the other player's grid\r\n" + //
                                        "8. Each player chooses one grid square to shoot at.\r\n" + //
                                        "9. If a ship is hit (ie. one of the grid squares which it occupies has been shot), then it is announced by the computer.\r\n" + //
                                        "10. If all the grid squares which a ship occupies is hit, then the ship sinks.\r\n" + //
                                        "11. The first player to have all their ships sunk loses the game. \r\n" + //
                                        "\r\n" + //
                                        "");
                t1.setFont(new Font("Times New Roman", Font.PLAIN, 20));
                t1.setEditable(false);
                t1.setLineWrap(true);
                t1.setWrapStyleWord(true);
                JScrollPane p1 = new JScrollPane(t1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                p1.setBackground(Color.BLACK);
                p1.setForeground(Color.WHITE);
                rules_popup.add(p1, BorderLayout.CENTER);



        
            }
        });

        JButton tutorial = new JButton("Tutorial");
        tutorial.setBounds(ScreenScaler.scaleX(200),ScreenScaler.scaleY(330), ScreenScaler.scaleX(100), ScreenScaler.scaleY(50));
        this.add(tutorial);
        tutorial.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new TutorialScreen();
            }
        });
        
        this.setVisible(true);
    }

    private static void showOnlineSetup(JFrame parent) {
        String[] options = {"Host a match", "Join a match", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                parent,
                "Choose how you want to start the online session.",
                "Online Battleship",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0) {
            startHosting(parent);
        } else if (choice == 1) {
            joinMatch(parent);
        }
    }

    private static void startHosting(JFrame parent) {
        Integer port = promptForPort(parent, "Host Port", DEFAULT_ONLINE_PORT);
        if (port == null) {
            return;
        }

        String roomCode = generateRoomCode();
        activeRoomCode = roomCode;
        String hostIp = promptForHostIp(parent);
        if (hostIp == null) {
            activeRoomCode = null;
            return;
        }

        JDialog waitingDialog = new JDialog(parent, "Hosting Online Match", false);
        waitingDialog.setSize(520, 250);
        waitingDialog.setResizable(false);
        waitingDialog.setLocationRelativeTo(parent);
        waitingDialog.setLayout(null);
        waitingDialog.getContentPane().setBackground(Color.BLACK);

        JLabel title = new JLabel("Waiting for player", SwingConstants.CENTER);
        title.setBounds(0, ScreenScaler.scaleY(20), ScreenScaler.scaleX(400), ScreenScaler.scaleY(30));
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        waitingDialog.add(title);

        JLabel codeLabel = new JLabel("Room code: " + roomCode, SwingConstants.CENTER);
        codeLabel.setBounds(0, ScreenScaler.scaleY(60), ScreenScaler.scaleX(500), ScreenScaler.scaleY(30));
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        waitingDialog.add(codeLabel);

        JLabel endpointLabel = new JLabel("Connect using IP: " + hostIp + "   Port: " + port, SwingConstants.CENTER);
        endpointLabel.setBounds(0, ScreenScaler.scaleY(90), ScreenScaler.scaleX(500), ScreenScaler.scaleY(30));
        endpointLabel.setForeground(Color.WHITE);
        endpointLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingDialog.add(endpointLabel);

        JLabel status = new JLabel("Starting server on port " + port + "...", SwingConstants.CENTER);
        status.setBounds(0, ScreenScaler.scaleY(125), ScreenScaler.scaleX(500), ScreenScaler.scaleY(30));
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingDialog.add(status);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(ScreenScaler.scaleX(205), ScreenScaler.scaleY(170), ScreenScaler.scaleX(100), ScreenScaler.scaleY(25));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeQuietly(activeOnlineSocket);
                closeQuietly(activeOnlineServerSocket);
                activeOnlineSocket = null;
                activeOnlineServerSocket = null;
                activeRoomCode = null;
                waitingDialog.dispose();
            }
        });
        waitingDialog.add(cancel);

        waitingDialog.setVisible(true);

        Thread hostThread = new Thread(() -> {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);
                activeOnlineServerSocket = serverSocket;
                updateStatus(status, "Server ready. Share the room code and wait for a client.");

                Socket socket = serverSocket.accept();
                activeOnlineSocket = socket;

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                socket.setSoTimeout(CONNECTION_TIMEOUT_MS);
                String clientCode = reader.readLine();
                if (clientCode != null && clientCode.trim().equalsIgnoreCase(roomCode)) {
                    writer.println("OK");
                    socket.setSoTimeout(0);
                    updateStatus(status, "Client connected. Online session is ready.");
                    launchOnlinePlacement(parent, waitingDialog, true);
                } else {
                    writer.println("BAD_CODE");
                    updateStatus(status, "Connection rejected. Room code did not match.");
                    closeQuietly(socket);
                    activeOnlineSocket = null;
                }
            } catch (SocketTimeoutException ex) {
                updateStatus(status, "Connection timed out.");
                closeQuietly(activeOnlineSocket);
                activeOnlineSocket = null;
            } catch (IOException ex) {
                updateStatus(status, "Unable to host: " + ex.getMessage());
                closeQuietly(activeOnlineSocket);
                activeOnlineSocket = null;
            } finally {
                closeQuietly(serverSocket);
                activeOnlineServerSocket = null;
            }
        }, "Battleship-Host");
        hostThread.setDaemon(true);
        hostThread.start();
    }

    private static void joinMatch(JFrame parent) {
        String hostAddress = JOptionPane.showInputDialog(parent, "Enter the host IP address:", "Join Online Match", JOptionPane.QUESTION_MESSAGE);
        if (hostAddress == null || hostAddress.trim().isEmpty()) {
            return;
        }

        Integer port = promptForPort(parent, "Host Port", DEFAULT_ONLINE_PORT);
        if (port == null) {
            return;
        }

        String roomCode = JOptionPane.showInputDialog(parent, "Enter the room code shared by the host:", "Join Online Match", JOptionPane.QUESTION_MESSAGE);
        if (roomCode == null || roomCode.trim().isEmpty()) {
            return;
        }

        JDialog waitingDialog = new JDialog(parent, "Joining Online Match", false);
        waitingDialog.setSize(420, 180);
        waitingDialog.setResizable(false);
        waitingDialog.setLocationRelativeTo(parent);
        waitingDialog.setLayout(null);
        waitingDialog.getContentPane().setBackground(Color.BLACK);

        JLabel status = new JLabel("Connecting to " + hostAddress.trim() + ":" + port + "...", SwingConstants.CENTER);
        status.setBounds(0, ScreenScaler.scaleY(40), ScreenScaler.scaleX(400), ScreenScaler.scaleY(30));
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        waitingDialog.add(status);

        JButton cancel = new JButton("Cancel");
        cancel.setBounds(ScreenScaler.scaleX(150), ScreenScaler.scaleY(90), ScreenScaler.scaleX(100), ScreenScaler.scaleY(25));
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeQuietly(activeOnlineSocket);
                activeOnlineSocket = null;
                waitingDialog.dispose();
            }
        });
        waitingDialog.add(cancel);

        waitingDialog.setVisible(true);

        Thread clientThread = new Thread(() -> {
            Socket socket = null;
            try {
                socket = new Socket(hostAddress.trim(), port);
                socket.setSoTimeout(CONNECTION_TIMEOUT_MS);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

                writer.println(roomCode.trim());
                String response = reader.readLine();

                if ("OK".equalsIgnoreCase(response)) {
                    socket.setSoTimeout(0);
                    activeOnlineSocket = socket;
                    updateStatus(status, "Connected successfully. Online session is ready.");
                    launchOnlinePlacement(parent, waitingDialog, false);
                } else {
                    updateStatus(status, "Connection rejected. Check the room code.");
                    closeQuietly(socket);
                    activeOnlineSocket = null;
                }
            } catch (IOException ex) {
                updateStatus(status, "Unable to connect: " + ex.getMessage());
                closeQuietly(socket);
                activeOnlineSocket = null;
            }
        }, "Battleship-Client");
        clientThread.setDaemon(true);
        clientThread.start();
    }

    private static Integer promptForPort(JFrame parent, String title, int defaultPort) {
        String portText = JOptionPane.showInputDialog(parent, "Enter the network port:", String.valueOf(defaultPort));
        if (portText == null) {
            return null;
        }

        try {
            int port = Integer.parseInt(portText.trim());
            if (port < 1 || port > 65535) {
                JOptionPane.showMessageDialog(parent, "Please enter a port between 1 and 65535.", title, JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return port;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent, "Please enter a valid number.", title, JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private static String generateRoomCode() {
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder builder = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            builder.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    private static String promptForHostIp(JFrame parent) {
        List<HostIpChoice> addresses = getCandidateLocalIpv4s();
        if (addresses.isEmpty()) {
            JOptionPane.showMessageDialog(parent,
                    "No suitable local IPv4 address was found.",
                    "Network Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        }

        if (addresses.size() == 1) {
            return addresses.get(0).ip;
        }

        Object selected = JOptionPane.showInputDialog(
                parent,
                "Select the IP address of the network shared with the other player:",
                "Select Host IP",
                JOptionPane.QUESTION_MESSAGE,
                null,
                addresses.toArray(new HostIpChoice[0]),
                addresses.get(0));

        if (selected == null) {
            return null;
        }

        return ((HostIpChoice) selected).ip;
    }

    private static List<HostIpChoice> getCandidateLocalIpv4s() {
        List<HostIpChoice> ipList = new ArrayList<HostIpChoice>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback() || networkInterface.isVirtual()) {
                    continue;
                }

                Enumeration<InetAddress> interfaceAddresses = networkInterface.getInetAddresses();
                while (interfaceAddresses.hasMoreElements()) {
                    InetAddress address = interfaceAddresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        if (!address.isLinkLocalAddress()) {
                            String ip = address.getHostAddress();
                            if (!containsIp(ipList, ip)) {
                                String interfaceName = networkInterface.getDisplayName();
                                if (interfaceName == null || interfaceName.trim().isEmpty()) {
                                    interfaceName = networkInterface.getName();
                                }
                                ipList.add(new HostIpChoice(interfaceName, ip));
                            }
                        }
                    }
                }
            }
        } catch (SocketException ignored) {
        }

        if (ipList.isEmpty()) {
            try {
                String fallback = InetAddress.getLocalHost().getHostAddress();
                if (fallback != null && !fallback.trim().isEmpty()) {
                    ipList.add(new HostIpChoice("Default", fallback));
                }
            } catch (Exception ignored) {
            }
        }

        return ipList;
    }

    private static boolean containsIp(List<HostIpChoice> entries, String ip) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).ip.equals(ip)) {
                return true;
            }
        }
        return false;
    }

    private static final class HostIpChoice {
        private final String interfaceName;
        private final String ip;

        private HostIpChoice(String interfaceName, String ip) {
            this.interfaceName = interfaceName;
            this.ip = ip;
        }

        @Override
        public String toString() {
            return interfaceName + " - " + ip;
        }
    }

    private static void updateStatus(JLabel label, String message) {
        SwingUtilities.invokeLater(() -> label.setText(message));
    }

    private static void launchOnlinePlacement(JFrame parent, JDialog waitingDialog, boolean host) {
        SwingUtilities.invokeLater(() -> {
            if (waitingDialog != null) {
                waitingDialog.dispose();
            }

            if (parent != null) {
                parent.dispose();
            }

            Socket socket = getActiveOnlineSocket();
            if (socket == null) {
                JOptionPane.showMessageDialog(parent, "The online session is no longer available.", "Network Error", JOptionPane.ERROR_MESSAGE);
                new LoadingScreen();
                return;
            }

            try {
                OnlineMatchSession session = new OnlineMatchSession(socket, host ? OnlineMatchSession.Mode.HOST : OnlineMatchSession.Mode.CLIENT, getActiveRoomCode());
                new OnlinePlacementScreen(session, GameMain.p1, GameMain.p2, host);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Unable to start the online session: " + ex.getMessage(), "Network Error", JOptionPane.ERROR_MESSAGE);
                new LoadingScreen();
            }
        });
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    public static Socket getActiveOnlineSocket() {
        return activeOnlineSocket;
    }

    public static String getActiveRoomCode() {
        return activeRoomCode;
    }

    public static boolean hasActiveOnlineConnection() {
        Socket socket = activeOnlineSocket;
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
