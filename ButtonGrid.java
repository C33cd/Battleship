import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonGrid {
        JButton grid[][];
        boolean setMode;
        int no_of_ships_placed;

    public ButtonGrid(JPanel boardPanel, boolean setM){
        setMode = setM;
        no_of_ships_placed = 0;
    
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(10, 10, 0, 0));
        boardPanel.setOpaque(true);
        boardPanel.setBackground(Color.BLACK);

        grid = new JButton[10][10];
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                grid[i][j] = new JButton();
                grid[i][j].setOpaque(true);
                grid[i][j].setBackground(Color.BLACK);
                grid[i][j].setMargin(new Insets(0, 0, 0, 0));
                grid[i][j].setBorder(BorderFactory.createMatteBorder(
                    1,
                    1,
                    i == 9 ? 1 : 0,
                    j == 9 ? 1 : 0,
                    new Color(65, 65, 65)));
                grid[i][j].setContentAreaFilled(true);
                grid[i][j].setFont(new Font("Dialog", Font.BOLD, 14));
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
