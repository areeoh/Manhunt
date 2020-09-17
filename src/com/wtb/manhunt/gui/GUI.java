package com.wtb.manhunt.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GUI {

    private final String title;
    private final Inventory inventory;
    private final Player player;
    private final List<Button> buttons;

    public GUI(String title, int size, Player player) {
        this.title = title;
        this.inventory = Bukkit.createInventory(null, size, title);
        this.player = player;
        this.buttons = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public Button getButton(ItemStack itemStack) {
        if(itemStack != null && itemStack.getType() != Material.AIR) {
            if(itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
                for (Button button : buttons) {
                    if(button.getItemStack().equals(itemStack)) {
                        return button;
                    }
                }
            }
        }
        return null;
    }

    protected void addButton(Button button) {
        this.buttons.add(button);
    }

    protected void construct() {
        for (Button button : buttons) {
            getInventory().setItem(button.getSlot(), button.getItemStack());
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public List<Button> getButtons() {
        return buttons;
    }
}