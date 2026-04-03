import javax.swing.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
public class LoadingScreen extends JFrame{
    public LoadingScreen(){
        this.setTitle("Battleship");
        this.setBounds(100,100,500,500);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.getContentPane().setBackground(Color.BLACK);

        JLabel l = new JLabel("Battleship", SwingConstants.CENTER);
        l.setFont(new Font("Blackadder ITC", Font.PLAIN, 50));//for font
        l.setForeground(Color.WHITE);
        l.setBounds(0,50,this.getWidth(),50);
        this.add(l);

        JButton play = new JButton("Play");
        play.setBounds(200,120,100,50);
        this.add(play);
        LoadingScreen f = this;
        play.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                new Player_ChoiceScreen(GameMain.p1);
                f.dispose();
            }
        });

        JButton rules = new JButton("Rules");
        rules.setBounds(200,190,100,50);
        this.add(rules);
        rules.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                JFrame rules_popup = new JFrame("Rules of the game");
                rules_popup.setBounds(800,100,515,600);//adjust height and width until the scrollbars show up
                rules_popup.setVisible(true);
                //rules_popup.setResizable(false);
                rules_popup.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                rules_popup.setLayout(null);
                rules_popup.getContentPane().setBackground(Color.BLACK);

                JLabel header  = new JLabel("Rules", SwingConstants.CENTER);//for center-aligned text
                header.setBounds(0,0, 500,60);
                header.setFont(new Font("Blackadder ITC", Font.BOLD, 50));
                header.setForeground(Color.WHITE);
                rules_popup.add(header);
                
                JTextArea t1 = new JTextArea();
                t1.setBounds(0,0, 1000, 500);
                t1.setBackground(Color.BLACK);
                t1.setForeground(Color.WHITE);
                //Rules text: 
                t1.setText("1. Battleship is a 2-player game\r\n" + //
                                        "2. Each player gets 5 ships\r\n" + //
                                        "3. the ships are of the following types:\r\n" + //
                                        "    (a) Patrol Boat (occupies 2 grid spaces)\r\n" + //
                                        "    (b) Submarine (occupies 3 grid spaces)\r\n" + //
                                        "    (c) Destroyer (occupies 3 grid spaces)\r\n" + //
                                        "    (d) Battleship (occupies 4 grid spaces)\r\n" + //
                                        "    (e) Carrier (occupies 5 grid spaces)\r\n" + //
                                        "4. Each player gets one ship of each kind to place on the grid.\r\n" + //
                                        "5. The ships are arranged on a 10x10 grid with columns labelled 1-10 and rows labelled A-J\r\n" + //
                                        "6. Each player has to keep his/her arrangement of ships secret from the other player\r\n" + //
                                        "7. After the grids are arranged, players take turns to shoot at the other player's grid\r\n" + //
                                        "8. Each player chooses one grid square to shoot at.\r\n" + //
                                        "9. If a ship is hit (ie. one of the grid squares which it occupies has been shot), then it is announced by the computer.\r\n" + //
                                        "10. If all the grid squares which a ship occupies is hit, then the ship sinks.\r\n" + //
                                        "11. The first player to have all their ships sunk loses the game. \r\n" + //
                                        "\r\n" + //
                                        "");
                t1.setFont(new Font("Times New Roman", Font.PLAIN, 20));
                t1.setEditable(false);
                JScrollPane p1 = new JScrollPane(t1, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                p1.setBounds(0,70,500, 490);
                p1.setBackground(Color.BLACK);
                p1.setForeground(Color.WHITE);
                rules_popup.add(p1);



        
            }
        });

        JButton tutorial = new JButton("Tutorial");
        tutorial.setBounds(200,260, 100, 50);
        this.add(tutorial);
        tutorial.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                new TutorialScreen();
            }
        });

    }
}
