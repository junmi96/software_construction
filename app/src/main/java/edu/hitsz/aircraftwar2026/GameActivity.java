package edu.hitsz.aircraftwar2026;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import edu.hitsz.rank.Rank;
import edu.hitsz.rank.SqliteRankDao;
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
    private Difficulty difficulty;
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
        difficulty = Difficulty.fromName(getIntent().getStringExtra(EXTRA_DIFFICULTY));
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
    @Override public void onGameOver(int finalScore) {
        handler.post(() -> {
            if (destroyed || gameOver) return;
            gameOver = true;
            if (enabled) {
                music.pause();
                if (resumed) effects.play(R.raw.game_over);
            }
            showResult(finalScore);
        });
    }
    private void showResult(int finalScore) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("玩家姓名");
        new AlertDialog.Builder(this)
                .setTitle("游戏结束 · " + finalScore + " 分")
                .setView(input)
                .setCancelable(false)
                .setNegativeButton("跳过", (dialog, which) -> openRanking())
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = input.getText().toString().trim();
                    new SqliteRankDao(this).saveRank(difficulty.rankMode(),
                            new Rank(0, name.isEmpty() ? "玩家" : name,
                                    finalScore, System.currentTimeMillis()));
                    openRanking();
                })
                .show();
    }

    private void openRanking() {
        startActivity(new Intent(this, RankActivity.class)
                .putExtra(RankActivity.EXTRA_DIFFICULTY, difficulty.name()));
        finish();
    }

    @Override protected void onDestroy() {
        destroyed = true;
        game.pauseGame();
        handler.removeCallbacksAndMessages(null);
        if (enabled) { music.release(); effects.release(); }
        super.onDestroy();
    }
}
