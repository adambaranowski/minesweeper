package pl.adambaranowski.minesweeper.utils;

public class Room {
    private String name;
    private int maxPlayers;
    private int players;
    private int roomId;
    private String playersString;

    public Room(int roomId, String name, int maxPlayers, int players) {
        this.roomId = roomId;
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.players = players;
        playersString = players + "/" + maxPlayers;
    }

    public int getRoomId() {
        return roomId;
    }


    public int getMaxPlayers() {
        return maxPlayers;
    }


    public int getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public String getPlayersString() {
        return playersString;
    }
}
