import java.awt.Toolkit;

import javax.swing.JFrame;

public class TutorialScreen extends JFrame{
    TutorialScreen(){
        this.setTitle("Tutorial");
        this.setVisible(true);
        this.setResizable(true);
        this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(null);

        //JFrame f = this;//used whenever we want to refer to current frame in ActionListener



    }
}
