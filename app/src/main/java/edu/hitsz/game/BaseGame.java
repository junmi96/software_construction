package edu.hitsz.game;

import android.content.Context;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.enemy.enemy_factory.BossEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.EnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.ElitePlusEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.EliteProEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_product.BossEnemy;
import edu.hitsz.item.BaseItem;
import edu.hitsz.item.BombItem;
import edu.hitsz.aircraftwar2026.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;

import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.aircraft.enemy.enemy_factory.EliteEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_factory.MobEnemyFactory;
import edu.hitsz.aircraft.enemy.enemy_product.AbstractEnemy;
import edu.hitsz.application.ImageManager;
import static edu.hitsz.game.GameConfig.GAME_HEIGHT;
import static edu.hitsz.game.GameConfig.GAME_WIDTH;
public abstract class BaseGame extends SurfaceView
        implements SurfaceHolder.Callback, Runnable {

    private static final int BACKGROUND_STEP = 2;
    private static final long FRAME_DELAY_MS = 40L;

    private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private final Rect backgroundBounds = new Rect();
    private Bitmap gameBackground;
    private final Difficulty difficulty;
    private final BossEnemyFactory bossFactory = new BossEnemyFactory();
    private final List<BaseItem> items = new ArrayList<>();
    private int nextBossScore;
    private boolean bossActive, initialized, surfaceReady, foreground, gameOver;
    private GameListener listener;

    public interface GameListener {
        void onSound(int resource);
        void onBossChanged(boolean active);
        void onGameOver(int finalScore);
    }

    public void setGameListener(GameListener listener) { this.listener = listener; }
    private void sound(int resource) { if (listener != null) listener.onSound(resource); }

    private final MobEnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final EliteEnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    private final ElitePlusEnemyFactory elitePlusFactory = new ElitePlusEnemyFactory();
    private final EliteProEnemyFactory eliteProFactory = new EliteProEnemyFactory();
    private int difficultyTicks, difficultyStage;
    private double spawnCycle, heroShootCycle, enemyShootCycle;
    private final List<AbstractEnemy> enemies = new ArrayList<>();
    private final Rect enemyBounds = new Rect();

    private int enemySpawnTicks;
    private final Rect heroBounds = new Rect();

    private volatile boolean running;
    private volatile PointF touchTarget;
    private Thread gameThread;
    private int backgroundOffset;
    private static final int HERO_SHOOT_INTERVAL = 20;
    private static final int ENEMY_SHOOT_INTERVAL = 22;
    private static final int HERO_START_HP = 100;

    private final List<BaseBullet> heroBullets = new ArrayList<>();
    private final List<BaseBullet> enemyBullets = new ArrayList<>();
    private final Rect bulletBounds = new Rect();
    private final Paint hudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private HeroAircraft heroAircraft;
    private int heroShootTicks;
    private int enemyShootTicks;
    private int spawnedEnemies;
    private int score;

    protected BaseGame(Context context) {
        this(context, Difficulty.EASY);
    }

    protected BaseGame(Context context, Difficulty difficulty) {
        super(context);
        this.difficulty = difficulty;
        nextBossScore = difficulty.bossScore;
        spawnCycle = difficulty.spawnInterval;
        heroShootCycle = difficulty == Difficulty.HARD ? 18 : 20;
        enemyShootCycle = difficulty == Difficulty.EASY ? 22 : difficulty == Difficulty.HARD ? 18 : 20;
        for (EnemyFactory factory : new EnemyFactory[]{mobEnemyFactory, eliteEnemyFactory, elitePlusFactory, eliteProFactory, bossFactory}) {
            factory.setEnemyHpScale(difficulty.hpScale);
            factory.setEnemySpeedXScale(difficulty.speedScale);
            factory.setEnemySpeedYScale(difficulty.speedScale);
        }
        bossFactory.resetBossHpRule(difficulty == Difficulty.HARD ? 140 : 120,
                difficulty == Difficulty.HARD ? 30 : 0);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
        if (initialized) {
            startLoop();
            return;
        }
        initialized = true;
        ImageManager.init(getResources());
        BitmapFactory.Options backgroundOptions = new BitmapFactory.Options();
        backgroundOptions.inScaled = false;
        int backgroundResource = difficulty == Difficulty.HARD ? R.drawable.bg5
                : difficulty == Difficulty.NORMAL ? R.drawable.bg3 : R.drawable.bg;
        gameBackground = BitmapFactory.decodeResource(getResources(), backgroundResource, backgroundOptions);

        Bitmap hero = ImageManager.HERO_IMAGE;
        int left = (GAME_WIDTH - hero.getWidth()) / 2;
        int bottom = GAME_HEIGHT - 24;
        heroBounds.set(left, bottom - hero.getHeight(),
                left + hero.getWidth(), bottom);
        heroAircraft = HeroAircraft.getHeroAircraft();
        heroAircraft.resetForNewGame(HERO_START_HP);
        heroAircraft.setLocation(heroBounds.centerX(), heroBounds.centerY());

        heroBullets.clear();
        enemyBullets.clear();
        enemyShootTicks = 0;
        spawnedEnemies = 0;
        heroShootTicks = 0;
        score = 0;

        hudPaint.setColor(Color.WHITE);
        hudPaint.setTextSize(24f);
        hudPaint.setShadowLayer(2f, 1f, 1f, Color.BLACK);
        touchTarget = null;
        backgroundOffset = 0;
        enemies.clear();
        enemySpawnTicks = 0;
        startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
        // 绘制和触摸分别读取当前画布和 View 的尺寸。
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        stopLoop();
    }

    public void resumeGame() {
        foreground = true;
        startLoop();
    }

    public void pauseGame() {
        foreground = false;
        stopLoop();
        touchTarget = null;
    }

    private void startLoop() {
        if (!surfaceReady || !foreground || gameOver || gameThread != null) return;
        running = true;
        gameThread = new Thread(this, "aircraft-game");
        gameThread.start();
    }

    private void stopLoop() {
        running = false;
        Thread thread = gameThread;
        if (thread == null) return;
        thread.interrupt();
        boolean interrupted = false;
        while (thread.isAlive()) {
            try { thread.join(); }
            catch (InterruptedException exception) { interrupted = true; }
        }
        gameThread = null;
        if (interrupted) Thread.currentThread().interrupt();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN
                || action == MotionEvent.ACTION_MOVE) {
            int width = getWidth();
            int height = getHeight();
            if (width > 0 && height > 0) {
                float x = event.getX() * GAME_WIDTH / width;
                float y = event.getY() * GAME_HEIGHT / height;
                touchTarget = new PointF(x, y);
            }
        } else if (action == MotionEvent.ACTION_UP) {
            performClick();
        }

        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public void run() {
        while (running) {
            if (!heroAircraft.notValid()) {
                updateHeroPosition();
                backgroundOffset = (backgroundOffset + BACKGROUND_STEP) % GAME_HEIGHT;
                updateDifficulty();
                updateEnemies();
                updateHeroBullets();
                updateEnemyBullets();
                for (BaseItem item : items) item.forward();
                checkCollisions();
                updateBossMusic();
                if (heroAircraft.notValid() && !gameOver) {
                    gameOver = true;
                    if (listener != null) listener.onGameOver(score);
                }
            }
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                try {
                    drawFrame(canvas);
                } finally {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
                if (heroAircraft.notValid()) {
                    return;
                }
            }

            try {
                Thread.sleep(FRAME_DELAY_MS);
            } catch (InterruptedException exception) {
                return;
            }
        }
    }

    private void updateHeroPosition() {
        PointF target = touchTarget;
        if (target == null) {
            return;
        }

        int heroWidth = heroBounds.width();
        int heroHeight = heroBounds.height();

        int left = Math.round(target.x - heroWidth / 2f);
        int top = Math.round(target.y - heroHeight / 2f);

        left = Math.max(0, Math.min(left, GAME_WIDTH - heroWidth));
        top = Math.max(0, Math.min(top, GAME_HEIGHT - heroHeight));
        heroBounds.offsetTo(left, top);
        heroAircraft.setLocation(heroBounds.centerX(), heroBounds.centerY());
    }

    private void updateEnemies() {
        enemySpawnTicks++;
        if (enemySpawnTicks >= spawnCycle && enemies.size() < difficulty.maxEnemies) {
            enemySpawnTicks = 0;
            if (difficulty != Difficulty.EASY && score >= nextBossScore && !bossActive) {
                AbstractEnemy boss = bossFactory.createEnemy();
                boss.setLocation(GAME_WIDTH / 2.0, boss.getHeight() / 2.0 + 24);
                enemies.add(boss);
                nextBossScore += difficulty.bossScore;
                bossActive = true;
                if (listener != null) listener.onBossChanged(true);
            } else {
                double random = Math.random();
                double mob = difficulty == Difficulty.EASY ? .70 : difficulty == Difficulty.NORMAL ? .58 : .50;
                double elite = difficulty == Difficulty.EASY ? .90 : difficulty == Difficulty.NORMAL ? .82 : .72;
                double plus = difficulty == Difficulty.EASY ? .98 : difficulty == Difficulty.NORMAL ? .94 : .88;
                enemies.add(random < mob ? mobEnemyFactory.createEnemy()
                        : random < elite ? eliteEnemyFactory.createEnemy()
                        : random < plus ? elitePlusFactory.createEnemy() : eliteProFactory.createEnemy());
            }
        }

        for (AbstractEnemy enemy : enemies) {
            enemy.forward();
        }

        enemies.removeIf(AbstractEnemy::notValid);
    }

    private void updateDifficulty() {
        if (++difficultyTicks < 600) return;
        difficultyTicks = 0;
        int stage = ++difficultyStage;
        if (difficulty == Difficulty.EASY) return;
        boolean hard = difficulty == Difficulty.HARD;
        spawnCycle = hard ? Math.max(8, 20 * Math.pow(.93, stage))
                : Math.max(12, 22 * Math.pow(.95, stage));
        if (hard) {
            heroShootCycle = Math.max(10, 18 * Math.pow(.97, stage));
            enemyShootCycle = Math.max(10, 18 * Math.pow(.95, stage));
        }
        double hp = 1 + (hard ? .12 : .08) * stage;
        double speed = 1 + (hard ? .10 : .08) * stage;
        for (EnemyFactory factory : new EnemyFactory[]{mobEnemyFactory, eliteEnemyFactory, elitePlusFactory, eliteProFactory, bossFactory}) {
            factory.setEnemyHpScale(hp);
            factory.setEnemySpeedXScale(speed);
            factory.setEnemySpeedYScale(speed);
        }
    }

    private void drawFrame(Canvas canvas) {
        canvas.save();
        canvas.scale(canvas.getWidth() / (float) GAME_WIDTH,
                canvas.getHeight() / (float) GAME_HEIGHT);

        drawBackground(canvas);
        drawEnemies(canvas);
        for (BaseItem item : items) {
            Bitmap image = item.getImage();
            canvas.drawBitmap(image, item.getLocationX() - image.getWidth() / 2f,
                    item.getLocationY() - image.getHeight() / 2f, paint);
        }
        drawBullets(canvas, heroBullets);
        drawBullets(canvas, enemyBullets);

        if (!heroAircraft.notValid()) {
            canvas.drawBitmap(ImageManager.HERO_IMAGE, null, heroBounds, paint);
        }

        canvas.drawText("Score: " + score, 16f, 32f, hudPaint);
        canvas.drawText("HP: " + heroAircraft.getHp(), 16f, 62f, hudPaint);

        if (heroAircraft.notValid()) {
            canvas.drawText("Game Over", GAME_WIDTH / 2f - 65f,
                    GAME_HEIGHT / 2f, hudPaint);
        }

        canvas.restore();
    }

    private void drawEnemies(Canvas canvas) {
        for (AbstractEnemy enemy : enemies) {
            Bitmap image = enemy.getImage();
            int width = image.getWidth();
            int height = image.getHeight();

            // 敌机坐标表示图片中心，转换成绘制矩形的左上角。
            int left = enemy.getLocationX() - width / 2;
            int top = enemy.getLocationY() - height / 2;

            enemyBounds.set(left, top, left + width, top + height);
            canvas.drawBitmap(image, null, enemyBounds, paint);
        }
    }
    private void drawBackground(Canvas canvas) {
        Bitmap background = gameBackground;

        // 上方的一张填补背景下移后露出的区域。
        backgroundBounds.set(0, backgroundOffset - GAME_HEIGHT,
                GAME_WIDTH, backgroundOffset);
        canvas.drawBitmap(background, null, backgroundBounds, paint);

        backgroundBounds.set(0, backgroundOffset,
                GAME_WIDTH, backgroundOffset + GAME_HEIGHT);
        canvas.drawBitmap(background, null, backgroundBounds, paint);
    }

    private void updateHeroBullets() {
        heroAircraft.updateFireMode();
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }

        heroShootTicks++;
        if (heroShootTicks >= heroShootCycle) {
            heroShootTicks = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }
    }

    private void updateEnemyBullets() {
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }

        enemyShootTicks++;
        if (enemyShootTicks >= enemyShootCycle) {
            enemyShootTicks = 0;
            for (AbstractEnemy enemy : enemies) {
                enemyBullets.addAll(enemy.shoot());
            }
        }
    }

    private void checkCollisions() {
        for (BaseBullet bullet : enemyBullets) {
            if (!bullet.notValid() && !heroAircraft.notValid()
                    && heroAircraft.crash(bullet)) {
                sound(R.raw.bullet_hit);
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }

            for (AbstractEnemy enemy : enemies) {
                if (enemy.notValid() || !enemy.crash(bullet)) {
                    continue;
                }

                sound(R.raw.bullet_hit);
                enemy.decreaseHp(bullet.getPower());
                bullet.vanish();

                if (enemy.notValid()) {
                    score += enemy.getScore();
                    items.addAll(enemy.dropItems());
                }

                // 一颗普通子弹命中一架敌机后就停止检测。
                break;
            }
        }

        for (AbstractEnemy enemy : enemies) {
            if (!enemy.notValid() && heroAircraft.crash(enemy)) {
                enemy.vanish();
                heroAircraft.decreaseHp(heroAircraft.getHp());
                break;
            }
        }

        List<BaseItem> dropped = new ArrayList<>();
        for (BaseItem item : items) {
            if (item.notValid() || heroAircraft.notValid() || !heroAircraft.crash(item)) continue;
            List<AbstractAircraft> targets = new ArrayList<>();
            for (AbstractEnemy enemy : enemies) {
                if (!enemy.notValid()) targets.add(enemy);
            }
            item.activate(heroAircraft, targets, enemyBullets);
            item.vanish();
            sound(item instanceof BombItem ? R.raw.bomb_explosion : R.raw.get_supply);
            for (AbstractAircraft target : targets) {
                if (target.notValid()) {
                    AbstractEnemy enemy = (AbstractEnemy) target;
                    score += enemy.getScore();
                    dropped.addAll(enemy.dropItems());
                }
            }
        }
        items.removeIf(BaseItem::notValid);
        items.addAll(dropped);
        heroBullets.removeIf(BaseBullet::notValid);
        enemyBullets.removeIf(BaseBullet::notValid);
        enemies.removeIf(AbstractEnemy::notValid);
    }

    private void updateBossMusic() {
        boolean active = false;
        for (AbstractEnemy enemy : enemies) {
            if (enemy instanceof BossEnemy && !enemy.notValid()) active = true;
        }
        if (active != bossActive) {
            bossActive = active;
            if (listener != null) listener.onBossChanged(active);
        }
    }

    private void drawBullets(Canvas canvas, List<BaseBullet> bullets) {
        for (BaseBullet bullet : bullets) {
            Bitmap image = bullet.getImage();
            int width = image.getWidth();
            int height = image.getHeight();
            int left = bullet.getLocationX() - width / 2;
            int top = bullet.getLocationY() - height / 2;

            bulletBounds.set(left, top, left + width, top + height);
            canvas.drawBitmap(image, null, bulletBounds, paint);
        }
    }
}
