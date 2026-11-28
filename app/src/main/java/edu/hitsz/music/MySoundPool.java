package edu.hitsz.music;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import edu.hitsz.aircraftwar2026.R;

public final class MySoundPool {
    private final SoundPool pool;
    private final Map<Integer, Integer> sounds = new HashMap<>();
    private final Set<Integer> loaded = new HashSet<>();

    public MySoundPool(Context context) {
        pool = new SoundPool.Builder().setMaxStreams(6)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();
        pool.setOnLoadCompleteListener((soundPool, id, status) -> {
            if (status == 0) loaded.add(id);
        });
        for (int resource : new int[]{R.raw.bullet_hit, R.raw.bomb_explosion,
                R.raw.get_supply, R.raw.game_over}) {
            sounds.put(resource, pool.load(context.getApplicationContext(), resource, 1));
        }
    }

    public void play(int resource) {
        Integer id = sounds.get(resource);
        if (id != null && loaded.contains(id)) pool.play(id, 0.7f, 0.7f, 1, 0, 1f);
    }

    public void pause() { pool.autoPause(); }
    public void release() {
        pool.setOnLoadCompleteListener(null);
        pool.release();
        loaded.clear();
    }
}
