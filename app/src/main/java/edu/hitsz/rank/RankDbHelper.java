package edu.hitsz.rank;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

    public class RankDbHelper extends SQLiteOpenHelper {

        public RankDbHelper(Context context) {
            super(context.getApplicationContext(), "rank.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE rank ("
                    + "_id INTEGER PRIMARY KEY, "
                    + "mode TEXT NOT NULL, "
                    + "name TEXT NOT NULL, "
                    + "score INTEGER NOT NULL, "
                    + "time INTEGER NOT NULL)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // 增加数据库版本时，在这里编写迁移逻辑。
        }
    }

