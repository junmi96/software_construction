package edu.hitsz.game;

public enum Difficulty {
    EASY(24, 5, 1.0, 1.0, 100),
    NORMAL(22, 6, 1.0, 1.0, 100),
    HARD(20, 7, 1.0, 1.0, 100);

    public final int spawnInterval, maxEnemies, bossScore;
    public final double hpScale, speedScale;

    Difficulty(int interval, int count, double hp, double speed, int bossScore) {
        this.spawnInterval = interval;
        this.maxEnemies = count;
        this.hpScale = hp;
        this.speedScale = speed;
        this.bossScore = bossScore;
    }

    public String rankMode() {
        switch (this) {
            case NORMAL: return "medium";
            case HARD: return "hard";
            default: return "easy";
        }
    }

    public static Difficulty fromName(String name) {
        for (Difficulty value : values()) {
            if (value.name().equals(name)) return value;
        }
        return EASY;
    }
}
