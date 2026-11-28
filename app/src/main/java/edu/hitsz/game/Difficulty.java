package edu.hitsz.game;

public enum Difficulty {
    EASY(28, 4, 0.8, 0.7, 300),
    NORMAL(22, 6, 1.0, 0.9, 200),
    HARD(16, 8, 1.4, 1.1, 150);

    public final int spawnInterval, maxEnemies, bossScore;
    public final double hpScale, speedScale;

    Difficulty(int interval, int count, double hp, double speed, int bossScore) {
        this.spawnInterval = interval;
        this.maxEnemies = count;
        this.hpScale = hp;
        this.speedScale = speed;
        this.bossScore = bossScore;
    }

    public static Difficulty fromName(String name) {
        for (Difficulty value : values()) {
            if (value.name().equals(name)) return value;
        }
        return EASY;
    }
}
