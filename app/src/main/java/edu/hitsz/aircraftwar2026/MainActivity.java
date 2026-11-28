package edu.hitsz.aircraftwar2026;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.game.EasyGame;
import edu.hitsz.rank.Rank;
import edu.hitsz.rank.SqliteRankDao;

public class MainActivity extends AppCompatActivity {
    private boolean resultShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EasyGame game = new EasyGame(this);
        game.setOnGameOverListener(this::showResult);
        setContentView(game);
    }

    private void showResult(int finalScore) {
        if (resultShown || isFinishing() || isDestroyed()) {
            return;
        }
        resultShown = true;

        EditText nameInput = new EditText(this);
        nameInput.setSingleLine(true);
        nameInput.setHint("玩家姓名");

        new AlertDialog.Builder(this)
                .setTitle("游戏结束 · " + finalScore + " 分")
                .setView(nameInput)
                .setCancelable(false)
                .setNegativeButton("跳过", (dialog, which) -> openRanking())
                .setPositiveButton("保存", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    if (name.isEmpty()) {
                        name = "玩家";
                    }
                    new SqliteRankDao(this).saveRank(
                            RankActivity.MODE,
                            new Rank(0, name, finalScore, System.currentTimeMillis()));
                    openRanking();
                })
                .show();
    }

    private void openRanking() {
        startActivity(new Intent(this, RankActivity.class));
        finish();
    }
}