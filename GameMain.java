public class GameMain{
    static Player p1;
    static Player p2;
    public static void main(String[] args){
        p1 = new Player(1);
        p2 = new Player(2);
        new LoadingScreen();
        //new GameScreen();//temporarily, for testing
    }
}