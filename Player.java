public class Player {
    Battleship[] bt = new Battleship[5];
    final int playerno;
    ButtonGrid bgrid;
    ButtonGrid dg;//for displaying/shooting
    int ships_lost;
    Player(int pl){
        bt[0] = new Battleship(Ship.PATROL_BOAT);
        bt[1] = new Battleship(Ship.SUBMARINE);
        bt[2] = new Battleship(Ship.DESTROYER);
        bt[3] = new Battleship(Ship.BATTLESHIP);
        bt[4] = new Battleship(Ship.CARRIER);
        this.playerno = pl;
    }
}
