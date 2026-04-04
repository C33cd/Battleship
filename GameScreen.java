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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class GameScreen extends JFrame {
    Player controlPlayer;
    Player passivePlayer;

    GameScreen() {
        this.setTitle("Game");
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
                int ch = JOptionPane.showConfirmDialog(f,
                        "Are you sure you want to exit",
                        "Confirm exit",
                        JOptionPane.YES_NO_CANCEL_OPTION);
                switch (ch) {
                    case 0:
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

        JPanel leftBoardPanel = new JPanel();
        JPanel rightBoardPanel = new JPanel();
        GameMain.p1.dg = new ButtonGrid(leftBoardPanel, true);
        GameMain.p2.dg = new ButtonGrid(rightBoardPanel, true);
        JPanel leftBoardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        leftBoardWrapper.add(leftBoardPanel);
        JPanel rightBoardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        rightBoardWrapper.add(rightBoardPanel);

        JLabel leftTitle = new JLabel(GameMain.p1.name + " choosing to shoot...", SwingConstants.CENTER);
        JLabel rightTitle = new JLabel(GameMain.p2.name + " choosing to shoot...", SwingConstants.CENTER);

        JPanel leftColumn = new JPanel(new BorderLayout(0, 10));
        leftColumn.add(leftTitle, BorderLayout.NORTH);
        leftColumn.add(leftBoardWrapper, BorderLayout.CENTER);

        JPanel rightColumn = new JPanel(new BorderLayout(0, 10));
        rightColumn.add(rightTitle, BorderLayout.NORTH);
        rightColumn.add(rightBoardWrapper, BorderLayout.CENTER);

        JButton shoot = new JButton("Shoot");
        shoot.setFont(new Font("Dialog", Font.BOLD, 24));
        shoot.setPreferredSize(new Dimension(220, 62));
        JLabel turnLabel = new JLabel(GameMain.p1.name + "'s turn", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        JLabel announcer = new JLabel("Announcements", SwingConstants.CENTER);
        announcer.setFont(new Font("Dialog", Font.BOLD, 18));
        announcer.setVisible(false);

        JTextArea announcements = new JTextArea(10, 16);
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

        controlPlayer = GameMain.p1;
        passivePlayer = GameMain.p2;

        shoot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                announcer.setVisible(false);
                ArrayList<Integer[]> coord = new ArrayList<Integer[]>();
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (controlPlayer.dg.grid[i][j].getBackground() == Color.GREEN) {
                            Integer[] arr = new Integer[2];
                            arr[0] = i;
                            arr[1] = j;
                            coord.add(arr);
                        }
                    }
                }
                if (coord.size() == 1) {
                    controlPlayer.dg.grid[coord.get(0)[0]][coord.get(0)[1]].setBackground(Color.WHITE);
                    controlPlayer.dg.grid[coord.get(0)[0]][coord.get(0)[1]].setForeground(Color.RED);
                    for (int i = 0; i < 5; i++) {
                        for (int j = 0; j < passivePlayer.bt[i].gridCoord.size(); j++) {
                            if (passivePlayer.bt[i].gridCoord.get(j)[0] == coord.get(0)[0]
                                    && passivePlayer.bt[i].gridCoord.get(j)[1] == coord.get(0)[1]) {
                                controlPlayer.dg.grid[coord.get(0)[0]][coord.get(0)[1]].setText("X");
                                passivePlayer.bt[i].gridCoord.remove(j);
                                if (passivePlayer.bt[i].gridCoord.size() == 0) {
                                    passivePlayer.ships_lost++;
                                    announcer.setVisible(true);
                                    announcements.append(passivePlayer.bt[i].type.getName() + " of " + passivePlayer.name + " sunk\n");
                                }
                            }
                        }
                    }

                    if (passivePlayer.ships_lost == 5) {
                        JOptionPane.showMessageDialog(f, controlPlayer.name + " has won", "Game over", JOptionPane.PLAIN_MESSAGE);
                        new LoadingScreen();
                        f.dispose();
                        return;
                    }

                    if (controlPlayer.equals(GameMain.p1)) {
                        controlPlayer = GameMain.p2;
                        passivePlayer = GameMain.p1;
                    } else if (controlPlayer.equals(GameMain.p2)) {
                        controlPlayer = GameMain.p1;
                        passivePlayer = GameMain.p2;
                    }
                    turnLabel.setText(controlPlayer.name + "'s turn");
                } else if (coord.size() == 0) {
                    JOptionPane.showMessageDialog(f, "Please select a square to shoot at.", "Error", JOptionPane.PLAIN_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(f,
                            "You are not allowed to shoot at more than one square at a time. Please reselect.",
                            "Error",
                            JOptionPane.PLAIN_MESSAGE);
                    for (int i = 0; i < coord.size(); i++) {
                        controlPlayer.dg.grid[coord.get(i)[0]][coord.get(i)[1]].setBackground(Color.BLACK);
                    }
                    coord.clear();
                }
            }
        });

        this.setVisible(true);
    }
}
