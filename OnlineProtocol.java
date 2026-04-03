import java.awt.Color;

public final class OnlineProtocol {
    private OnlineProtocol() {
    }

    public static String serializePlacement(Player player) {
        StringBuilder builder = new StringBuilder("PLACEMENT");
        for (int i = 0; i < player.bt.length; i++) {
            Battleship ship = player.bt[i];
            if (ship == null || ship.type == null) {
                continue;
            }

            builder.append("|").append(ship.type.name()).append(":");
            for (int j = 0; j < ship.gridCoord.size(); j++) {
                Integer[] coord = ship.gridCoord.get(j);
                if (j > 0) {
                    builder.append(";");
                }
                builder.append(coord[0]).append(",").append(coord[1]);
            }
        }

        return builder.toString();
    }

    public static void applyPlacement(Player player, String payload) {
        resetPlayer(player);
        if (payload == null || !payload.startsWith("PLACEMENT")) {
            return;
        }

        String[] sections = payload.split("\\|");
        for (int i = 1; i < sections.length; i++) {
            String section = sections[i];
            if (section == null || section.trim().isEmpty()) {
                continue;
            }

            String[] parts = section.split(":");
            if (parts.length != 2) {
                continue;
            }

            Ship shipType;
            try {
                shipType = Ship.valueOf(parts[0].trim());
            } catch (IllegalArgumentException ex) {
                continue;
            }

            Battleship target = findShip(player, shipType);
            if (target == null) {
                continue;
            }

            target.type = shipType;
            target.gridCoord.clear();
            if (!parts[1].trim().isEmpty()) {
                String[] coords = parts[1].split(";");
                for (String coordText : coords) {
                    int[] coord = parseCoordinate(coordText.trim());
                    if (coord != null) {
                        Integer[] stored = new Integer[2];
                        stored[0] = coord[0];
                        stored[1] = coord[1];
                        target.gridCoord.add(stored);
                    }
                }
            }
        }
    }

    public static String serializeShot(int row, int col) {
        return "SHOT|" + row + "," + col;
    }

    public static String serializeMatchEnd() {
        return "MATCH_END";
    }

    public static boolean isMatchEnd(String message) {
        return "MATCH_END".equalsIgnoreCase(message == null ? null : message.trim());
    }

    public static ShotMessage parseShot(String message) {
        if (message == null || !message.startsWith("SHOT|")) {
            return null;
        }

        String[] parts = message.split("\\|");
        if (parts.length != 2) {
            return null;
        }

        int[] coord = parseCoordinate(parts[1]);
        if (coord == null) {
            return null;
        }

        return new ShotMessage(coord[0], coord[1]);
    }

    public static String serializeShotResult(ShotResult result) {
        StringBuilder builder = new StringBuilder("SHOT_RESULT|");
        builder.append(result.row).append(",").append(result.col);
        builder.append("|").append(result.hit ? "HIT" : "MISS");
        builder.append("|").append(result.sunkShipName == null ? "NONE" : result.sunkShipName);
        builder.append("|").append(result.gameOver ? "WIN" : "CONTINUE");
        return builder.toString();
    }

    public static String serializeIncomingAttack(ShotResult result) {
        StringBuilder builder = new StringBuilder("INCOMING_ATTACK|");
        builder.append(result.row).append(",").append(result.col);
        builder.append("|").append(result.hit ? "HIT" : "MISS");
        builder.append("|").append(result.sunkShipName == null ? "NONE" : result.sunkShipName);
        builder.append("|").append(result.gameOver ? "WIN" : "CONTINUE");
        return builder.toString();
    }

    public static ShotResult parseShotResult(String message) {
        if (message == null || !message.startsWith("SHOT_RESULT|")) {
            return null;
        }

        String[] parts = message.split("\\|");
        if (parts.length != 5) {
            return null;
        }

        int[] coord = parseCoordinate(parts[1]);
        if (coord == null) {
            return null;
        }

        boolean hit = "HIT".equalsIgnoreCase(parts[2]);
        String sunkShipName = "NONE".equalsIgnoreCase(parts[3]) ? null : parts[3].trim();
        boolean gameOver = "WIN".equalsIgnoreCase(parts[4]);
        return new ShotResult(coord[0], coord[1], hit, sunkShipName, gameOver);
    }

