import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
public class Exp extends JFrame{
    public Exp(){
        ScreenScaler.initialize();
        this.setBounds(ScreenScaler.scaleX(0),ScreenScaler.scaleY(0),ScreenScaler.scaleX(500),ScreenScaler.scaleY(500));
        this.setLayout(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JFrame f = this;

        JPanel p1 = new JPanel();
        p1.setBounds(ScreenScaler.scaleX(0),ScreenScaler.scaleY(0),ScreenScaler.scaleX(100),ScreenScaler.scaleY(100));
        p1.setToolTipText("This is a button");
        p1.setBackground(Color.BLUE);
        this.add(p1);

        Point coord_og = new Point(0, 0);

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                p1.setBounds(e.getX(), e.getY(), ScreenScaler.scaleX(100), ScreenScaler.scaleY(100));
                f.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                
                coord_og.x = p1.getX();
                coord_og.y = p1.getY();
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
            
        });

        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e){
                //think over hoe to make it move if only area in panel is clicked
                int dx = e.getX() - coord_og.x;
                int dy = e.getY() - coord_og.y;
                p1.setBounds(coord_og.x + dx, coord_og.y+dy, ScreenScaler.scaleX(100), ScreenScaler.scaleY(100));
                f.repaint();
            }
        });

        
    }
    public static void main(String args[]){
        new Exp();
    }
}