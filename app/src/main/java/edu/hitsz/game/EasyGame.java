package edu.hitsz.game;

import edu.hitsz.application.Game;
import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;

public class EasyGame extends Game {

    public EasyGame(String playerName) {
        super(playerName);
    }

    @Override
    protected void initializeDifficulty() {
        enemyMaxNumber = 5;
        enemySpawnCycle = 24;
        heroShootCycle = 20;
        enemyShootCycle = 22;
        enemyHpScale = 1.0;
        enemySpeedScale = 1.0;
    }

    @Override
    protected void increaseDifficulty(int stage) {
        enemyMaxNumber = 5;
        enemySpawnCycle = 24;
        heroShootCycle = 20;
        enemyShootCycle = 22;
        enemyHpScale = 1.0;
        enemySpeedScale = 1.0;
        printDifficultyIncrease("简单难度：难度不随游戏时间变化，保持初始设定。");
    }

    @Override
    protected AbstractEnemy createEnemy() {
        double rand = Math.random();
        if (rand < 0.70) {
            return mobEnemyFactory.createEnemy();
        }
        if (rand < 0.90) {
            return eliteEnemyFactory.createEnemy();
        }
        if (rand < 0.98) {
            return elitePlusEnemyFactory.createEnemy();
        }
        return eliteProEnemyFactory.createEnemy();
    }
}
