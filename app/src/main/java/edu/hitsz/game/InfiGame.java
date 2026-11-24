package edu.hitsz.game;

public class InfiGame extends MediumGame {

    public InfiGame(String playerName) {
        super(playerName);
    }

    @Override
    protected boolean isHeroInvincible() {
        return true;
    }

    @Override
    protected int getMaxGameDurationMs() {
        return 60_000;
    }
}
