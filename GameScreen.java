import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.*;

public class GameScreen extends JFrame{
    Player controlPlayer;
    Player passivePlayer;
    GameScreen(){
        this.setTitle("Game");
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        ScreenScaler.initialize();
        JFrame f = this;//incase we want to use current frame inside ActionListener

        //window listener, in case accidentally closed
        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e){
                int ch = JOptionPane.showConfirmDialog(f, 
                              "Are you sure you want to exit", 
                              "Confirm exit", JOptionPane.YES_NO_CANCEL_OPTION);
                switch(ch){
                    case 0://yes
                        new LoadingScreen();
                        f.dispose();
                    break;
                    case 1://no
                    case 2://cancel
                        break;    

                }
            }
            public void windowOpened(WindowEvent e){}
            public void windowDeiconified(WindowEvent e){}
            public void windowActivated(WindowEvent e){}
            public void windowClosed(WindowEvent e){}
            public void windowDeactivated(WindowEvent e){} 
            public void windowIconified(WindowEvent e){}

        });

        GameMain.p1.dg = new ButtonGrid(this, ScreenScaler.scaleX(200), ScreenScaler.scaleY(200), ScreenScaler.scaleX(620), ScreenScaler.scaleY(600), ScreenScaler.scaleX(42), ScreenScaler.scaleY(40), true);
        GameMain.p2.dg = new ButtonGrid(this, ScreenScaler.scaleX(1020), ScreenScaler.scaleY(200), ScreenScaler.scaleX(1440), ScreenScaler.scaleY(600), ScreenScaler.scaleX(42), ScreenScaler.scaleY(40), true);

        JButton shoot = new JButton("Shoot");
        shoot.setBounds(ScreenScaler.scaleX(740),ScreenScaler.scaleY(200),ScreenScaler.scaleX(120),ScreenScaler.scaleY(20));
        this.add(shoot);
        
        JLabel pl_1 = new JLabel(GameMain.p1.name+" choosing to shoot...", SwingConstants.CENTER);
        pl_1.setBounds(ScreenScaler.scaleX(200),ScreenScaler.scaleY(100),ScreenScaler.scaleX(420),ScreenScaler.scaleY(50));
        this.add(pl_1);
        JLabel pl_2 = new JLabel(GameMain.p2.name+" choosing to shoot...", SwingConstants.CENTER);
        pl_2.setBounds(ScreenScaler.scaleX(1020),ScreenScaler.scaleY(100),ScreenScaler.scaleX(420),ScreenScaler.scaleY(50));
        this.add(pl_2);

        JLabel announcer = new JLabel("Announcements",SwingConstants.CENTER);
        announcer.setVisible(false);
        //set announcer font
        announcer.setBounds(ScreenScaler.scaleX(740), ScreenScaler.scaleY(260), ScreenScaler.scaleX(160), ScreenScaler.scaleY(20));
        this.add(announcer);

        JTextArea announcements = new JTextArea("");
        announcements.setBounds(0, 0, 1000, 700);
        JScrollPane ann_scroller = new JScrollPane(announcements, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        ann_scroller.setBounds(ScreenScaler.scaleX(740), ScreenScaler.scaleY(300), ScreenScaler.scaleX(160), ScreenScaler.scaleY(200));
        this.add(ann_scroller);

        controlPlayer = GameMain.p1;//initially, changes based on who is supposed to move currently
        passivePlayer = GameMain.p2;//initially, changes based on who is the player supposed to move after current player's move

        shoot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                announcer.setVisible(false);//always false whenever the player clicks shoot. If sinking, then it becomes visible
                ArrayList<Integer[]> coord = new ArrayList<Integer[]>();
                for(int i = 0; i<10; i++){
                    for(int j = 0; j<10; j++){
                        if(controlPlayer.dg.grid[i][j].getBackground()==Color.GREEN){
                            Integer[] arr = new Integer[2];
                            arr[0] = i; arr[1] = j;
                            coord.add(arr);
                        }
                    }
                }
                if(coord.size()==1){
                    controlPlayer.dg.grid[coord.get(0)[0]][coord.get(0)[1]].setBackground(Color.WHITE);//make button red
                    controlPlayer.dg.grid[coord.get(0)[0]][coord.get(0)[1]].setForeground(Color.RED);//make button red
                    //arraylist logic  -> mark with cross if it is a hit
                    for(int i = 0; i<5;i++){
                        for(int j = 0; j<passivePlayer.bt[i].gridCoord.size(); j++){
                            if(passivePlayer.bt[i].gridCoord.get(j)[0]==coord.get(0)[0] && passivePlayer.bt[i].gridCoord.get(j)[1]==coord.get(0)[1]){
                                controlPlayer.dg.grid[coord.get(0)[0]][coord.get(0)[1]].setText("X");//make button red
                                passivePlayer.bt[i].gridCoord.remove(j);
                                if(passivePlayer.bt[i].gridCoord.size()==0){
                                    //announce sinking
                                    passivePlayer.ships_lost++;
                                    announcer.setVisible(true);
                                    announcements.append(passivePlayer.bt[i].type.getName()+" of "+passivePlayer.name+" sunk\n");
                                }
                            }
                        }
                    }
                    //check if a player has lost all his ships or not
                    if(passivePlayer.ships_lost==5){
                        JOptionPane.showMessageDialog(f, controlPlayer.name+" has won", "Game over", JOptionPane.PLAIN_MESSAGE);                                      
                        new LoadingScreen();
                        f.dispose();
                    }
                    //change controlPlayer so that control shifts to other player
                    if(controlPlayer.equals(GameMain.p1)){
                        controlPlayer = GameMain.p2;
                        passivePlayer = GameMain.p1;
                        pl_1.setVisible(false);
                        pl_2.setVisible(true);
                    }
                    else if(controlPlayer.equals(GameMain.p2)){
                        controlPlayer = GameMain.p1;
                        passivePlayer = GameMain.p2;
                        pl_1.setVisible(true);
                        pl_2.setVisible(false);
                    }
                }
                else if(coord.size()==0){
                    JOptionPane.showMessageDialog(f, "Please select a square to shoot at.", "Error", JOptionPane.PLAIN_MESSAGE);                                      
                }
                else{
                    JOptionPane.showMessageDialog(f, "You are not allowed to shoot at more than one square at a time. Please reselect.", "Error", JOptionPane.PLAIN_MESSAGE);                   
                    for(int i = 0; i<coord.size(); i++){
                        controlPlayer.dg.grid[coord.get(i)[0]][coord.get(i)[1]].setBackground(Color.BLACK);
                    }
                    coord.clear();
                }
            }
        });
        
    }
}