    public static ShotResult parseIncomingAttack(String message) {
        if (message == null || !message.startsWith("INCOMING_ATTACK|")) {
            return null;
        }

        String[] parts = message.split("\\|");
        if (parts.length != 5) {
            return null;
        }

        int[] coord = parseCoordinate(parts[1]);
        if (coord == null) {
            return null;
        }

        boolean hit = "HIT".equalsIgnoreCase(parts[2]);
        String sunkShipName = "NONE".equalsIgnoreCase(parts[3]) ? null : parts[3].trim();
        boolean gameOver = "WIN".equalsIgnoreCase(parts[4]);
        return new ShotResult(coord[0], coord[1], hit, sunkShipName, gameOver);
    }

    public static ShotResult resolveShot(Player defender, int row, int col) {
        boolean hit = false;
        String sunkShipName = null;

        for (int i = 0; i < defender.bt.length; i++) {
            Battleship ship = defender.bt[i];
            if (ship == null || ship.type == null) {
                continue;
            }

            for (int j = 0; j < ship.gridCoord.size(); j++) {
                Integer[] coord = ship.gridCoord.get(j);
                if (coord[0] == row && coord[1] == col) {
                    hit = true;
                    ship.gridCoord.remove(j);
                    if (ship.gridCoord.isEmpty()) {
                        defender.ships_lost++;
                        sunkShipName = ship.type.getName();
                    }
                    break;
                }
            }

            if (hit) {
                break;
            }
        }

        return new ShotResult(row, col, hit, sunkShipName, defender.ships_lost >= 5);
    }

    public static void paintShot(ButtonGrid grid, int row, int col, boolean hit) {
        if (grid == null) {
            return;
        }

        grid.grid[row][col].setBackground(hit ? Color.WHITE : Color.DARK_GRAY);
        grid.grid[row][col].setForeground(Color.RED);
        if (hit) {
            grid.grid[row][col].setText("X");
        }
    }

    public static void paintPlacement(ButtonGrid grid, Player player) {
        if (grid == null || player == null) {
            return;
        }

        for (int i = 0; i < player.bt.length; i++) {
            Battleship ship = player.bt[i];
            if (ship == null || ship.type == null) {
                continue;
            }

            for (int j = 0; j < ship.gridCoord.size(); j++) {
                Integer[] coord = ship.gridCoord.get(j);
                grid.grid[coord[0]][coord[1]].setBackground(Color.RED);
            }
        }
    }

    private static void resetPlayer(Player player) {
        if (player == null) {
            return;
        }

        player.ships_lost = 0;
        for (int i = 0; i < player.bt.length; i++) {
            Battleship ship = player.bt[i];
            if (ship != null) {
                ship.gridCoord.clear();
            }
        }
    }

    private static Battleship findShip(Player player, Ship shipType) {
        for (int i = 0; i < player.bt.length; i++) {
            Battleship ship = player.bt[i];
            if (ship != null && ship.type == shipType) {
                return ship;
            }
        }

        return null;
    }

    private static int[] parseCoordinate(String coordText) {
        String[] parts = coordText.split(",");
        if (parts.length != 2) {
            return null;
        }

        try {
            int row = Integer.parseInt(parts[0].trim());
            int col = Integer.parseInt(parts[1].trim());
            return new int[] {row, col};
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static final class ShotMessage {
        public final int row;
        public final int col;

        public ShotMessage(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    public static final class ShotResult {
        public final int row;
        public final int col;
        public final boolean hit;
        public final String sunkShipName;
        public final boolean gameOver;

        public ShotResult(int row, int col, boolean hit, String sunkShipName, boolean gameOver) {
            this.row = row;
            this.col = col;
            this.hit = hit;
            this.sunkShipName = sunkShipName;
            this.gameOver = gameOver;
        }
    }
}