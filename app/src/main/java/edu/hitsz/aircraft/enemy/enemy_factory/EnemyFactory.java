package edu.hitsz.aircraft.enemy.enemy_factory;

import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;

public abstract class EnemyFactory {

    /**
     * 难度递进相关倍率：由游戏主循环按时间更新并下发到工厂。
     * 仅影响新生成的敌机（已在场的敌机不回溯修改）。
     */
    private double enemyHpScale = 1.0;
    private double enemySpeedXScale = 1.0;
    private double enemySpeedYScale = 1.0;

    /**
     * 工厂方法：由具体敌机工厂实现具体创建逻辑。
     */
    public abstract AbstractEnemy createEnemy();

    public void setEnemyHpScale(double enemyHpScale) {
        if (Double.isNaN(enemyHpScale) || Double.isInfinite(enemyHpScale)) {
            return;
        }
        this.enemyHpScale = Math.max(0.1, enemyHpScale);
    }

    public void setEnemySpeedXScale(double enemySpeedXScale) {
        if (Double.isNaN(enemySpeedXScale) || Double.isInfinite(enemySpeedXScale)) {
            return;
        }
        this.enemySpeedXScale = Math.max(0.1, enemySpeedXScale);
    }

    public void setEnemySpeedYScale(double enemySpeedYScale) {
        if (Double.isNaN(enemySpeedYScale) || Double.isInfinite(enemySpeedYScale)) {
            return;
        }
        this.enemySpeedYScale = Math.max(0.1, enemySpeedYScale);
    }

    protected int scaleHp(int baseHp) {
        int hp = (int) Math.round(baseHp * enemyHpScale);
        return Math.max(1, hp);
    }

    protected int scaleSpeedX(int baseSpeedX) {
        int sx = (int) Math.round(baseSpeedX * enemySpeedXScale);
        if (sx == 0 && baseSpeedX != 0) {
            sx = baseSpeedX > 0 ? 1 : -1;
        }
        return sx;
    }

    protected int scaleSpeedY(int baseSpeedY) {
        int sy = (int) Math.round(baseSpeedY * enemySpeedYScale);
        if (sy == 0 && baseSpeedY != 0) {
            sy = baseSpeedY > 0 ? 1 : -1;
        }
        return sy;
    }

    /**
     * 随机生成横坐标，确保飞机完全出现在屏幕内。
     */
    protected int randomX(int imageWidth) {
        return (int) (Math.random() * (Main.WINDOW_WIDTH - imageWidth));
    }

    /**
     * 敌机在屏幕上方小范围随机出现。
     */
    protected int randomY() {
        return (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05);
    }

    /**
     * 统一提供普通敌机尺寸，便于复用横坐标边界计算。
     */
    protected int defaultEnemyWidth() {
        return ImageManager.MOB_ENEMY_IMAGE.getWidth();
    }
}
