import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class SquareBoardPanel extends JPanel {
    private static final int DEFAULT_PREFERRED_SIDE = 520;

    private final JComponent board;
    private final int minSide;
    private final int maxSide;

    public SquareBoardPanel(JComponent board, int minSide, int maxSide) {
        super(null);
        this.board = board;
        this.minSide = minSide;
        this.maxSide = maxSide;

        if (this.board.getParent() != this) {
            add(this.board);
        }
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();
        int availableWidth = getWidth() - insets.left - insets.right;
        int availableHeight = getHeight() - insets.top - insets.bottom;

        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        int side = Math.min(availableWidth, availableHeight);
        side = Math.max(minSide, side);
        side = Math.min(maxSide, side);
        side = Math.min(side, Math.min(availableWidth, availableHeight));

        int x = insets.left + (availableWidth - side) / 2;
        int y = insets.top + (availableHeight - side) / 2;
        board.setBounds(x, y, side, side);
    }

    @Override
    public Dimension getPreferredSize() {
        int side = Math.max(minSide, Math.min(DEFAULT_PREFERRED_SIDE, maxSide));
        return new Dimension(side, side);
    }
}
