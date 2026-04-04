import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class TutorialScreen extends JFrame {
    TutorialScreen() {
        this.setTitle("Tutorial");
        this.setResizable(true);
        this.setSize(980, 720);
        this.setMinimumSize(new Dimension(820, 620));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 12));

        JLabel header = new JLabel("Tutorial", SwingConstants.CENTER);
        header.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        this.add(header, BorderLayout.NORTH);

        JTextArea content = new JTextArea();
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setText(
                "Welcome to Battleship.\n\n" +
                "1. Place all five ships on the grid.\n" +
                "2. Ships must be straight and contiguous.\n" +
                "3. Confirm placement, then start the match.\n" +
                "4. On your turn, select exactly one square and press Shoot.\n" +
                "5. Sink all opponent ships to win.\n\n" +
                "Tip: In online mode, host moves first.");

        JScrollPane scrollPane = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        this.add(scrollPane, BorderLayout.CENTER);

        this.setVisible(true);
    }
}
