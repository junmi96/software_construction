package edu.hitsz.application;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.enemy.enemy_factory.BossEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.EliteEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.ElitePlusEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.EliteProEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.EnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.MobEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.aircraft.enemy.enemy_product.BossEnemy;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.item.BaseItem;
import edu.hitsz.rank.Rank;
import edu.hitsz.rank.RankDao;
import edu.hitsz.rank.RankDaoImpl;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 游戏模板父类：在父类中定义主循环骨架，
 * 不同难度通过重写具体步骤和钩子方法实现差异化逻辑。
 */
public abstract class Game extends JPanel {

    private final AudioManager audioManager = AudioManager.getInstance();

    private int backGroundTop = 0;

    private final int timeInterval = 40;
    private final Timer timer;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseItem> items;

    protected int enemyMaxNumber = 5;
    protected double enemySpawnCycle = 20;
    protected double heroShootCycle = 20;
    protected double enemyShootCycle = 20;
    protected int bossScoreStep = 100;
    protected int nextBossScoreThreshold = 100;
    protected int difficultyUpgradeCycle = 600;
    protected double enemyHpScale = 1.0;
    protected double enemySpeedScale = 1.0;

    private int enemySpawnCounter = 0;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;
    private int difficultyStage = 0;
    private int difficultyCounter = 0;
    private int elapsedTimeMs = 0;

    private int score = 0;
    private boolean gameOverFlag = false;
    private boolean rankSavedFlag = false;
    private boolean bossBgmPlaying = false;

    private final RankDao rankDao = new RankDaoImpl();
    private final String defaultPlayerName;
    private Runnable onGameOver;

    protected final EnemyFactory mobEnemyFactory = new MobEnemyFactory();
    protected final EnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    protected final EnemyFactory elitePlusEnemyFactory = new ElitePlusEnemyFactory();
    protected final EnemyFactory eliteProEnemyFactory = new EliteProEnemyFactory();
    protected final BossEnemyFactory bossEnemyFactory = new BossEnemyFactory();

    protected Game(String playerName) {
        this.heroAircraft = HeroAircraft.getHeroAircraft();
        this.enemyAircrafts = new LinkedList<>();
        this.heroBullets = new LinkedList<>();
        this.enemyBullets = new LinkedList<>();
        this.items = new LinkedList<>();
        this.defaultPlayerName = (playerName == null || playerName.trim().isEmpty()) ? "Player" : playerName.trim();
        new HeroController(this, heroAircraft);
        this.timer = new Timer("game-action-timer", true);
    }

