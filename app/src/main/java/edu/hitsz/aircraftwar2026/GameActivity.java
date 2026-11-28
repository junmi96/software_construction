package edu.hitsz.aircraftwar2026;
import android.os.Bundle;
import android.content.Intent;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import edu.hitsz.rank.Rank;
import edu.hitsz.rank.SqliteRankDao;
import android.os.Handler;
import android.os.Looper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
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
        FrameLayout root = new FrameLayout(this);
        root.addView(game, new FrameLayout.LayoutParams(-1, -1));
        addGameHud(root);
        setContentView(root);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(android.R.id.content), (view, insets) -> {
                    androidx.core.graphics.Insets bars = insets.getInsets(
                            androidx.core.view.WindowInsetsCompat.Type.systemBars()
                                    | androidx.core.view.WindowInsetsCompat.Type.displayCutout());
                    view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
                    return insets;
                });
    }

    private void addGameHud(FrameLayout root) {
        TextView hud = new TextView(this);
        hud.setText("SCORE  0\nHP  100");
        hud.setTextColor(Color.WHITE);
        hud.setTextSize(14);
        hud.setPadding(24, 10, 24, 10);
        hud.setBackgroundResource(R.drawable.hud_panel);
        FrameLayout.LayoutParams hudParams = new FrameLayout.LayoutParams(180, 70);
        hudParams.gravity = Gravity.TOP | Gravity.START;
        hudParams.setMargins(16, 16, 0, 0);
        root.addView(hud, hudParams);

        ProgressBar hp = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        hp.setMax(100); hp.setProgress(100);
        hp.setProgressDrawable(getDrawable(R.drawable.hp_fill));
        hp.setBackgroundResource(R.drawable.hp_track);
        FrameLayout.LayoutParams hpParams = new FrameLayout.LayoutParams(180, 18);
        hpParams.gravity = Gravity.TOP | Gravity.START;
        hpParams.setMargins(16, 92, 0, 0);
        root.addView(hp, hpParams);

        ImageButton pause = new ImageButton(this);
        pause.setImageResource(R.drawable.ic_pause);
        pause.setBackgroundResource(R.drawable.dialog_panel);
        pause.setContentDescription("暂停");
        FrameLayout.LayoutParams pauseParams = new FrameLayout.LayoutParams(58, 58);
        pauseParams.gravity = Gravity.TOP | Gravity.END;
        pauseParams.setMargins(0, 16, 16, 0);
        root.addView(pause, pauseParams);
        pause.setOnClickListener(v -> showPauseDialog(pause));
    }

    private void showPauseDialog(ImageButton pause) {
        game.pauseGame();
        AlertDialog pauseDialog = new AlertDialog.Builder(this)
                .setTitle("游戏暂停")
                .setMessage("准备好后继续战斗")
                .setIcon(R.drawable.ic_pause)
                .setNegativeButton("主页", (dialog, which) -> {
                    startActivity(new Intent(this, MainActivity.class)); finish();
                })
                .setNeutralButton("重新开始", (dialog, which) -> recreate())
                .setPositiveButton("继续", (dialog, which) -> game.resumeGame())
                .show();
        pauseDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_panel);
        pauseDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_home, 0, 0, 0);
        pauseDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_restart, 0, 0, 0);
        pauseDialog.getButton(AlertDialog.BUTTON_POSITIVE).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play, 0, 0, 0);
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
