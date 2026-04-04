import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.*;
public class Player_ChoiceScreen extends JFrame{
    public Player_ChoiceScreen(Player player){
        this.setTitle(player.name+" Choice Grid");
        this.setResizable(true);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(null);
        ScreenScaler.initialize();

        JFrame f = this;//used whenever we want to refer to cuurent frame in ActionListener

        //Incase this window is closed, open up loading screen
        this.addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent e){
                // Check if all ships have been placed
                boolean allShipsPlaced = true;
                for(int i = 0; i<5; i++){
                    if(player.bt[i].gridCoord.isEmpty()){
                        allShipsPlaced = false;
                        break;
                    }
                }
                
                if(!allShipsPlaced){
                    // Show confirmation dialog for forfeit
                    int res = JOptionPane.showConfirmDialog(f, 
                        "You haven't finished placing your ships. Do you want to forfeit the game?", 
                        "Forfeit Game", 
                        JOptionPane.YES_NO_OPTION);
                    
                    if(res == JOptionPane.YES_OPTION){
                        // User confirmed forfeit
                        new LoadingScreen();
                        f.dispose();
                    }
                    // If user clicked No, window stays open (method returns without disposing)
                } else {
                    // All ships placed, allow normal close
                    new LoadingScreen();
                    f.dispose();
                }
            }
            public void windowOpened(WindowEvent e){}
            public void windowDeiconified(WindowEvent e){}
            public void windowActivated(WindowEvent e){}
            public void windowClosed(WindowEvent e){
                //write an if conditonal for if player 1 close screen without completing selection
                boolean allShipsPlaced = true;
                for(int i = 0; i<5; i++){
                    if(player.bt[i].gridCoord.isEmpty()){
                        allShipsPlaced = false;
                        break;
                    }
                }

                if(player.playerno==1){
                    if(!allShipsPlaced){
                        JOptionPane.showMessageDialog(f, "Player 1 has not completed ship placement. Player 2 wins by default. Returning to loading screen.", "Game Over", JOptionPane.PLAIN_MESSAGE);
                    }
                    else{
                    JOptionPane.showMessageDialog(f, "Your ships have been successfully placed. Please let "+GameMain.p2.name+" make their arrangement of ships", "Success", JOptionPane.PLAIN_MESSAGE);
                    }
                }
                else if(player.playerno==2){
                    if(!allShipsPlaced){
                        JOptionPane.showMessageDialog(f, "Player 2 has not completed ship placement. Player 1 wins by default. Returning to loading screen.", "Game Over", JOptionPane.PLAIN_MESSAGE);
                    }
                    else{
                        JOptionPane.showMessageDialog(f, "Your ships have been successfully placed. You can call "+GameMain.p1.name+" back to play. \n"+GameMain.p1.name+" starts first", "Success", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            }
            public void windowDeactivated(WindowEvent e){} 
            public void windowIconified(WindowEvent e){}

        });

        JLabel plab = new JLabel(player.name+" choosing", SwingConstants.CENTER);
        //set font and fontsize. eg.: plab.setFont(new Font("Serif", Font.PLAIN, 14));
        
        //player 1 text: .setBounds(320,100,100,50);
        this.add(plab);
        //player 2 text: .setBounds(1120,100,100,50);

        
        if(player.playerno==1){
            player.bgrid = new ButtonGrid(this, ScreenScaler.scaleX(200), ScreenScaler.scaleY(200), ScreenScaler.scaleX(600), ScreenScaler.scaleY(600), ScreenScaler.scaleX(40), ScreenScaler.scaleY(40), true);
            plab.setBounds(ScreenScaler.scaleX(200),ScreenScaler.scaleY(100),ScreenScaler.scaleX(400),ScreenScaler.scaleY(50));
        }
        else{
            player.bgrid = new ButtonGrid(this, ScreenScaler.scaleX(1000), ScreenScaler.scaleY(200), ScreenScaler.scaleX(1400), ScreenScaler.scaleY(600), ScreenScaler.scaleX(40), ScreenScaler.scaleY(40), true);
            //xt=1000, yt=200,xb=1400, yb=600, buttonLength = 40, buttonWidth = 40 for final layout
            plab.setBounds(ScreenScaler.scaleX(1000),ScreenScaler.scaleY(100),ScreenScaler.scaleX(400),ScreenScaler.scaleY(50));
        }


        
        //Buttons to add: Remove(from grid)

        JButton place = new JButton("Place on grid");
        place.setBounds(ScreenScaler.scaleX(720),ScreenScaler.scaleY(200),ScreenScaler.scaleX(120),ScreenScaler.scaleY(20));
        player.bgrid.no_of_ships_placed = 0;//in case user places more ships than allowed
        place.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //grid logic
                place.setBackground(Color.RED);//to show button is computing
                ArrayList<Integer[]> select_coord = new ArrayList<Integer[]>();
                int noClicked = 0;
                //run through the grid, store the no of selected buttons
                for(int i = 0; i<10; i++){
                    for(int j = 0; j<10; j++){
                        if(player.bgrid.grid[i][j].getBackground()==Color.GREEN){
                            Integer[] a = new Integer[2];
                            a[0] = i; a[1] = j;
                            select_coord.add(a); 
                            noClicked++;
                        }
                    }
                }
                Battleship btship = new Battleship(Ship.PATROL_BOAT);
                boolean switchCheck = false;
                //assign type to btship
                switch(noClicked){
                    case 0:
                    case 1:
                        //Error message
                        JOptionPane.showConfirmDialog(f, "Please check your selection", "Error", JOptionPane.PLAIN_MESSAGE);
                        //write switch case
                        switchCheck = false;//to check whether no. of squares selected is valid
                        //make all green buttons black
                        Iterator<Integer[]> it = select_coord.iterator();
                        while(it.hasNext()){
                            Integer coord[] = it.next();
                            player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                        }
                        //set selected coord to []
                        select_coord.clear();                    
                        break;
                    case 2:
                        btship.type = Ship.PATROL_BOAT;
                        switchCheck = true;
                        break;
                    case 3:
                        //Default: Submarine if 3 squares selected
                        if(player.bt[1].gridCoord.isEmpty()){
                            btship.type = Ship.SUBMARINE;
                        }
                        else{
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

                //perform diagonal check and in-line check. 
                //Overlap check not necessary; button wont be selected if already red
                boolean diagonalCheck = true;
                if(switchCheck){
                    Iterator<Integer[]> it = select_coord.iterator();//iterator for coordinates
                    Integer[] prev = it.next();
                    boolean horizontal = false;
                    int count = 0;
                    while(it.hasNext()){
                        Integer[] coord = it.next();
                        //compare
                        if(count==0){
                            if(prev[0]==coord[0] && (prev[1]==coord[1]+1 || prev[1]==coord[1]-1)){
                                horizontal = false;
                            }
                            else if(prev[1]==coord[1] && (prev[0]==coord[0]+1 || prev[0]==coord[0]-1)){
                                horizontal = true;
                            }
                            else{
                                diagonalCheck = false;
                                break;
                            }
                        }
                        //general comparison
                        if(prev[0]==coord[0] && (prev[1]==coord[1]+1 || prev[1]==coord[1]-1) && horizontal==false){
                            diagonalCheck = true;
                        }
                        else if(prev[1]==coord[1] && (prev[0]==coord[0]+1 || prev[0]==coord[0]-1)&&horizontal==true){
                            diagonalCheck = true;
                        }
                        else{
                            diagonalCheck = false;
                            break;
                        }
                        prev = coord;
                        count++;
                    }
                }

                    
                //Go to coordinates, change color to red if selectedCoord.length > 0, set btship's gridcoord
                Iterator<Integer[]> it1 = select_coord.iterator();//iterator for coordinates; used later also
                if(diagonalCheck && switchCheck){
                    if(player.bgrid.no_of_ships_placed + 1 > 5){
                        int ch = JOptionPane.showConfirmDialog(f, "You are trying to place more than 5 ships. Your latest entry will be cleared", "Error", JOptionPane.YES_NO_CANCEL_OPTION);
                        //actions for each choice
                        switch(ch){
                            case 0:
                                while(it1.hasNext()){
                                    Integer[] coord = it1.next();
                                    player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                                    btship.gridCoord.clear();
                                    btship.type = null;
                                }
                                select_coord.clear();
                                break;
                            case 1:
                            case 2:
                                //nothing happens, user can use remove button to remove ships which he wants to remove
                                break;
                        }

                    }
                    else{
                        while(it1.hasNext()){
                            Integer[] coord = it1.next();
                            //System.out.println(btship.type.getName());
                            player.bgrid.grid[coord[0]][coord[1]].setToolTipText(btship.type.getName());//when user hovers over selected ships, the name is displayed
                            player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.RED);
                            btship.gridCoord.add(coord);
                        }
                        player.bgrid.no_of_ships_placed++;
                    }
                }
                else{
                    //Set btship to null
                    btship.type = null;
                    //Error message
                    JOptionPane.showConfirmDialog(f, "Please check your selection", "Error", JOptionPane.PLAIN_MESSAGE);
                    //make all green buttons black
                    while(it1.hasNext()){
                        Integer coord[] = it1.next();
                        player.bgrid.grid[coord[0]][coord[1]].setBackground(Color.BLACK);
                    }
                    //set selected coord to []
                    select_coord.clear();                    
                        
                }
                //add btship to player
                for(int i = 0; i<5; i++){
                    if(player.bt[i].type==btship.type){
                        player.bt[i] = btship;
                    }
                }
                //For player 2, same code as player 1, with player 1 changed to player 2
                
                place.setBackground(null);//to show computation has finished(for extreme cases)
            }
        });
        this.add(place);


        //remove button code:
        /*JButton remove = new JButton("Remove");
        remove.setBounds(720,320,100,20);
        remove.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                //code for removing items
                
            }
        });
        this.add(remove);*/

        JButton clear_all = new JButton("Clear all");
        clear_all.setBounds(ScreenScaler.scaleX(720),ScreenScaler.scaleY(440),ScreenScaler.scaleX(100),ScreenScaler.scaleY(20));
        clear_all.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //code for clearing grid:
                player.bgrid.no_of_ships_placed = 0;
                for(int i = 0; i<10; i++){
                    for(int j = 0; j<10; j++){
                        player.bgrid.grid[i][j].setBackground(Color.BLACK);
                    }
                }
                for(int i = 0; i<5; i++){
                    player.bt[i].gridCoord.clear();
                }
                
            }
        });
        this.add(clear_all);
        
        JButton setGrid = new JButton("Done");
        setGrid.setBounds(720,700,100,20);
        this.add(setGrid);
                
        setGrid.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                //check length of grid before showing confirm message
                int res = JOptionPane.showConfirmDialog(f, "Confirm your grid?", "Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
                switch(res){
                    case 0:
                        //User pressed yes
                        //Check for if all ships are there
                        if(player.playerno==1){
                            boolean noCheck = false;
                            for(int i = 0; i<5; i++){
                                if(GameMain.p1.bt[i].gridCoord.isEmpty()){
                                    JOptionPane.showConfirmDialog(f, "All ships have not been placed. Please place all ships first", "Error", JOptionPane.PLAIN_MESSAGE);
                                    noCheck = false;
                                    break;
                                }
                                else{
                                    noCheck = true;
                                }
                            }
                            if(noCheck){
                                f.dispose();
                                new Player_ChoiceScreen(GameMain.p2);
                            }
                        }
                        else if(player.playerno==2){
                            new GameScreen();
                            f.dispose();
                        }
                        return;
                    case 1:
                        //User pressed no
                    default:
                        //User pressed cancel
                        break;
                }
            }
        });

        this.setVisible(true);
        
        
    }
}

