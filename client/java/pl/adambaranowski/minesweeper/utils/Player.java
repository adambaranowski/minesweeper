package pl.adambaranowski.minesweeper.utils;

public class Player {
    private String name;
    private String sessionId;
    private double score;
    private double total;

    public Player(String name, String sessionId, double score, double total) {
        this.name = name;
        this.sessionId = sessionId;
        this.score = score;
        this.total = total;
    }

    public String getSessionId() {
        return sessionId;
    }

    public double getScore() {
        return score;
    }

    public double getTotal() {
        return total;
    }

    public String getName() {
        return name;
    }
}
