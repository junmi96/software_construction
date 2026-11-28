package edu.hitsz.aircraftwar2026;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import edu.hitsz.game.Difficulty;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.rank.Rank;
import edu.hitsz.rank.RankAdapter;
import edu.hitsz.rank.RankDao;
import edu.hitsz.rank.SqliteRankDao;

public class RankActivity extends AppCompatActivity {
    static final String EXTRA_DIFFICULTY = "difficulty";
    private String mode;

    private RankDao rankDao;
    private RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        Difficulty difficulty = Difficulty.fromName(getIntent().getStringExtra(EXTRA_DIFFICULTY));
        mode = difficulty.rankMode();
        TextView title = findViewById(R.id.rank_title);
        int label = difficulty == Difficulty.HARD ? R.string.hard
                : difficulty == Difficulty.NORMAL ? R.string.normal : R.string.easy;
        title.setText(getString(R.string.rank_title, getString(label)));

        rankDao = new SqliteRankDao(this);
        adapter = new RankAdapter(this::confirmDelete);

        ListView list = findViewById(R.id.lv_rank);
        list.setAdapter(adapter);
        refreshRanks();
    }

    private void refreshRanks() {
        adapter.setRanks(rankDao.getAllRanks(mode));
    }

    private void confirmDelete(Rank rank) {
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