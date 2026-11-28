package edu.hitsz.aircraftwar2026;

import android.app.Instrumentation;
import android.content.Context;
import android.view.ViewGroup;
import androidx.appcompat.widget.SwitchCompat;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import edu.hitsz.game.EasyGame;
import edu.hitsz.game.NormalGame;
import edu.hitsz.game.HardGame;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class GameNavigationTest {
    @Test
    public void difficultyButtonsLaunchMatchingGamesAndRememberMute() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        int[] buttons = {R.id.easy_button, R.id.normal_button, R.id.hard_button};
        Class<?>[] games = {EasyGame.class, NormalGame.class, HardGame.class};
        String[] names = {"EASY", "NORMAL", "HARD"};
        Context context = instrumentation.getTargetContext();
        android.content.SharedPreferences preferences =
                context.getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
        boolean hadValue = preferences.contains("music");
        boolean original = preferences.getBoolean("music", true);
        try {
            for (int i = 0; i < buttons.length; i++) {
                final int button = buttons[i];
                Instrumentation.ActivityMonitor monitor = instrumentation.addMonitor(
                        GameActivity.class.getName(), null, false);
                try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
                    final boolean checkPersistedMute = i > 0;
                    scenario.onActivity(activity -> {
                        SwitchCompat toggle = activity.findViewById(R.id.music_switch);
                        if (checkPersistedMute) assertFalse(toggle.isChecked());
                        toggle.setChecked(false);
                        activity.findViewById(button).performClick();
                    });
                    GameActivity game = (GameActivity) monitor.waitForActivityWithTimeout(5000);
                    assertNotNull("GameActivity should open", game);
                    try {
                        assertEquals(names[i], game.getIntent().getStringExtra(GameActivity.EXTRA_DIFFICULTY));
                        assertFalse(game.getIntent().getBooleanExtra(GameActivity.EXTRA_MUSIC, true));
                        final Class<?> expected = games[i];
                        instrumentation.runOnMainSync(() -> {
                            ViewGroup content = game.findViewById(android.R.id.content);
                            assertTrue(expected.isInstance(content.getChildAt(0)));
                        });
                    } finally {
                        instrumentation.runOnMainSync(game::finish);
                        instrumentation.waitForIdleSync();
                    }
                } finally {
                    instrumentation.removeMonitor(monitor);
                }
            }
        } finally {
            if (hadValue) preferences.edit().putBoolean("music", original).commit();
            else preferences.edit().remove("music").commit();
        }
    }
}