    /**
     * 模板方法：定义游戏主循环固定骨架。
     */
    public final void action() {
        prepareGame();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (gameOverFlag) {
                    return;
                }

                updateDifficulty();
                spawnEnemyAction();
                aircraftsMoveAction();
                bulletsMoveAction();
                itemsMoveAction();
                shootAction();
                crashCheckAction();
                postProcessAction();
                repaint();
                elapsedTimeMs += timeInterval;
                checkResultAction();
            }
        };
        timer.schedule(task, 0, timeInterval);
    }

    /**
     * 抽象步骤：初始化当前难度的初始参数。
     */
    protected abstract void initializeDifficulty();

    /**
     * 抽象步骤：随着游戏推进，更新当前难度的参数。
     */
    protected abstract void increaseDifficulty(int stage);

    /**
     * 抽象步骤：由子类决定如何生成普通敌机。
     */
    protected abstract AbstractEnemy createEnemy();

    /**
     * 钩子方法：是否支持 Boss。
     */
    protected boolean supportBoss() {
        return false;
    }

    /**
     * 钩子方法：由支持 Boss 的难度决定如何生成 Boss。
     */
    protected AbstractEnemy createBossEnemy() {
        return null;
    }

    /**
     * 钩子方法：是否启用英雄机无敌模式。
     */
    protected boolean isHeroInvincible() {
        return false;
    }

    /**
     * 钩子方法：返回游戏最长时长（毫秒），小于等于 0 表示不限时。
     */
    protected int getMaxGameDurationMs() {
        return -1;
    }

    protected final void printDifficultyInfo(String difficultyName, int stage) {
        System.out.printf("[%s] difficulty stage -> %d%n", difficultyName, stage);
    }

    protected final void printDifficultyIncrease(String message) {
        System.out.println(message);
    }

    protected final int getScore() {
        return score;
    }

    protected final void applyEnemyFactoryScales() {
        EnemyFactory[] factories = {
                mobEnemyFactory, eliteEnemyFactory, elitePlusEnemyFactory, eliteProEnemyFactory, bossEnemyFactory
        };
        for (EnemyFactory factory : factories) {
            factory.setEnemyHpScale(enemyHpScale);
            factory.setEnemySpeedXScale(enemySpeedScale);
            factory.setEnemySpeedYScale(enemySpeedScale);
        }
    }

    public void setOnGameOver(Runnable onGameOver) {
        this.onGameOver = onGameOver;
    }

    public int getCurrentScore() {
        return score;
    }

    public String getDefaultPlayerName() {
        return defaultPlayerName;
    }

    public synchronized void saveCurrentRank(String playerName, String mode) {
        if (rankSavedFlag) {
            return;
        }
        String finalPlayerName = playerName == null ? "" : playerName.trim();
        if (finalPlayerName.isEmpty()) {
            finalPlayerName = defaultPlayerName;
        }
        rankSavedFlag = true;
        rankDao.saveRank(mode, new Rank(0, finalPlayerName, score, System.currentTimeMillis()));
    }

    public void stopGame() {
        if (gameOverFlag) {
            audioManager.stopAll();
            return;
        }
        gameOverFlag = true;
        timer.cancel();
        audioManager.stopAll();
    }

    private void prepareGame() {
        audioManager.stopAll();
        resetRuntimeState();
        resetHeroAircraft();
        initializeDifficulty();
        applyEnemyFactoryScales();
        audioManager.playNormalBgm();
    }

    private void resetRuntimeState() {
        enemyAircrafts.clear();
        heroBullets.clear();
        enemyBullets.clear();
        items.clear();

        backGroundTop = 0;
        enemySpawnCounter = 0;
        heroShootCounter = 0;
        enemyShootCounter = 0;
        difficultyCounter = 0;
        difficultyStage = 0;
        elapsedTimeMs = 0;

        score = 0;
        gameOverFlag = false;
        rankSavedFlag = false;
        bossBgmPlaying = false;

        enemyMaxNumber = 5;
        enemySpawnCycle = 20;
        heroShootCycle = 20;
        enemyShootCycle = 20;
        bossScoreStep = 100;
        nextBossScoreThreshold = 100;
        difficultyUpgradeCycle = 600;
        enemyHpScale = 1.0;
        enemySpeedScale = 1.0;
        bossEnemyFactory.resetBossHpRule(100, 0);
    }

    private void resetHeroAircraft() {
        heroAircraft.setLocation(Main.WINDOW_WIDTH / 2.0,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight());
        heroAircraft.resetForNewGame(100);
    }

    private void updateDifficulty() {
        difficultyCounter++;
        if (difficultyCounter < difficultyUpgradeCycle) {
            return;
        }
        difficultyCounter = 0;
        difficultyStage++;
        increaseDifficulty(difficultyStage);
        applyEnemyFactoryScales();
    }

    private void spawnEnemyAction() {
        enemySpawnCounter++;
        if (enemySpawnCounter < enemySpawnCycle || enemyAircrafts.size() >= enemyMaxNumber) {
            return;
        }
        enemySpawnCounter = 0;

        AbstractEnemy enemy;
        if (supportBoss() && getScore() >= nextBossScoreThreshold && !isBossAlive()) {
            enemy = createBossEnemy();
        } else {
            enemy = createEnemy();
        }

        if (enemy != null) {
            enemyAircrafts.add(enemy);
            if (enemy instanceof BossEnemy) {
                bossBgmPlaying = true;
                audioManager.playBossBgm();
            }
        }
    }

    private boolean isBossAlive() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            if (enemyAircraft instanceof BossEnemy && !enemyAircraft.notValid()) {
                return true;
            }
        }
        return false;
    }

    private void shootAction() {
        heroAircraft.updateFireMode();

        heroShootCounter++;
        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }

        enemyShootCounter++;
        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            for (AbstractAircraft enemy : enemyAircrafts) {
                enemyBullets.addAll(enemy.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void itemsMoveAction() {
        for (BaseItem item : items) {
            item.forward();
        }
    }

    private void crashCheckAction() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                if (!isHeroInvincible()) {
                    heroAircraft.decreaseHp(bullet.getPower());
                }
                bullet.vanish();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    audioManager.playHit();
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid() && enemyAircraft instanceof AbstractEnemy) {
                        AbstractEnemy enemy = (AbstractEnemy) enemyAircraft;
                        score += enemy.getScore();
                        items.addAll(enemy.dropItems());
                    }
                }
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    if (!isHeroInvincible()) {
                        heroAircraft.decreaseHp(Integer.MAX_VALUE);
                    }
                }
            }
        }

        for (BaseItem item : items) {
            if (item.notValid()) {
                continue;
            }
            if (heroAircraft.crash(item)) {
                audioManager.playGetSupply();
                item.activate(heroAircraft, enemyAircrafts, enemyBullets);
                item.vanish();
            }
        }
    }

    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        items.removeIf(AbstractFlyingObject::notValid);
        updateBgmState();
    }

    private void updateBgmState() {
        if (!supportBoss()) {
            return;
        }
        if (bossBgmPlaying && !isBossAlive()) {
            bossBgmPlaying = false;
            audioManager.playNormalBgm();
        }
    }

    private void checkResultAction() {
        if (heroAircraft.getHp() > 0 && !isTimeUp()) {
            return;
        }

        gameOverFlag = true;
        timer.cancel();
        audioManager.playGameOver();
        if (onGameOver != null) {
            SwingUtilities.invokeLater(onGameOver);
        }
        System.out.println("Game Over!");
    }

    private boolean isTimeUp() {
        int maxDurationMs = getMaxGameDurationMs();
        return maxDurationMs > 0 && elapsedTimeMs >= maxDurationMs;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, backGroundTop, null);
        backGroundTop += 1;
        if (backGroundTop == Main.WINDOW_HEIGHT) {
            backGroundTop = 0;
        }

        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, items);

        g.drawImage(ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        paintScoreAndLife(g);
    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image!";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE: " + score, x, y);
        y += 20;
        g.drawString("LIFE: " + heroAircraft.getHp(), x, y);
    }
}
