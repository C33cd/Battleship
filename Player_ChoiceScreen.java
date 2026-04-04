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

public class Player_ChoiceScreen extends JFrame {
    public Player_ChoiceScreen(Player player) {
        this.setTitle(player.name + " Choice Grid");
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
                boolean allShipsPlaced = true;
                for (int i = 0; i < 5; i++) {
                    if (player.bt[i].gridCoord.isEmpty()) {
                        allShipsPlaced = false;
                        break;
                    }
                }

                if (!allShipsPlaced) {
                    int res = JOptionPane.showConfirmDialog(f,
                            "You haven't finished placing your ships. Do you want to forfeit the game?",
                            "Forfeit Game",
                            JOptionPane.YES_NO_OPTION);

                    if (res == JOptionPane.YES_OPTION) {
                        new LoadingScreen();
                        f.dispose();
                    }
                } else {
                    new LoadingScreen();
                    f.dispose();
                }
            }

            public void windowOpened(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowClosed(WindowEvent e) {
                boolean allShipsPlaced = true;
                for (int i = 0; i < 5; i++) {
                    if (player.bt[i].gridCoord.isEmpty()) {
                        allShipsPlaced = false;
                        break;
                    }
                }

                if (player.playerno == 1) {
                    if (!allShipsPlaced) {
                        JOptionPane.showMessageDialog(f,
                                "Player 1 has not completed ship placement. Player 2 wins by default. Returning to loading screen.",
                                "Game Over",
                                JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(f,
                                "Your ships have been successfully placed. Please let " + GameMain.p2.name + " make their arrangement of ships",
                                "Success",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                } else if (player.playerno == 2) {
                    if (!allShipsPlaced) {
                        JOptionPane.showMessageDialog(f,
                                "Player 2 has not completed ship placement. Player 1 wins by default. Returning to loading screen.",
                                "Game Over",
                                JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(f,
                                "Your ships have been successfully placed. You can call " + GameMain.p1.name + " back to play. \n" + GameMain.p1.name + " starts first",
                                "Success",
                                JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }

            public void windowDeactivated(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
        });

        JLabel title = new JLabel(player.name + " choosing", SwingConstants.CENTER);
        this.add(title, BorderLayout.NORTH);

        JPanel boardGridPanel = new JPanel();
        player.bgrid = new ButtonGrid(boardGridPanel, true);
        JPanel boardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        boardWrapper.add(boardGridPanel);

        JPanel centerPanel = new JPanel(new BorderLayout(16, 16));
        centerPanel.add(boardWrapper, BorderLayout.CENTER);

        JButton place = new JButton("Place on grid");
        JButton clearAll = new JButton("Clear all");
        JButton setGrid = new JButton("Done");

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        controls.setPreferredSize(new Dimension(250, 10));
        Font actionFont = new Font("Dialog", Font.BOLD, 26);
        Dimension actionSize = new Dimension(220, 62);
        place.setFont(actionFont);
        place.setPreferredSize(actionSize);
        place.setMaximumSize(actionSize);
        clearAll.setFont(actionFont);
        clearAll.setPreferredSize(actionSize);
        clearAll.setMaximumSize(actionSize);
        setGrid.setFont(actionFont);
        setGrid.setPreferredSize(actionSize);
        setGrid.setMaximumSize(actionSize);
        place.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearAll.setAlignmentX(Component.CENTER_ALIGNMENT);
        setGrid.setAlignmentX(Component.CENTER_ALIGNMENT);
        controls.add(place);
        controls.add(Box.createVerticalStrut(24));
        controls.add(clearAll);
        controls.add(Box.createVerticalStrut(24));
        controls.add(setGrid);
        centerPanel.add(controls, BorderLayout.EAST);

        this.add(centerPanel, BorderLayout.CENTER);

        player.bgrid.no_of_ships_placed = 0;
        place.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                place.setBackground(Color.RED);
                ArrayList<Integer[]> select_coord = new ArrayList<Integer[]>();
                int noClicked = 0;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (player.bgrid.grid[i][j].getBackground() == Color.GREEN) {
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
                            player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                        }
                        select_coord.clear();
                        break;
                    case 2:
                        btship.type = Ship.PATROL_BOAT;
                        switchCheck = true;
                        break;
                    case 3:
                        if (player.bt[1].gridCoord.isEmpty()) {
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
                    if (player.bgrid.no_of_ships_placed + 1 > 5) {
                        int ch = JOptionPane.showConfirmDialog(f,
                                "You are trying to place more than 5 ships. Your latest entry will be cleared",
                                "Error",
                                JOptionPane.YES_NO_CANCEL_OPTION);
                        switch (ch) {
                            case 0:
                                while (it1.hasNext()) {
                                    Integer[] coord = it1.next();
                                    player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
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
                            player.bgrid.grid[coord[0]][coord[1]].setToolTipText(btship.type.getName());
                            player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.RED);
                            btship.gridCoord.add(coord);
                        }
                        player.bgrid.no_of_ships_placed++;
                    }
                } else {
                    btship.type = null;
                    JOptionPane.showConfirmDialog(f, "Please check your selection", "Error", JOptionPane.PLAIN_MESSAGE);
                    while (it1.hasNext()) {
                        Integer coord[] = it1.next();
                        player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                    }
                    select_coord.clear();
                }

                for (int i = 0; i < 5; i++) {
                    if (player.bt[i].type == btship.type) {
                        player.bt[i] = btship;
                    }
                }

                place.setBackground(null);
            }
        });

        clearAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player.bgrid.no_of_ships_placed = 0;
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        player.bgrid.grid[i][j].setBackground(Color.BLACK);
                    }
                }
                for (int i = 0; i < 5; i++) {
                    player.bt[i].gridCoord.clear();
                }
            }
        });

        setGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int res = JOptionPane.showConfirmDialog(f, "Confirm your grid?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                switch (res) {
                    case 0:
                        if (player.playerno == 1) {
                            boolean noCheck = false;
                            for (int i = 0; i < 5; i++) {
                                if (GameMain.p1.bt[i].gridCoord.isEmpty()) {
                                    JOptionPane.showConfirmDialog(f,
                                            "All ships have not been placed. Please place all ships first",
                                            "Error",
                                            JOptionPane.PLAIN_MESSAGE);
                                    noCheck = false;
                                    break;
                                } else {
                                    noCheck = true;
                                }
                            }
                            if (noCheck) {
                                f.dispose();
                                new Player_ChoiceScreen(GameMain.p2);
                            }
                        } else if (player.playerno == 2) {
                            new GameScreen();
                            f.dispose();
                        }
                        return;
                    case 1:
                    default:
                        break;
                }
            }
        });

        this.setVisible(true);
    }
}
