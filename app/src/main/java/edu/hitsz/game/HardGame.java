package edu.hitsz.game;

import edu.hitsz.application.Game;
import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;

public class HardGame extends Game {

    public HardGame(String playerName) {
        super(playerName);
    }

    @Override
    protected void initializeDifficulty() {
        enemyMaxNumber = 7;
        enemySpawnCycle = 20;
        heroShootCycle = 18;
        enemyShootCycle = 18;
        bossScoreStep = 100;
        nextBossScoreThreshold = 100;
        enemyHpScale = 1.0;
        enemySpeedScale = 1.0;
        bossEnemyFactory.resetBossHpRule(140, 30);
    }

    @Override
    protected void increaseDifficulty(int stage) {
        enemyMaxNumber = 7;
        enemySpawnCycle = Math.max(8, 20 * Math.pow(0.93, stage));
        heroShootCycle = Math.max(10, 18 * Math.pow(0.97, stage));
        enemyShootCycle = Math.max(10, 18 * Math.pow(0.95, stage));
        enemyHpScale = 1.0 + 0.12 * stage;
        enemySpeedScale = 1.0 + 0.10 * stage;
        printDifficultyIncrease(String.format(
                "困难难度：敌机产生周期:%.2f，敌机速度倍率:%.2f，敌机血量倍率:%.2f，英雄机射击周期:%.2f，敌机射击周期:%.2f。",
                enemySpawnCycle,
                enemySpeedScale,
                enemyHpScale,
                heroShootCycle,
                enemyShootCycle
        ));
    }

    @Override
    protected AbstractEnemy createEnemy() {
        double rand = Math.random();
        if (rand < 0.50) {
            return mobEnemyFactory.createEnemy();
        }
        if (rand < 0.72) {
            return eliteEnemyFactory.createEnemy();
        }
        if (rand < 0.88) {
            return elitePlusEnemyFactory.createEnemy();
        }
        return eliteProEnemyFactory.createEnemy();
    }

    @Override
    protected boolean supportBoss() {
        return true;
    }

    @Override
    protected AbstractEnemy createBossEnemy() {
        nextBossScoreThreshold += bossScoreStep;
        return bossEnemyFactory.createEnemy();
    }
}
