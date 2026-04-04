import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

public class LoadingScreen extends JFrame{
    private static final int DEFAULT_ONLINE_PORT = 5050;
    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static volatile Socket activeOnlineSocket;
    private static volatile ServerSocket activeOnlineServerSocket;
    private static volatile String activeRoomCode;

    public LoadingScreen(){
        this.setTitle("Battleship");
        this.setSize(1000, 760);
        this.setMinimumSize(new java.awt.Dimension(900, 680));
        this.setLocationRelativeTo(null);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 24));
        this.getContentPane().setBackground(Color.BLACK);

        JLabel l = new JLabel("Battleship", SwingConstants.CENTER);
        l.setFont(new Font("Blackadder ITC", Font.PLAIN, 50));//for font
        l.setForeground(Color.WHITE);
        l.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        this.add(l, BorderLayout.NORTH);

        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 40, 0));

        JButton play = new JButton("Play");
        play.setFont(new Font("Dialog", Font.BOLD, 24));
        play.setAlignmentX(Component.CENTER_ALIGNMENT);
        play.setPreferredSize(new java.awt.Dimension(260, 58));
        play.setMaximumSize(new java.awt.Dimension(260, 58));
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
        menuPanel.add(play);
        menuPanel.add(Box.createVerticalStrut(14));

        JButton online = new JButton("Play Online");
        online.setFont(new Font("Dialog", Font.BOLD, 24));
        online.setAlignmentX(Component.CENTER_ALIGNMENT);
        online.setPreferredSize(new java.awt.Dimension(260, 58));
        online.setMaximumSize(new java.awt.Dimension(260, 58));
        online.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                showOnlineSetup(f);
            }
        });
        menuPanel.add(online);
        menuPanel.add(Box.createVerticalStrut(14));

        JButton rules = new JButton("Rules");
        rules.setFont(new Font("Dialog", Font.BOLD, 24));
        rules.setAlignmentX(Component.CENTER_ALIGNMENT);
        rules.setPreferredSize(new java.awt.Dimension(260, 58));
        rules.setMaximumSize(new java.awt.Dimension(260, 58));
        rules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                showRulesDialog(f);
            }
        });
        menuPanel.add(rules);
        menuPanel.add(Box.createVerticalStrut(14));

        JButton tutorial = new JButton("Tutorial");
        tutorial.setFont(new Font("Dialog", Font.BOLD, 24));
        tutorial.setAlignmentX(Component.CENTER_ALIGNMENT);
        tutorial.setPreferredSize(new java.awt.Dimension(260, 58));
        tutorial.setMaximumSize(new java.awt.Dimension(260, 58));
        tutorial.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new TutorialScreen();
            }
        });
        menuPanel.add(tutorial);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        center.setOpaque(false);
        center.add(menuPanel);
        this.add(center, BorderLayout.CENTER);
        
        this.setVisible(true);
    }

    private static void showRulesDialog(JFrame parent) {
        JDialog rulesDialog = new JDialog(parent, "Rules of the game", false);
        rulesDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        rulesDialog.setLayout(new BorderLayout(0, 12));
        rulesDialog.getContentPane().setBackground(Color.BLACK);

        JLabel header = new JLabel("Rules", SwingConstants.CENTER);
        header.setFont(new Font("Blackadder ITC", Font.BOLD, 50));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        rulesDialog.add(header, BorderLayout.NORTH);

        JTextArea rulesText = new JTextArea();
        rulesText.setBackground(Color.BLACK);
        rulesText.setForeground(Color.WHITE);
        rulesText.setText(
                "1. Battleship is a 2-player game\n" +
                "2. Each player gets 5 ships\n" +
                "3. the ships are of the following types:\n" +
                "    (a) Patrol Boat (occupies 2 grid spaces)\n" +
                "    (b) Submarine (occupies 3 grid spaces)\n" +
                "    (c) Destroyer (occupies 3 grid spaces)\n" +
                "    (d) Battleship (occupies 4 grid spaces)\n" +
                "    (e) Carrier (occupies 5 grid spaces)\n" +
                "4. Each player gets one ship of each kind to place on the grid.\n" +
                "5. The ships are arranged on a 10x10 grid with columns labelled 1-10 and rows labelled A-J\n" +
                "6. Each player has to keep his/her arrangement of ships secret from the other player\n" +
                "7. After the grids are arranged, players take turns to shoot at the other player's grid\n" +
                "8. Each player chooses one grid square to shoot at.\n" +
                "9. If a ship is hit (ie. one of the grid squares which it occupies has been shot), then it is announced by the computer.\n" +
                "10. If all the grid squares which a ship occupies is hit, then the ship sinks.\n" +
                "11. The first player to have all their ships sunk loses the game.\n");
        rulesText.setFont(new Font("Times New Roman", Font.PLAIN, 20));
        rulesText.setEditable(false);
        rulesText.setLineWrap(true);
        rulesText.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(rulesText,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        scrollPane.setBackground(Color.BLACK);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        rulesText.setMargin(new Insets(6, 6, 6, 6));
        rulesDialog.add(scrollPane, BorderLayout.CENTER);

        rulesDialog.setSize(620, 720);
        rulesDialog.setMinimumSize(new java.awt.Dimension(480, 520));
        rulesDialog.setLocationRelativeTo(parent);
        rulesDialog.setVisible(true);
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
        waitingDialog.setSize(560, 280);
        waitingDialog.setResizable(false);
        waitingDialog.setLocationRelativeTo(parent);
        waitingDialog.setLayout(new BorderLayout(0, 10));
        waitingDialog.getContentPane().setBackground(Color.BLACK);
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 6, 20));

        JLabel title = new JLabel("Waiting for player", SwingConstants.CENTER);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        infoPanel.add(title);
        infoPanel.add(Box.createVerticalStrut(10));

        JLabel codeLabel = new JLabel("Room code: " + roomCode, SwingConstants.CENTER);
        codeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        codeLabel.setForeground(Color.WHITE);
        codeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(codeLabel);
        infoPanel.add(Box.createVerticalStrut(8));

        JLabel endpointLabel = new JLabel("Connect using IP: " + hostIp + "   Port: " + port, SwingConstants.CENTER);
        endpointLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        endpointLabel.setForeground(Color.WHITE);
        endpointLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(endpointLabel);
        infoPanel.add(Box.createVerticalStrut(10));

        JLabel status = new JLabel("Starting server on port " + port + "...", SwingConstants.CENTER);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(status);

        JButton cancel = new JButton("Cancel");
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
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        actionsPanel.add(cancel);

        waitingDialog.add(infoPanel, BorderLayout.CENTER);
        waitingDialog.add(actionsPanel, BorderLayout.SOUTH);

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
        waitingDialog.setSize(460, 220);
        waitingDialog.setResizable(false);
        waitingDialog.setLocationRelativeTo(parent);
        waitingDialog.setLayout(new BorderLayout(0, 8));
        waitingDialog.getContentPane().setBackground(Color.BLACK);
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 20, 0, 20));

        JLabel status = new JLabel("Connecting to " + hostAddress.trim() + ":" + port + "...", SwingConstants.CENTER);
        status.setAlignmentX(Component.CENTER_ALIGNMENT);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Arial", Font.PLAIN, 14));
        content.add(status);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeQuietly(activeOnlineSocket);
                activeOnlineSocket = null;
                waitingDialog.dispose();
            }
        });
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        actionsPanel.add(cancel);

        waitingDialog.add(content, BorderLayout.CENTER);
        waitingDialog.add(actionsPanel, BorderLayout.SOUTH);

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
        final String preferredIp = getPreferredOutboundIpv4();
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
                                int score = getInterfaceScore(networkInterface, ip, preferredIp);
                                ipList.add(new HostIpChoice(interfaceName, ip, score));
                            }
                        }
                    }
                }
            }
        } catch (SocketException ignored) {
        }

        if (!ipList.isEmpty()) {
            Collections.sort(ipList, new Comparator<HostIpChoice>() {
                @Override
                public int compare(HostIpChoice left, HostIpChoice right) {
                    if (left.score != right.score) {
                        return right.score - left.score;
                    }
                    return left.interfaceName.compareToIgnoreCase(right.interfaceName);
                }
            });
        }

        if (ipList.isEmpty()) {
            try {
                String fallback = InetAddress.getLocalHost().getHostAddress();
                if (fallback != null && !fallback.trim().isEmpty()) {
                    ipList.add(new HostIpChoice("Default", fallback, 0));
                }
            } catch (Exception ignored) {
            }
        }

        return ipList;
    }

    private static String getPreferredOutboundIpv4() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 53);
            InetAddress localAddress = socket.getLocalAddress();
            if (localAddress instanceof Inet4Address && !localAddress.isAnyLocalAddress() && !localAddress.isLoopbackAddress()) {
                return localAddress.getHostAddress();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static int getInterfaceScore(NetworkInterface networkInterface, String ip, String preferredIp) {
        int score = 0;

        String name = networkInterface.getName();
        String displayName = networkInterface.getDisplayName();
        String combinedName = ((name == null ? "" : name) + " " + (displayName == null ? "" : displayName)).toLowerCase();

        if (preferredIp != null && preferredIp.equals(ip)) {
            score += 1000;
        }

        if (combinedName.contains("wlan") || combinedName.contains("wifi") || combinedName.contains("wi-fi")
                || combinedName.contains("eth") || combinedName.contains("enp") || combinedName.contains("eno")) {
            score += 200;
        }

        if (combinedName.contains("docker") || combinedName.contains("veth") || combinedName.contains("virbr")
                || combinedName.contains("vmnet") || combinedName.contains("br-") || combinedName.contains("cni")
                || combinedName.contains("zt") || combinedName.contains("tun") || combinedName.contains("tap")) {
            score -= 300;
        }

        try {
            if (networkInterface.isPointToPoint()) {
                score -= 150;
            }
            if (networkInterface.supportsMulticast()) {
                score += 20;
            }
        } catch (SocketException ignored) {
        }

        return score;
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
        private final int score;

        private HostIpChoice(String interfaceName, String ip, int score) {
            this.interfaceName = interfaceName;
            this.ip = ip;
            this.score = score;
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
