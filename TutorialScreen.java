import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class TutorialScreen extends JFrame {
    TutorialScreen() {
        this.setTitle("Tutorial");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout(0, 12));
    this.getContentPane().setBackground(Color.BLACK);

        JLabel header = new JLabel("Tutorial", SwingConstants.CENTER);
    header.setFont(new Font("Blackadder ITC", Font.BOLD, 50));
    header.setForeground(Color.WHITE);
    header.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        this.add(header, BorderLayout.NORTH);

    JTextArea content = new JTextArea();
    content.setBackground(Color.BLACK);
    content.setForeground(Color.WHITE);
    content.setFont(new Font("Times New Roman", Font.PLAIN, 20));
    content.setEditable(false);
    content.setLineWrap(true);
    content.setWrapStyleWord(true);
    content.setMargin(new Insets(6, 6, 6, 6));
    content.setText(
        "BATTLESHIP TUTORIAL\n\n" +
        "A) SHIP PLACEMENT\n" +
        "1. Each player must place exactly 5 ships on a 10x10 board.\n" +
        "2. Ships and lengths:\n" +
        "   - Patrol Boat: 2 squares\n" +
        "   - Submarine: 3 squares\n" +
        "   - Destroyer: 3 squares\n" +
        "   - Battleship: 4 squares\n" +
        "   - Carrier: 5 squares\n" +
        "3. Click board squares to select a ship shape.\n" +
        "4. Selected squares must be straight and contiguous:\n" +
        "   - Horizontal or vertical only\n" +
        "   - No diagonal ship shapes\n" +
        "5. Press Place on grid to confirm the selected ship.\n" +
        "6. Press Clear all to reset placement and start over.\n" +
        "7. Press Done only after all 5 ships are placed.\n\n" +
        "B) SHOOTING PHASE\n" +
        "1. Players take turns shooting at the opponent's board.\n" +
        "2. Select exactly one square and press Shoot.\n" +
        "3. Hit result:\n" +
        "   - A successful hit is marked on the board.\n" +
        "   - When all squares of a ship are hit, that ship sinks.\n" +
        "4. You cannot shoot multiple squares in one turn.\n" +
        "5. First player to sink all enemy ships wins.\n\n" +
        "C) LOCAL GAME FLOW\n" +
        "1. From the main menu, choose Play.\n" +
        "2. Player 1 places ships and presses Done.\n" +
        "3. Player 2 places ships and presses Done.\n" +
        "4. Battle screen opens and turns begin.\n\n" +
        "D) ONLINE GAME FLOW\n" +
        "1. From main menu, choose Play Online.\n" +
        "2. Host flow:\n" +
        "   - Choose Host a match\n" +
        "   - Share room code / endpoint shown by the game\n" +
        "3. Join flow:\n" +
        "   - Choose Join a match\n" +
        "   - Enter host details and room code\n" +
        "4. Both players place ships and press Done.\n" +
        "5. Game syncs placements and opens online battle screen.\n" +
        "6. Host gets the first turn.\n\n" +
        "E) FORFEIT / EXIT\n" +
        "1. Closing during placement asks for confirmation.\n" +
        "2. Closing during online match sends match-end / forfeit.\n" +
        "3. Use confirmations to avoid accidental exits.\n");

        JScrollPane scrollPane = new JScrollPane(
                content,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
    scrollPane.setBackground(Color.BLACK);
    scrollPane.getViewport().setBackground(Color.BLACK);
    scrollPane.getVerticalScrollBar().setUnitIncrement(18);
        this.add(scrollPane, BorderLayout.CENTER);

    this.setSize(620, 720);
    this.setMinimumSize(new Dimension(480, 520));
    this.setLocationRelativeTo(null);
    this.setResizable(true);
        this.setVisible(true);
    }
}
