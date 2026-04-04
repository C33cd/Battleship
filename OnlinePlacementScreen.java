import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class OnlinePlacementScreen extends JFrame {
    public OnlinePlacementScreen(OnlineMatchSession session, Player localPlayer, Player remotePlayer, boolean host) {
        this.setTitle(host ? "Online Placement - Host" : "Online Placement - Client");
        this.setResizable(true);
        Rectangle usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setSize(Math.min(1220, usableBounds.width), Math.min(860, usableBounds.height));
        this.setMinimumSize(new Dimension(1024, 720));
        this.setLocation(usableBounds.x + (usableBounds.width - this.getWidth()) / 2,
            usableBounds.y + (usableBounds.height - this.getHeight()) / 2);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout(16, 16));
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

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

        JLabel title = new JLabel(localPlayer.name + " placing ships", SwingConstants.CENTER);
        this.add(title, BorderLayout.NORTH);

        JPanel boardGridPanel = new JPanel();
        localPlayer.bgrid = new ButtonGrid(boardGridPanel, true);
        JPanel boardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardWrapper.add(boardGridPanel);

        JButton place = new JButton("Place on grid");
        JButton clearAll = new JButton("Clear all");
        JButton done = new JButton("Done");

        JPanel centerPanel = new JPanel(new BorderLayout(16, 16));
        centerPanel.add(boardWrapper, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        controls.setPreferredSize(new Dimension(250, 10));
        Font actionFont = AppFonts.uiBold(26f);
        Dimension actionSize = new Dimension(220, 62);
        place.setFont(actionFont);
        place.setPreferredSize(actionSize);
        place.setMaximumSize(actionSize);
        clearAll.setFont(actionFont);
        clearAll.setPreferredSize(actionSize);
        clearAll.setMaximumSize(actionSize);
        done.setFont(actionFont);
        done.setPreferredSize(actionSize);
        done.setMaximumSize(actionSize);
        place.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        done.setAlignmentX(Component.CENTER_ALIGNMENT);
        controls.add(place);
        controls.add(Box.createVerticalStrut(24));
        controls.add(clearAll);
        controls.add(Box.createVerticalStrut(24));
        controls.add(done);

        centerPanel.add(controls, BorderLayout.EAST);
        this.add(centerPanel, BorderLayout.CENTER);

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

        clearAll.addActionListener(new ActionListener() {
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

        done.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(f, "Confirm your grid?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                if (res != 0) {
                    return;
                }

                for (int i = 0; i < 5; i++) {
                    if (localPlayer.bt[i].gridCoord.isEmpty()) {
                        JOptionPane.showConfirmDialog(f,
                                "All ships have not been placed. Please place all ships first",
                                "Error",
                                JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }

                done.setEnabled(false);
                place.setEnabled(false);
                clearAll.setEnabled(false);

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
                            clearAll.setEnabled(true);
                        });
                    }
                }, "Battleship-OnlinePlacementSync");
                syncThread.setDaemon(true);
                syncThread.start();
            }
        });

        this.setVisible(true);
    }
}
