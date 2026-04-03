import javax.swing.JOptionPane;

public class GameMain{
    static Player p1;
    static Player p2;
    private static final String REMOTE_PLAYER_FALLBACK = "Remote Player";

    public static void main(String[] args){
        String p1Name = promptForName("Enter Player 1 username:", "Player 1");
        p1 = new Player(1, p1Name);
        p2 = new Player(2, REMOTE_PLAYER_FALLBACK);
        new LoadingScreen();
        //new GameScreen();//temporarily, for testing
    }

    public static boolean ensureLocalSecondPlayerName() {
        String current = (p2 == null || p2.name == null || p2.name.trim().isEmpty()) ? "Player 2" : p2.name;
        String input = JOptionPane.showInputDialog(null,
                "Enter Player 2 username:",
                current.equals(REMOTE_PLAYER_FALLBACK) ? "Player 2" : current);

        if (input == null) {
            return false;
        }

        String trimmed = input.trim();
        p2.name = trimmed.isEmpty() ? "Player 2" : trimmed;
        return true;
    }

    private static String promptForName(String prompt, String fallback) {
        String input = JOptionPane.showInputDialog(null, prompt, "Player Setup", JOptionPane.QUESTION_MESSAGE);
        if (input == null) {
            return fallback;
        }

        String trimmed = input.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }
}