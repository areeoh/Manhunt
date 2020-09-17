package com.wtb.manhunt.manhunt;

import com.wtb.manhunt.gui.Button;
import com.wtb.manhunt.gui.GUI;
import com.wtb.manhunt.utility.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class ManhuntGUI extends GUI {

    public ManhuntGUI(ManhuntManager manhuntManager, Player player) {
        super(ChatColor.GREEN + "Select your play style!", 27, player);

        addButton(new Button(new ItemBuilder(Material.FEATHER).setDisplayName(ChatColor.GREEN + "Speed Runner").setLore(ChatColor.GRAY + "Click here to become a " + ChatColor.AQUA + "Speed Runner"), 11) {
            @Override
            public void onClick(Player player) {
                player.getInventory().remove(Material.COMPASS);

                if(manhuntManager.getPlayerHandler().getPlayStyle(player) == PlayStyle.HUNTER) {
                    player.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.RED + "Hunter");
                }

                manhuntManager.getPlayerHandler().addPlayer(player, PlayStyle.SPEED_RUNNER);
                player.sendMessage(ChatColor.GRAY + "You are now a " + ChatColor.AQUA + "Speed Runner");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            }
        });

        addButton(new Button(new ItemBuilder(Material.IRON_SWORD).setDisplayName(ChatColor.GREEN + "Hunter").setLore(ChatColor.GRAY + "Click here to become a " + ChatColor.RED + "Hunter"), 15) {
            @Override
            public void onClick(Player player) {
                if(Arrays.stream(player.getInventory().getContents()).noneMatch(itemStack -> itemStack != null && itemStack.getType() == Material.COMPASS)) {
                    player.getInventory().addItem(new ItemStack(Material.COMPASS));
                }
                if(manhuntManager.getPlayerHandler().getPlayStyle(player) == PlayStyle.SPEED_RUNNER) {
                    player.sendMessage(ChatColor.GRAY + "You are no longer " + ChatColor.AQUA + "Speed Runner");
                }

                manhuntManager.getPlayerHandler().addPlayer(player, PlayStyle.HUNTER);
                player.sendMessage(ChatColor.GRAY + "You are now a " + ChatColor.RED + "Hunter");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
            }
        });

        manhuntManager.getMenuHandler().addGUI(this);
        construct();
    }
}