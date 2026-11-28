package edu.hitsz.rank;

public class Rank {
    private int rank;
    private String name;
    private int score;
    private long timestamp;

    private final long id;

    public Rank(int rank, String name, int score, long timestamp) {
        this(0L, rank, name, score, timestamp);
    }

    public Rank(long id, int rank, String name, int score, long timestamp) {
        this.id = id;
        this.rank = rank;
        this.name = name;
        this.score = score;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
