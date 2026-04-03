import javax.swing.*;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonGrid {
        JButton grid[][];
        boolean setMode;
        int no_of_ships_placed;

        public ButtonGrid(JFrame f, int xt, int yt, int xb, int yb, int buttonLength, int buttonHeight, boolean setM){
        setMode = setM;
        no_of_ships_placed = 0;
    
        /*Sample code to create a black button and add to frame:
        
        JButton g1 = new JButton();
        g1.setBounds(0,0,40,40);
        g1.setOpaque(true);
        g1.setBackground(Color.BLACK);
        this.add(g1);
        
        */

        //buttonlength = along x, buttonHeight = along y
       
        grid = new JButton[(xb-xt)/(buttonLength)][(yb-yt)/(buttonHeight)];
        for(int i = 0; i<(yb-yt)/buttonHeight; i++){
            for(int j = 0; j<(xb-xt)/buttonLength; j++){
                grid[i][j] = new JButton();
                grid[i][j].setBounds(xt+(buttonLength*j), yt+(buttonHeight*i), buttonLength, buttonHeight);
                grid[i][j].setOpaque(true);
                grid[i][j].setBackground(Color.BLACK);
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
                f.add(grid[i][j]);
            }
        }

    }

}
