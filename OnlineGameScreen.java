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
        
        ScreenScaler.initialize();

        JFrame f = this;
        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e) {
                if (matchEnded.get()) {
                    return;
                }

                int ch = JOptionPane.showConfirmDialog(f,
                        "Are you sure you want to exit",
                        "Confirm exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (ch) {
                    case 0:
                        endMatch("Match exited.", true, null, OnlineProtocol.serializeForfeit(localPlayer.name), null);
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

        JLabel localTitle = new JLabel(localPlayer.name + "'s board", SwingConstants.CENTER);
        localTitle.setBounds(ScreenScaler.scaleX(200), ScreenScaler.scaleY(100), ScreenScaler.scaleX(400), ScreenScaler.scaleY(50));
        this.add(localTitle);

        JLabel remoteTitle = new JLabel(remotePlayer.name + "'s board", SwingConstants.CENTER);
        remoteTitle.setBounds(ScreenScaler.scaleX(1020), ScreenScaler.scaleY(100), ScreenScaler.scaleX(400), ScreenScaler.scaleY(50));
        this.add(remoteTitle);

        turnLabel = new JLabel(localPlayer.name + "'s turn", SwingConstants.CENTER);
        turnLabel.setBounds(ScreenScaler.scaleX(720), ScreenScaler.scaleY(100), ScreenScaler.scaleX(160), ScreenScaler.scaleY(30));
        this.add(turnLabel);

        localDisplayGrid = new ButtonGrid(this, ScreenScaler.scaleX(200), ScreenScaler.scaleY(200), ScreenScaler.scaleX(600), ScreenScaler.scaleY(600), ScreenScaler.scaleX(40), ScreenScaler.scaleY(40), false);
        attackGrid = new ButtonGrid(this, ScreenScaler.scaleX(1020), ScreenScaler.scaleY(200), ScreenScaler.scaleX(1420), ScreenScaler.scaleY(600), ScreenScaler.scaleX(40), ScreenScaler.scaleY(40), true);

        OnlineProtocol.paintPlacement(localDisplayGrid, localPlayer);

        JButton shoot = new JButton("Shoot");
        shoot.setBounds(ScreenScaler.scaleX(740), ScreenScaler.scaleY(200), ScreenScaler.scaleX(120), ScreenScaler.scaleY(20));
        this.add(shoot);

        announcer = new JLabel("Announcements", SwingConstants.CENTER);
        announcer.setVisible(false);
        announcer.setBounds(ScreenScaler.scaleX(720), ScreenScaler.scaleY(260), ScreenScaler.scaleX(180), ScreenScaler.scaleY(20));
        this.add(announcer);

        announcements = new JTextArea("");
        announcements.setEditable(false);
        JScrollPane annScroller = new JScrollPane(announcements, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        annScroller.setBounds(ScreenScaler.scaleX(720), ScreenScaler.scaleY(300), ScreenScaler.scaleX(180), ScreenScaler.scaleY(220));
        this.add(annScroller);

        shoot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!localTurn.get()) {
                    JOptionPane.showMessageDialog(f, "Please wait for " + remotePlayer.name + "'s turn.", "Wait", JOptionPane.PLAIN_MESSAGE);
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
                        announcements.append(result.sunkShipName + " of " + remotePlayer.name + " sunk\n");
                    }

                    session.sendLine(OnlineProtocol.serializeIncomingAttack(result));

                    if (result.gameOver) {
                        endMatch(localPlayer.name + " won the match.", true, localPlayer.name, OnlineProtocol.serializeMatchEnd(), null);
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
                            endMatch("Match ended. Connection closed.", false, null, null, null);
                        }
                        break;
                    }

                    String forfeitedPlayerName = OnlineProtocol.parseForfeit(message);
                    if (forfeitedPlayerName != null) {
                        if (running && !matchEnded.get()) {
                            endMatch(
                                    "Match ended. " + forfeitedPlayerName + " forfeited.",
                                    false,
                                    null,
                                    null,
                                    forfeitedPlayerName + " forfeited. Match ended.");
                        }
                        break;
                    }

                    if (OnlineProtocol.isMatchEnd(message)) {
                        if (running && !matchEnded.get()) {
                            endMatch("Match ended.", false, null, null, null);
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
                                    announcements.append(result.sunkShipName + " of " + localPlayer.name + " sunk\n");
                                }
                                if (result.gameOver) {
                                    endMatch(remotePlayer.name + " won the match.", true, remotePlayer.name, OnlineProtocol.serializeMatchEnd(), null);
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
                                    announcements.append(incoming.sunkShipName + " of " + localPlayer.name + " sunk\n");
                                }
                                if (incoming.gameOver) {
                                    endMatch(remotePlayer.name + " won the match.", true, remotePlayer.name, OnlineProtocol.serializeMatchEnd(), null);
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
                                    announcements.append(shotResult.sunkShipName + " of " + remotePlayer.name + " sunk\n");
                                }
                                if (shotResult.gameOver) {
                                    endMatch(localPlayer.name + " won the match.", true, localPlayer.name, OnlineProtocol.serializeMatchEnd(), null);
                                } else {
                                    localTurn.set(false);
                                    updateTurnLabel();
                                }
                            });
                        }
                    }
                } catch (Exception ex) {
                    if (running && !matchEnded.get()) {
                        endMatch("Match ended. Connection lost.", false, null, null, null);
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
        SwingUtilities.invokeLater(() -> {
            String turnName = localTurn.get() ? localPlayer.name : remotePlayer.name;
            turnLabel.setText(turnName + "'s turn");
        });
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

    private void endMatch(String summary, boolean notifyPeer, String winnerPlayerName, String peerEndMessage, String extraDialogMessage) {
        if (!matchEnded.compareAndSet(false, true)) {
            return;
        }

        running = false;
        if (notifyPeer && session.isOpen()) {
            try {
                session.sendLine(peerEndMessage == null ? OnlineProtocol.serializeMatchEnd() : peerEndMessage);
            } catch (Exception ignored) {
            }
        }

        Thread closer = new Thread(() -> session.close(), "Battleship-OnlineSessionCloser");
        closer.setDaemon(true);
        closer.start();

        SwingUtilities.invokeLater(() -> {
            announcer.setVisible(true);
            announcements.append(summary + "\n");
            if (winnerPlayerName != null) {
                JOptionPane.showMessageDialog(this,
                        winnerPlayerName + " has won",
                        "Game over",
                        JOptionPane.PLAIN_MESSAGE);
            }
            if (extraDialogMessage != null) {
                JOptionPane.showMessageDialog(this,
                        extraDialogMessage,
                        "Match ended",
                        JOptionPane.PLAIN_MESSAGE);
            }
            dispose();
            new LoadingScreen();
        });
    }
}