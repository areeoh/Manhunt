package com.wtb.manhunt.manhunt;

import com.wtb.manhunt.Main;

public class ManhuntManager {

    private boolean started = false;

    private final MenuHandler menuHandler;
    private final PlayerHandler playerHandler;

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
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}