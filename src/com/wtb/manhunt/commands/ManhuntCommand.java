package com.wtb.manhunt.commands;

import com.wtb.manhunt.Main;
import com.wtb.manhunt.manhunt.ManhuntGUI;
import com.wtb.manhunt.manhunt.PlayStyle;
import com.wtb.manhunt.utility.UtilMath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ManhuntCommand implements CommandExecutor {

    private final Main main;

    public ManhuntCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("start")) {
                    startCommand(sender, args);
                } else if (args[0].equalsIgnoreCase("stop")) {
                    stopCommand(sender, args);
                }
                return false;
            } else {
                sender.sendMessage("[Error] Only players can run this command!");
            }
            return false;
        }
        Player player = (Player) sender;
        if(main.getManhuntManager().isStarted()) {
            player.sendMessage(ChatColor.GRAY + "You cannot change your Play Style. The game has already started.");
            return false;
        }
        player.openInventory(new ManhuntGUI(main.getManhuntManager(), player).getInventory());
        return false;
    }

    public void startCountdown(int seconds) {
        new BukkitRunnable() {
            int count = seconds;

            @Override
            public void run() {
                if ((count % 30 == 0) || (count <= 30 && count % 10 == 0) || (count <= 10)) {
                    Bukkit.broadcastMessage(ChatColor.GRAY + "Manhunt is beginning in " + ChatColor.GREEN + count + " Seconds" + ChatColor.GRAY + "!");
                }
                count--;
                if (count <= 0) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Manhunt has begun! Good luck!");
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (main.getManhuntManager().getPlayerHandler().getPlayStyle(online) == PlayStyle.NONE) {
                            main.getManhuntManager().getPlayerHandler().addPlayer(online, PlayStyle.SPECTATOR);
                            online.sendMessage(ChatColor.GRAY + "You did not pick a Play Style so you were moved to " + ChatColor.GREEN + "Spectator");
                        }
                        online.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20);
                        online.setHealth(online.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
                        online.setFoodLevel(20);
                        online.setGameMode(main.getManhuntManager().getPlayerHandler().getPlayStyle(online).getGamemode());
                    }
                    main.getManhuntManager().setStarted(true);
                    cancel();
                }
            }
        }.runTaskTimer(main, 0L, 20L);
    }

    private void stopCommand(CommandSender sender, String[] args) {
        main.getManhuntManager().setStarted(false);
    }

    private void startCommand(CommandSender sender, String[] args) {
        try {
            double time = Double.parseDouble(args[1].substring(0, args[1].length() - 1));
            if (UtilMath.isNumeric(args[1])) {
                time = Double.parseDouble(args[1]);
            }
            if (args[1].substring(args[1].length() - 1).equalsIgnoreCase("s")) {
                time *= 1.0D;
            } else if (args[1].substring(args[1].length() - 1).equalsIgnoreCase("m")) {
                time *= 60.0D;
            }
            startCountdown((int) time);
        } catch (Exception ex) {
            sender.sendMessage(ChatColor.RED + "Number exception caught! Are you entering the number correctly?");
        }
    }
}