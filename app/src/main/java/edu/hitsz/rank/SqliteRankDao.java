package edu.hitsz.rank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

    public class SqliteRankDao implements RankDao {
        private final RankDbHelper dbHelper;

        public SqliteRankDao(Context context) {
            dbHelper = new RankDbHelper(context);
        }

        @Override
        public void saveRank(String mode, Rank rank) {
            ContentValues values = new ContentValues();
            values.put("mode", mode);
            values.put("name", rank.getName());
            values.put("score", rank.getScore());
            values.put("time", rank.getTimestamp());

            dbHelper.getWritableDatabase().insertOrThrow("rank", null, values);
        }

        @Override
        public Rank[] getAllRanks(String mode) {
            return queryRanks(mode, null);
        }

        @Override
        public Rank[] getTopRanks(String mode, int n) {
            if (n <= 0) {
                return new Rank[0];
            }
            return queryRanks(mode, String.valueOf(n));
        }

        private Rank[] queryRanks(String mode, String limit) {
            List<Rank> ranks = new ArrayList<>();

            try (Cursor cursor = dbHelper.getReadableDatabase().query(
                    "rank",
                    new String[]{"_id", "name", "score", "time"},
                    "mode = ?",
                    new String[]{mode},
                    null,
                    null,
                    "score DESC, time ASC, _id ASC",
                    limit)) {
                while (cursor.moveToNext()) {
                    ranks.add(new Rank(
                            cursor.getLong(0),
                            ranks.size() + 1,
                            cursor.getString(1),
                            cursor.getInt(2),
                            cursor.getLong(3)
                    ));
                }
            }

            return ranks.toArray(new Rank[0]);
        }

        @Override
        public void deleteRank(String mode, Rank rank) {
            dbHelper.getWritableDatabase().delete(
                    "rank",
                    "_id = ? AND mode = ?",
                    new String[]{String.valueOf(rank.getId()), mode}
            );
        }
    }

