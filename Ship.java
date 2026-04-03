public enum Ship {
    PATROL_BOAT(2, "Patrol Boat"),
    SUBMARINE(3, "Submarine"),
    DESTROYER(3, "Destroyer"),
    BATTLESHIP(4, "Battleship"),
    CARRIER(5, "Aircraft Carrier");
    private final int gridSpace;
    private final String shipName;
    Ship(int i, String name){
        this.gridSpace = i;
        this.shipName = name;
    }
    public int getGridSpace(){
        return this.gridSpace;
    }
    public String getName(){
        return this.shipName;
    }
    
}
