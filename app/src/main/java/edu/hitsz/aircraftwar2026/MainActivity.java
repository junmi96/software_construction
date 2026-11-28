package edu.hitsz.aircraftwar2026;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import edu.hitsz.game.Difficulty;
public class MainActivity extends AppCompatActivity {
    private boolean launching;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        SwitchCompat music = findViewById(R.id.music_switch);
        music.setChecked(getPreferences(MODE_PRIVATE).getBoolean("music", true));
        music.setOnCheckedChangeListener((button, checked) ->
                getPreferences(MODE_PRIVATE).edit().putBoolean("music", checked).apply());
        findViewById(R.id.easy_button).setOnClickListener(v -> startGame(Difficulty.EASY, music.isChecked()));
        findViewById(R.id.normal_button).setOnClickListener(v -> startGame(Difficulty.NORMAL, music.isChecked()));
        findViewById(R.id.hard_button).setOnClickListener(v -> startGame(Difficulty.HARD, music.isChecked()));
    }
    private void startGame(Difficulty difficulty, boolean enabled) {
        if (launching) return;
        launching = true;
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRA_DIFFICULTY, difficulty.name());
        intent.putExtra(GameActivity.EXTRA_MUSIC, enabled);
        startActivity(intent);
    }
    @Override protected void onResume() {
        super.onResume();
        launching = false;
    }
}
