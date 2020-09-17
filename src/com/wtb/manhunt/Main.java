package com.wtb.manhunt;

import com.wtb.manhunt.commands.ManhuntCommand;
import com.wtb.manhunt.manhunt.ManhuntManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private ManhuntManager manhuntManager;

    @Override
    public void onEnable() {
        this.manhuntManager = new ManhuntManager(this);

        getCommand("manhunt").setExecutor(new ManhuntCommand(this));
    }

    public ManhuntManager getManhuntManager() {
        return manhuntManager;
    }
}