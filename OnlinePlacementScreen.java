import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class OnlinePlacementScreen extends JFrame {
    private final OnlineMatchSession session;
    private final Player localPlayer;
    private final Player remotePlayer;
    private final boolean host;

    public OnlinePlacementScreen(OnlineMatchSession session, Player localPlayer, Player remotePlayer, boolean host) {
        this.session = session;
        this.localPlayer = localPlayer;
        this.remotePlayer = remotePlayer;
        this.host = host;

        this.setTitle(host ? "Online Placement - Host" : "Online Placement - Client");
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(null);

        JFrame f = this;
        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
                int ch = JOptionPane.showConfirmDialog(f,
                        "Disconnect from the online match?",
                        "Confirm exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (ch) {
                    case 0:
                        session.close();
                        new LoadingScreen();
                        f.dispose();
                        break;
                    case 1:
                    case 2:
                        break;
                }
            }

            public void windowOpened(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
        });

        JLabel title = new JLabel(localPlayer.name+" placing ships", SwingConstants.CENTER);
        title.setBounds(200, 100, 400, 50);
        this.add(title);

        localPlayer.bgrid = new ButtonGrid(this, 200, 200, 600, 600, 40, 40, true);

        JButton place = new JButton("Place on grid");
        place.setBounds(720, 200, 120, 20);
        localPlayer.bgrid.no_of_ships_placed = 0;
        place.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                place.setBackground(Color.RED);
                ArrayList<Integer[]> select_coord = new ArrayList<Integer[]>();
                int noClicked = 0;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (localPlayer.bgrid.grid[i][j].getBackground() == Color.GREEN) {
                            Integer[] a = new Integer[2];
                            a[0] = i;
                            a[1] = j;
                            select_coord.add(a);
                            noClicked++;
                        }
                    }
                }

                Battleship btship = new Battleship(Ship.PATROL_BOAT);
                boolean switchCheck = false;
                switch (noClicked) {
                    case 0:
                    case 1:
                        JOptionPane.showConfirmDialog(f, "Please check your selection", "Error", JOptionPane.PLAIN_MESSAGE);
                        switchCheck = false;
                        Iterator<Integer[]> it = select_coord.iterator();
                        while (it.hasNext()) {
                            Integer coord[] = it.next();
                            localPlayer.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                        }
                        select_coord.clear();
                        break;
                    case 2:
                        btship.type = Ship.PATROL_BOAT;
                        switchCheck = true;
                        break;
                    case 3:
                        if (localPlayer.bt[1].gridCoord.isEmpty()) {
                            btship.type = Ship.SUBMARINE;
                        } else {
                            btship.type = Ship.DESTROYER;
                        }
                        switchCheck = true;
                        break;
                    case 4:
                        btship.type = Ship.BATTLESHIP;
                        switchCheck = true;
                        break;
                    case 5:
                        btship.type = Ship.CARRIER;
                        switchCheck = true;
                        break;
                }

                boolean diagonalCheck = true;
                if (switchCheck) {
                    Iterator<Integer[]> it = select_coord.iterator();
                    Integer[] prev = it.next();
                    boolean horizontal = false;
                    int count = 0;
                    while (it.hasNext()) {
                        Integer[] coord = it.next();
                        if (count == 0) {
                            if (prev[0] == coord[0] && (prev[1] == coord[1] + 1 || prev[1] == coord[1] - 1)) {
                                horizontal = false;
                            } else if (prev[1] == coord[1] && (prev[0] == coord[0] + 1 || prev[0] == coord[0] - 1)) {
                                horizontal = true;
                            } else {
                                diagonalCheck = false;
                                break;
                            }
                        }
                        if (prev[0] == coord[0] && (prev[1] == coord[1] + 1 || prev[1] == coord[1] - 1) && horizontal == false) {
                            diagonalCheck = true;
                        } else if (prev[1] == coord[1] && (prev[0] == coord[0] + 1 || prev[0] == coord[0] - 1) && horizontal == true) {
                            diagonalCheck = true;
                        } else {
                            diagonalCheck = false;
                            break;
                        }
                        prev = coord;
                        count++;
                    }
                }

                Iterator<Integer[]> it1 = select_coord.iterator();
                if (diagonalCheck && switchCheck) {
                    if (localPlayer.bgrid.no_of_ships_placed + 1 > 5) {
                        int ch = JOptionPane.showConfirmDialog(f,
                                "You are trying to place more than 5 ships. Your latest entry will be cleared",
                                "Error",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (ch) {
                            case 0:
                                while (it1.hasNext()) {
                                    Integer[] coord = it1.next();
                                    localPlayer.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                                    btship.gridCoord.clear();
                                    btship.type = null;
                                }
                                select_coord.clear();
                                break;
                            case 1:
                            case 2:
                                break;
                        }
                    } else {
                        while (it1.hasNext()) {
                            Integer[] coord = it1.next();
                            localPlayer.bgrid.grid[coord[0]][coord[1]].setToolTipText(btship.type.getName());
                            localPlayer.bgrid.grid[coord[0]][coord[1]].setBackground(Color.RED);
                            btship.gridCoord.add(coord);
                        }
                        localPlayer.bgrid.no_of_ships_placed++;
                    }
                } else {
                    btship.type = null;
                    JOptionPane.showConfirmDialog(f, "Please check your selection", "Error", JOptionPane.PLAIN_MESSAGE);
                    while (it1.hasNext()) {
                        Integer coord[] = it1.next();
                        localPlayer.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                    }
                    select_coord.clear();
                }

                for (int i = 0; i < 5; i++) {
                    if (localPlayer.bt[i].type == btship.type) {
                        localPlayer.bt[i] = btship;
                    }
                }

                place.setBackground(null);
            }
        });
        this.add(place);

        JButton clear_all = new JButton("Clear all");
        clear_all.setBounds(720, 440, 100, 20);
        clear_all.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                localPlayer.bgrid.no_of_ships_placed = 0;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        localPlayer.bgrid.grid[i][j].setBackground(Color.BLACK);
                    }
                }
                for (int i = 0; i < 5; i++) {
                    localPlayer.bt[i].gridCoord.clear();
                }
            }
        });
        this.add(clear_all);

        JButton done = new JButton("Done");
        done.setBounds(720, 700, 100, 20);
        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(f, "Confirm your grid?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                if (res != 0) {
                    return;
                }

                for (int i = 0; i < 5; i++) {
                    if (localPlayer.bt[i].gridCoord.isEmpty()) {
                        JOptionPane.showConfirmDialog(f, "All ships have not been placed. Please place all ships first", "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }

                done.setEnabled(false);
                place.setEnabled(false);
                clear_all.setEnabled(false);

                Thread syncThread = new Thread(() -> {
                    try {
                        session.sendLine(OnlineProtocol.serializeIdentity(localPlayer.name));
                        String remoteIdentity = session.readLine();
                        String parsedRemoteName = OnlineProtocol.parseIdentity(remoteIdentity);
                        if (parsedRemoteName != null) {
                            remotePlayer.name = parsedRemoteName;
                        }

                        session.sendLine(OnlineProtocol.serializePlacement(localPlayer));
                        String remotePlacement = session.readLine();
                        if (remotePlacement != null) {
                            OnlineProtocol.applyPlacement(remotePlayer, remotePlacement);
                        }

                        SwingUtilities.invokeLater(() -> {
                            f.dispose();
                            new OnlineGameScreen(session, localPlayer, remotePlayer, host);
                        });
                    } catch (Exception ex) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(f,
                                    "Unable to synchronize ship placement: " + ex.getMessage(),
                                    "Network Error",
                                    JOptionPane.ERROR_MESSAGE);
                            done.setEnabled(true);
                            place.setEnabled(true);
                            clear_all.setEnabled(true);
                        });
                    }
                }, "Battleship-OnlinePlacementSync");
                syncThread.setDaemon(true);
                syncThread.start();
            }
        });
        this.add(done);
    }
}