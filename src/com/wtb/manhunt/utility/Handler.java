package com.wtb.manhunt.utility;

import com.wtb.manhunt.Main;
import org.bukkit.event.Listener;

public class Handler implements Listener {

    private final Main instance;

    public Handler(Main instance) {
        this.instance = instance;
        instance.getServer().getPluginManager().registerEvents(this, instance);
    }

    public Main getInstance() {
        return instance;
    }
}