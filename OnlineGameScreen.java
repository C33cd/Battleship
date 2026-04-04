import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class OnlineGameScreen extends JFrame {
    private final OnlineMatchSession session;
    private final Player localPlayer;
    private final Player remotePlayer;
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
        this.localTurn = new AtomicBoolean(host);
        this.matchEnded = new AtomicBoolean(false);
        this.running = true;

        this.setTitle(host ? "Online Game - Host" : "Online Game - Client");
        this.setResizable(true);
        Rectangle usableBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setSize(Math.min(1360, usableBounds.width), Math.min(860, usableBounds.height));
        this.setLocation(usableBounds.x + (usableBounds.width - this.getWidth()) / 2,
            usableBounds.y + (usableBounds.height - this.getHeight()) / 2);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());
        ((JPanel) this.getContentPane()).setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

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

        JPanel localBoardPanel = new JPanel();
        JPanel attackBoardPanel = new JPanel();
        localDisplayGrid = new ButtonGrid(localBoardPanel, false);
        attackGrid = new ButtonGrid(attackBoardPanel, true);

        OnlineProtocol.paintPlacement(localDisplayGrid, localPlayer);
        JPanel localWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        localWrapper.add(localBoardPanel);
        JPanel attackWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        attackWrapper.add(attackBoardPanel);

        JLabel localTitle = new JLabel(localPlayer.name + "'s board", SwingConstants.CENTER);
        JLabel remoteTitle = new JLabel(remotePlayer.name + "'s board", SwingConstants.CENTER);

        JPanel leftColumn = new JPanel(new BorderLayout(0, 10));
        leftColumn.add(localTitle, BorderLayout.NORTH);
        leftColumn.add(localWrapper, BorderLayout.CENTER);

        JPanel rightColumn = new JPanel(new BorderLayout(0, 10));
        rightColumn.add(remoteTitle, BorderLayout.NORTH);
        rightColumn.add(attackWrapper, BorderLayout.CENTER);

        JButton shoot = new JButton("Shoot");
        shoot.setFont(new Font("Dialog", Font.BOLD, 24));
        shoot.setPreferredSize(new Dimension(220, 62));
        turnLabel = new JLabel(localPlayer.name + "'s turn", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Dialog", Font.BOLD, 20));

        announcer = new JLabel("Announcements", SwingConstants.CENTER);
        announcer.setFont(new Font("Dialog", Font.BOLD, 18));
        announcer.setVisible(false);

        announcements = new JTextArea(10, 16);
        announcements.setEditable(false);
        JScrollPane annScroller = new JScrollPane(
                announcements,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JPanel centerColumn = new JPanel(new BorderLayout(0, 10));
        centerColumn.setPreferredSize(new Dimension(300, 10));
        JPanel topActions = new JPanel(new BorderLayout(0, 8));
        topActions.add(turnLabel, BorderLayout.NORTH);
        topActions.add(shoot, BorderLayout.SOUTH);
        centerColumn.add(topActions, BorderLayout.NORTH);

        JPanel announcementPanel = new JPanel(new BorderLayout(0, 8));
        announcementPanel.add(announcer, BorderLayout.NORTH);
        announcementPanel.add(annScroller, BorderLayout.CENTER);
        centerColumn.add(announcementPanel, BorderLayout.CENTER);

        JPanel shell = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.BOTH;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        shell.add(leftColumn, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.0;
        shell.add(centerColumn, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        shell.add(rightColumn, gbc);

        this.add(shell, BorderLayout.CENTER);

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
        this.setVisible(true);
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
