package pl.adambaranowski.minesweeper.utils;

public class Player {
    private String name;
    private String sessionId;
    private int time;
    private int boardPercentage;

    public Player(String name, String sessionId, int time, int boardPercentage) {
        this.name = name;
        this.sessionId = sessionId;
        this.time = time;
        this.boardPercentage = boardPercentage;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getBoardPercentage() {
        return boardPercentage;
    }

    public void setBoardPercentage(int boardPercentage) {
        this.boardPercentage = boardPercentage;
    }
}
