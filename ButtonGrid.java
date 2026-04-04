import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonGrid {
        JButton grid[][];
        boolean setMode;
        int no_of_ships_placed;
        private static final int GRID_SIZE = 10;
        private static final int DEFAULT_CELL_SIZE = 40;

    public ButtonGrid(JPanel boardPanel, boolean setM){
        this(boardPanel, DEFAULT_CELL_SIZE, setM);
    }

    public ButtonGrid(JPanel boardPanel, int cellSize, boolean setM){
        setMode = setM;
        no_of_ships_placed = 0;

        int boardSize = GRID_SIZE * cellSize;
        boardPanel.removeAll();
        boardPanel.setLayout(null);
        boardPanel.setOpaque(true);
        boardPanel.setBackground(Color.BLACK);
        boardPanel.setPreferredSize(new Dimension(boardSize, boardSize));
        boardPanel.setMinimumSize(new Dimension(boardSize, boardSize));
        boardPanel.setMaximumSize(new Dimension(boardSize, boardSize));

        grid = new JButton[GRID_SIZE][GRID_SIZE];
        for(int i = 0; i < GRID_SIZE; i++){
            for(int j = 0; j < GRID_SIZE; j++){
                grid[i][j] = new JButton();
                grid[i][j].setBounds(j * cellSize, i * cellSize, cellSize, cellSize);
                grid[i][j].setOpaque(true);
                grid[i][j].setBackground(Color.BLACK);
                grid[i][j].setMargin(new Insets(0, 0, 0, 0));
                grid[i][j].setBorderPainted(true);
                grid[i][j].setContentAreaFilled(true);
                grid[i][j].setFont(AppFonts.uiBold(14f));
                grid[i][j].setFocusPainted(false);
                final int i_c = i;
                final int j_c = j; 
                grid[i][j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        //logic while player is setting grid:
                        if(setMode){
                            if(grid[i_c][j_c].getBackground()==Color.BLACK){
                                grid[i_c][j_c].setBackground(Color.GREEN);
                            }
                        }
                        //Tutorial logic?:
                        else{}
                    }
                });
                boardPanel.add(grid[i][j]);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

}
