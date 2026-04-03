import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class OnlineGameScreen extends JFrame {
    private final OnlineMatchSession session;
    private final Player localPlayer;
    private final Player remotePlayer;
    private final boolean host;
    private final AtomicBoolean localTurn;
    private final AtomicBoolean matchEnded;
    private volatile boolean running;
    private ButtonGrid localDisplayGrid;
    private ButtonGrid attackGrid;
    private JLabel turnLabel;
    private JLabel announcer;
    private JTextArea announcements;

    public OnlineGameScreen(OnlineMatchSession session, Player localPlayer, Player remotePlayer, boolean host) {
        this.session = session;
        this.localPlayer = localPlayer;
        this.remotePlayer = remotePlayer;
        this.host = host;
        this.localTurn = new AtomicBoolean(host);
        this.matchEnded = new AtomicBoolean(false);
        this.running = true;

        this.setTitle(host ? "Online Game - Host" : "Online Game - Client");
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(null);

        JFrame f = this;
        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
                int ch = JOptionPane.showConfirmDialog(f,
                        "Are you sure you want to exit",
                        "Confirm exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (ch) {
                    case 0:
                        running = false;
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

        JLabel localTitle = new JLabel("Your board", SwingConstants.CENTER);
        localTitle.setBounds(200, 100, 400, 50);
        this.add(localTitle);

        JLabel remoteTitle = new JLabel("Opponent board", SwingConstants.CENTER);
        remoteTitle.setBounds(1020, 100, 400, 50);
        this.add(remoteTitle);

        turnLabel = new JLabel("Your turn", SwingConstants.CENTER);
        turnLabel.setBounds(720, 100, 160, 30);
        this.add(turnLabel);

        localDisplayGrid = new ButtonGrid(this, 200, 200, 600, 600, 40, 40, false);
        attackGrid = new ButtonGrid(this, 1020, 200, 1420, 600, 40, 40, true);

        OnlineProtocol.paintPlacement(localDisplayGrid, localPlayer);

        JButton shoot = new JButton("Shoot");
        shoot.setBounds(740, 200, 120, 20);
        this.add(shoot);

        announcer = new JLabel("Announcements", SwingConstants.CENTER);
        announcer.setVisible(false);
        announcer.setBounds(720, 260, 180, 20);
        this.add(announcer);

        announcements = new JTextArea("");
        announcements.setEditable(false);
        JScrollPane annScroller = new JScrollPane(announcements, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        annScroller.setBounds(720, 300, 180, 220);
        this.add(annScroller);

        shoot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!localTurn.get()) {
                    JOptionPane.showMessageDialog(f, "Please wait for your turn.", "Wait", JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                ArrayList<Integer[]> coord = getSelectedCoordinate(attackGrid);
                if (coord.size() != 1) {
                    if (coord.size() == 0) {
                        JOptionPane.showMessageDialog(f, "Please select a square to shoot at.", "Error", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(f, "You are not allowed to shoot at more than one square at a time. Please reselect.", "Error", JOptionPane.PLAIN_MESSAGE);
                        for (int i = 0; i < coord.size(); i++) {
                            attackGrid.grid[coord.get(i)[0]][coord.get(i)[1]].setBackground(Color.BLACK);
                        }
                    }
                    return;
                }

                int row = coord.get(0)[0];
                int col = coord.get(0)[1];

                if (host) {
                    OnlineProtocol.ShotResult result = OnlineProtocol.resolveShot(remotePlayer, row, col);
                    OnlineProtocol.paintShot(attackGrid, row, col, result.hit);
                    if (result.sunkShipName != null) {
                        announcer.setVisible(true);
                        announcements.append(result.sunkShipName + " of player " + remotePlayer.playerno + " sunk\n");
                    }

                    session.sendLine(OnlineProtocol.serializeIncomingAttack(result));

                    if (result.gameOver) {
                        endMatch("You won the match.");
                        return;
                    }

                    localTurn.set(false);
                    updateTurnLabel();
                } else {
                    session.sendLine(OnlineProtocol.serializeShot(row, col));
                    localTurn.set(false);
                    updateTurnLabel();
                }
            }
        });

        Thread receiverThread = new Thread(() -> {
            while (running && session.isOpen()) {
                try {
                    String message = session.readLine();
                    if (message == null) {
                        if (running && !matchEnded.get()) {
                            endMatch("Match ended. Connection closed.");
                        }
                        break;
                    }

                    if (host) {
                        OnlineProtocol.ShotMessage shot = OnlineProtocol.parseShot(message);
                        if (shot != null) {
                            OnlineProtocol.ShotResult result = OnlineProtocol.resolveShot(localPlayer, shot.row, shot.col);
                            SwingUtilities.invokeLater(() -> {
                                OnlineProtocol.paintShot(localDisplayGrid, shot.row, shot.col, result.hit);
                                if (result.sunkShipName != null) {
                                    announcer.setVisible(true);
                                    announcements.append(result.sunkShipName + " of player " + localPlayer.playerno + " sunk\n");
                                }
                                if (result.gameOver) {
                                    endMatch("You lost the match.");
                                } else {
                                    localTurn.set(true);
                                    updateTurnLabel();
                                }
                            });
                            session.sendLine(OnlineProtocol.serializeShotResult(result));
                        }
                    } else {
                        OnlineProtocol.ShotResult incoming = OnlineProtocol.parseIncomingAttack(message);
                        if (incoming != null) {
                            SwingUtilities.invokeLater(() -> {
                                OnlineProtocol.paintShot(localDisplayGrid, incoming.row, incoming.col, incoming.hit);
                                if (incoming.sunkShipName != null) {
                                    announcer.setVisible(true);
                                    announcements.append(incoming.sunkShipName + " of player " + localPlayer.playerno + " sunk\n");
                                }
                                if (incoming.gameOver) {
                                    endMatch("You lost the match.");
                                } else {
                                    localTurn.set(true);
                                    updateTurnLabel();
                                }
                            });
                            continue;
                        }

                        OnlineProtocol.ShotResult shotResult = OnlineProtocol.parseShotResult(message);
                        if (shotResult != null) {
                            SwingUtilities.invokeLater(() -> {
                                OnlineProtocol.paintShot(attackGrid, shotResult.row, shotResult.col, shotResult.hit);
                                if (shotResult.sunkShipName != null) {
                                    announcer.setVisible(true);
                                    announcements.append(shotResult.sunkShipName + " of player " + remotePlayer.playerno + " sunk\n");
                                }
                                if (shotResult.gameOver) {
                                    endMatch("You won the match.");
                                } else {
                                    localTurn.set(false);
                                    updateTurnLabel();
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    if (running && !matchEnded.get()) {
                        endMatch("Match ended. Connection lost.");
                    }
                    break;
                }
            }
        }, "Battleship-OnlineReceiver");
        receiverThread.setDaemon(true);
        receiverThread.start();

        updateTurnLabel();
    }

    private void updateTurnLabel() {
        SwingUtilities.invokeLater(() -> turnLabel.setText(localTurn.get() ? "Your turn" : "Waiting..."));
    }

    private static ArrayList<Integer[]> getSelectedCoordinate(ButtonGrid grid) {
        ArrayList<Integer[]> coord = new ArrayList<Integer[]>();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (grid.grid[i][j].getBackground() == Color.GREEN) {
                    Integer[] arr = new Integer[2];
                    arr[0] = i;
                    arr[1] = j;
                    coord.add(arr);
                }
            }
        }
        return coord;
    }

    private void endMatch(String summary) {
        if (!matchEnded.compareAndSet(false, true)) {
            return;
        }

        running = false;
        SwingUtilities.invokeLater(() -> {
            announcer.setVisible(true);
            announcements.append(summary + "\n");
            session.close();
            dispose();
            new LoadingScreen();
        });
    }
}