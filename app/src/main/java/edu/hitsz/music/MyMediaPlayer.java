package edu.hitsz.music;

import android.content.Context;
import android.media.MediaPlayer;
import edu.hitsz.aircraftwar2026.R;

/** All calls are serialized on the main thread by GameActivity. */
public final class MyMediaPlayer {
    private final MediaPlayer normal;
    private final MediaPlayer boss;

    public MyMediaPlayer(Context context) {
        normal = MediaPlayer.create(context.getApplicationContext(), R.raw.bgm);
        boss = MediaPlayer.create(context.getApplicationContext(), R.raw.bgm_boss);
        if (normal != null) normal.setLooping(true);
        if (boss != null) boss.setLooping(true);
    }

    public void play(boolean bossActive) {
        MediaPlayer selected = bossActive ? boss : normal;
        MediaPlayer other = bossActive ? normal : boss;
        if (other != null && other.isPlaying()) other.pause();
        if (selected != null && !selected.isPlaying()) selected.start();
    }

    public void pause() {
        if (normal != null && normal.isPlaying()) normal.pause();
        if (boss != null && boss.isPlaying()) boss.pause();
    }

    public void release() {
        if (normal != null) normal.release();
        if (boss != null) boss.release();
    }
}
