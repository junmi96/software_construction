package edu.hitsz.aircraftwar2026;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.hitsz.game.Difficulty;
import edu.hitsz.rank.Rank;
import edu.hitsz.rank.RankAdapter;
import edu.hitsz.rank.RankDao;
import edu.hitsz.rank.SqliteRankDao;

public class RankActivity extends AppCompatActivity {
    static final String EXTRA_DIFFICULTY = "difficulty";

    private Difficulty difficulty;
    private TextView title;
    private ListView list;
    private RankDao rankDao;
    private RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        View content = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(content, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        title = findViewById(R.id.rank_title);
        rankDao = new SqliteRankDao(this);
        adapter = new RankAdapter(this::confirmDelete);
        list = findViewById(R.id.lv_rank);
        list.setEmptyView(findViewById(R.id.rank_empty));
        list.setAdapter(adapter);

        String initialDifficulty = savedInstanceState == null
                ? getIntent().getStringExtra(EXTRA_DIFFICULTY)
                : savedInstanceState.getString(EXTRA_DIFFICULTY);
        Difficulty initial = Difficulty.fromName(initialDifficulty);
        RadioGroup choices = findViewById(R.id.rank_difficulties);
        choices.check(initial == Difficulty.HARD ? R.id.rank_hard
                : initial == Difficulty.NORMAL ? R.id.rank_normal : R.id.rank_easy);
        choices.setOnCheckedChangeListener((group, checkedId) -> {
            selectDifficulty(checkedId == R.id.rank_hard ? Difficulty.HARD
                    : checkedId == R.id.rank_normal ? Difficulty.NORMAL : Difficulty.EASY);
            list.setSelection(0);
        });
        selectDifficulty(initial);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(EXTRA_DIFFICULTY, difficulty.name());
    }

    private void selectDifficulty(Difficulty selected) {
        difficulty = selected;
        int label = selected == Difficulty.HARD ? R.string.hard
                : selected == Difficulty.NORMAL ? R.string.normal : R.string.easy;
        title.setText(getString(R.string.rank_title, getString(label)));
        refreshRanks();
    }

    private void refreshRanks() {
        adapter.setRanks(rankDao.getAllRanks(difficulty.rankMode()));
    }

    private void confirmDelete(Rank rank) {
        String mode = difficulty.rankMode();
        new AlertDialog.Builder(this)
                .setMessage("删除这条成绩？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (dialog, which) -> {
                    rankDao.deleteRank(mode, rank);
                    refreshRanks();
                })
                .show();
    }
}
