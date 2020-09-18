package com.wtb.manhunt.manhunt;

import com.wtb.manhunt.Main;

public class ManhuntManager {

    private final MenuHandler menuHandler;
    private final PlayerHandler playerHandler;

    private long startTime = 0L;

    public ManhuntManager(Main main) {
        this.menuHandler = new MenuHandler(main);
        this.playerHandler = new PlayerHandler(main);
    }

    public MenuHandler getMenuHandler() {
        return menuHandler;
    }

    public PlayerHandler getPlayerHandler() {
        return playerHandler;
    }

    public boolean isStarted() {
        return startTime != 0;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}