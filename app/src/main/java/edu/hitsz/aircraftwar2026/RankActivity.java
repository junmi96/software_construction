package edu.hitsz.aircraftwar2026;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.hitsz.rank.Rank;
import edu.hitsz.rank.RankAdapter;
import edu.hitsz.rank.RankDao;
import edu.hitsz.rank.SqliteRankDao;

public class RankActivity extends AppCompatActivity {
    static final String MODE = "easy";

    private RankDao rankDao;
    private RankAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        rankDao = new SqliteRankDao(this);
        adapter = new RankAdapter(this::confirmDelete);

        ListView list = findViewById(R.id.lv_rank);
        list.setAdapter(adapter);
        refreshRanks();
    }

    private void refreshRanks() {
        adapter.setRanks(rankDao.getAllRanks(MODE));
    }

    private void confirmDelete(Rank rank) {
        new AlertDialog.Builder(this)
                .setMessage("删除这条成绩？")
                .setNegativeButton("取消", null)
                .setPositiveButton("删除", (dialog, which) -> {
                    rankDao.deleteRank(MODE, rank);
                    refreshRanks();
                })
                .show();
    }
}