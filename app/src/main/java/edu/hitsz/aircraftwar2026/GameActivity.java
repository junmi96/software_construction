package edu.hitsz.aircraftwar2026;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import edu.hitsz.game.*;
import edu.hitsz.music.MyMediaPlayer;
import edu.hitsz.music.MySoundPool;

public class GameActivity extends AppCompatActivity implements BaseGame.GameListener {
    public static final String EXTRA_DIFFICULTY = "difficulty";
    public static final String EXTRA_MUSIC = "music_enabled";
    private final Handler handler = new Handler(Looper.getMainLooper());
    private BaseGame game;
    private MyMediaPlayer music;
    private MySoundPool effects;
    private boolean enabled, resumed, destroyed, bossActive, gameOver;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        enabled = getIntent().getBooleanExtra(EXTRA_MUSIC, true);
        if (enabled) {
            music = new MyMediaPlayer(this);
            effects = new MySoundPool(this);
        }
        Difficulty difficulty = Difficulty.fromName(getIntent().getStringExtra(EXTRA_DIFFICULTY));
        switch (difficulty) {
            case HARD: game = new HardGame(this); break;
            case NORMAL: game = new NormalGame(this); break;
            default: game = new EasyGame(this);
        }
        game.setGameListener(this);
        setContentView(game);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content), (view, insets) -> {
                    androidx.core.graphics.Insets bars = insets.getInsets(
                            androidx.core.view.WindowInsetsCompat.Type.systemBars()
                                    | androidx.core.view.WindowInsetsCompat.Type.displayCutout());
                    view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });
    }
    @Override protected void onResume() {
        super.onResume();
        resumed = true;
        game.resumeGame();
        if (enabled && !gameOver) music.play(bossActive);
    }
    @Override protected void onPause() {
        resumed = false;
        game.pauseGame();
        if (enabled) { music.pause(); effects.pause(); }
        super.onPause();
    }
    @Override public void onSound(int resource) {
        handler.post(() -> {
            if (!destroyed && resumed && enabled) effects.play(resource);
        });
    }
    @Override public void onBossChanged(boolean active) {
        handler.post(() -> {
            if (destroyed) return;
            bossActive = active;
            if (enabled && resumed && !gameOver) music.play(active);
        });
    }
    @Override public void onGameOver() {
        handler.post(() -> {
            if (destroyed || gameOver) return;
            gameOver = true;
            if (enabled) {
                music.pause();
                if (resumed) effects.play(R.raw.game_over);
            }
        });
    }
    @Override protected void onDestroy() {
        destroyed = true;
        game.pauseGame();
        handler.removeCallbacksAndMessages(null);
        if (enabled) { music.release(); effects.release(); }
        super.onDestroy();
    }
}
