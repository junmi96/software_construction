package edu.hitsz.aircraftwar2026;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RankNavigationTest {
    @Test
    public void menuOpensRankingAndDifficultyChangesTitle() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.rank_button)).perform(click());
            onView(withId(R.id.rank_easy)).check(matches(isChecked()));
            onView(withId(R.id.rank_normal)).perform(click());
            onView(withId(R.id.rank_title)).check(matches(withText("中等模式排行榜")));
        }
    }

    @Test
    public void recreatingRankingKeepsSelectedDifficulty() {
        try (ActivityScenario<RankActivity> scenario = ActivityScenario.launch(RankActivity.class)) {
            onView(withId(R.id.rank_hard)).perform(click());
            scenario.recreate();
            onView(withId(R.id.rank_hard)).check(matches(isChecked()));
            onView(withId(R.id.rank_title)).check(matches(withText("困难模式排行榜")));
        }
    }
}
