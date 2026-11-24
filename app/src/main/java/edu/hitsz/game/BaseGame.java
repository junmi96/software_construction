package edu.hitsz.game;

import android.content.Context;
import android.graphics.Bitmap;
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
    private static final int ENEMY_SPAWN_INTERVAL = 24;
    private static final int MAX_ENEMIES = 5;

    private final MobEnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final EliteEnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
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
        super(context);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ImageManager.init(getResources());

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
        running = true;
        gameThread = new Thread(this, "aircraft-game");
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
        // 绘制和触摸分别读取当前画布和 View 的尺寸。
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        running = false;
        Thread thread = gameThread;
        if (thread == null) {
            return;
        }

        thread.interrupt();
        boolean interrupted = false;
        while (thread.isAlive()) {
            try {
                thread.join();
            } catch (InterruptedException exception) {
                interrupted = true;
            }
        }
        gameThread = null;

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
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
                updateEnemies();
                updateHeroBullets();
                updateEnemyBullets();
                checkCollisions();
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
        if (enemySpawnTicks >= ENEMY_SPAWN_INTERVAL) {
            enemySpawnTicks = 0;
            if (enemies.size() < MAX_ENEMIES) {
                spawnedEnemies++;
                enemies.add(spawnedEnemies % 3 == 0
                        ? eliteEnemyFactory.createEnemy()
                        : mobEnemyFactory.createEnemy());
            }
        }

        for (AbstractEnemy enemy : enemies) {
            enemy.forward();
        }

        enemies.removeIf(AbstractEnemy::notValid);
    }

    private void drawFrame(Canvas canvas) {
        canvas.save();
        canvas.scale(canvas.getWidth() / (float) GAME_WIDTH,
                canvas.getHeight() / (float) GAME_HEIGHT);

        drawBackground(canvas);
        drawEnemies(canvas);
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
        Bitmap background = ImageManager.BACKGROUND_IMAGE;

        // 上方的一张填补背景下移后露出的区域。
        backgroundBounds.set(0, backgroundOffset - GAME_HEIGHT,
                GAME_WIDTH, backgroundOffset);
        canvas.drawBitmap(background, null, backgroundBounds, paint);

        backgroundBounds.set(0, backgroundOffset,
                GAME_WIDTH, backgroundOffset + GAME_HEIGHT);
        canvas.drawBitmap(background, null, backgroundBounds, paint);
    }

    private void updateHeroBullets() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }

        heroShootTicks++;
        if (heroShootTicks >= HERO_SHOOT_INTERVAL) {
            heroShootTicks = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }
    }

    private void updateEnemyBullets() {
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }

        enemyShootTicks++;
        if (enemyShootTicks >= ENEMY_SHOOT_INTERVAL) {
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

                enemy.decreaseHp(bullet.getPower());
                bullet.vanish();

                if (enemy.notValid()) {
                    score += enemy.getScore();
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

        heroBullets.removeIf(BaseBullet::notValid);
        enemyBullets.removeIf(BaseBullet::notValid);
        enemies.removeIf(AbstractEnemy::notValid);
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