package edu.hitsz.application;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public final class AudioManager {

    private static final String AUDIO_DIR = "src/videos/";
    private static final String BGM = AUDIO_DIR + "bgm.wav";
    private static final String BGM_BOSS = AUDIO_DIR + "bgm_boss.wav";
    private static final String BOMB = AUDIO_DIR + "bomb_explosion.wav";
    private static final String HIT = AUDIO_DIR + "bullet_hit.wav";
    private static final String GET_SUPPLY = AUDIO_DIR + "get_supply.wav";
    private static final String GAME_OVER = AUDIO_DIR + "game_over.wav";

    private static final AudioManager INSTANCE = new AudioManager();

    private final List<MusicThread> effectThreads = new LinkedList<>();
    private MusicThread currentBgmThread;
    private String currentBgmFile;

    private AudioManager() {
    }

    public static AudioManager getInstance() {
        return INSTANCE;
    }

    public synchronized void playNormalBgm() {
        playBgm(BGM);
    }

    public synchronized void playBossBgm() {
        playBgm(BGM_BOSS);
    }

    public void playBomb() {
        playEffect(BOMB);
    }

    public void playHit() {
        playEffect(HIT);
    }

    public void playGetSupply() {
        playEffect(GET_SUPPLY);
    }

    public synchronized void playGameOver() {
        stopBgm();
        playEffect(GAME_OVER);
    }

    public synchronized void stopBgm() {
        if (currentBgmThread != null) {
            currentBgmThread.stopMusic();
            currentBgmThread = null;
            currentBgmFile = null;
        }
    }

    public synchronized void stopAll() {
        stopBgm();
        for (MusicThread thread : effectThreads) {
            thread.stopMusic();
        }
        effectThreads.clear();
    }

    private synchronized void playBgm(String filename) {
        if (filename.equals(currentBgmFile) && currentBgmThread != null && currentBgmThread.isAlive()) {
            return;
        }
        stopBgm();
        currentBgmThread = new MusicThread(filename, true);
        currentBgmFile = filename;
        currentBgmThread.start();
    }

    private synchronized void playEffect(String filename) {
        cleanupFinishedEffects();
        MusicThread effectThread = new MusicThread(filename, false);
        effectThreads.add(effectThread);
        effectThread.start();
    }

    private void cleanupFinishedEffects() {
        Iterator<MusicThread> iterator = effectThreads.iterator();
        while (iterator.hasNext()) {
            MusicThread thread = iterator.next();
            if (!thread.isAlive()) {
                iterator.remove();
            }
        }
    }
}
