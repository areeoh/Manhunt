package com.wtb.manhunt.manhunt;

import com.wtb.manhunt.Main;
import com.wtb.manhunt.utility.Handler;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerHandler extends Handler {

    private final Map<UUID, PlayStyle> playerMap = new HashMap<>();

    public PlayerHandler(Main instance) {
        super(instance);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack.getType() != Material.COMPASS) {
            return;
        }
        final Player nearestRunner = findNearestRunner(player.getLocation());
        if (nearestRunner == null) {
            return;
        }
        if (nearestRunner.getWorld().getEnvironment() == World.Environment.NORMAL && player.getWorld().getEnvironment() == World.Environment.NORMAL) {
            player.setCompassTarget(nearestRunner.getLocation());
        }
        player.sendMessage(ChatColor.GRAY + "Tracking: " + ChatColor.RED + nearestRunner.getName());
        player.sendMessage(ChatColor.GRAY + "World: " + ChatColor.GREEN + WordUtils.capitalizeFully(nearestRunner.getWorld().getEnvironment().name().toLowerCase().replaceAll("_", " ")));
        player.sendMessage(ChatColor.GRAY + "X: " + ChatColor.RED + (int) nearestRunner.getLocation().getX());
        player.sendMessage(ChatColor.GRAY + "Y: " + ChatColor.RED + (int) nearestRunner.getLocation().getZ());
        player.sendMessage(ChatColor.GRAY + "Z: " + ChatColor.RED + (int) nearestRunner.getLocation().getZ());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!playerMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (playerMap.get(event.getPlayer().getUniqueId()) != PlayStyle.HUNTER) {
            return;
        }
        if(!getInstance().getManhuntManager().isStarted()) {
            return;
        }
        if(System.currentTimeMillis() - getInstance().getManhuntManager().getStartTime() >= 10000) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onHandleFrozenPvP(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) {
            return;
        }
        if(!(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        if(!(getPlayStyle(damager) == PlayStyle.HUNTER || getPlayStyle((Player) event.getEntity()) == PlayStyle.HUNTER)) {
            return;
        }
        if(!getInstance().getManhuntManager().isStarted()) {
            return;
        }
        if(System.currentTimeMillis() - getInstance().getManhuntManager().getStartTime() >= 10000) {
            return;
        }
        event.setCancelled(true);

    }

    @EventHandler
    public void handleRunnerRespawn(PlayerRespawnEvent event) {
        if (!playerMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (playerMap.get(event.getPlayer().getUniqueId()) != PlayStyle.SPEED_RUNNER) {
            return;
        }
        playerMap.put(event.getPlayer().getUniqueId(), PlayStyle.SPECTATOR);
        event.getPlayer().setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void handleHunterRespawn(PlayerRespawnEvent event) {
        if (!playerMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (playerMap.get(event.getPlayer().getUniqueId()) != PlayStyle.HUNTER) {
            return;
        }
        event.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
    }

    @EventHandler
    public void onHunterDeath(PlayerDeathEvent event) {
        if (!playerMap.containsKey(event.getEntity().getUniqueId())) {
            return;
        }
        if (playerMap.get(event.getEntity().getUniqueId()) != PlayStyle.HUNTER) {
            return;
        }
        event.getDrops().removeIf(itemStack -> itemStack.getType() == Material.COMPASS);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player entity = event.getEntity();
        if (!playerMap.containsKey(entity.getUniqueId())) {
            return;
        }
        PlayStyle style = getPlayStyle(entity);
        if (style != PlayStyle.SPEED_RUNNER) {
            return;
        }
        if (playerMap.entrySet().stream().noneMatch(entry -> {
            Player player1 = Bukkit.getPlayer(entry.getKey());
            return player1 != null && !player1.isDead() && entry.getValue() == PlayStyle.SPEED_RUNNER;
        })) {
            getInstance().getManhuntManager().setStartTime(0L);
            Bukkit.broadcastMessage(ChatColor.RED + "THE HUNTERS WIN!");
            Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0F, 1.0F));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!playerMap.containsKey(event.getPlayer().getUniqueId())) {
            playerMap.put(event.getPlayer().getUniqueId(), PlayStyle.NONE);
        }
        event.getPlayer().setGameMode(playerMap.get(event.getPlayer().getUniqueId()).getGamemode());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (getInstance().getManhuntManager().isStarted()) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onCompassDrop(PlayerDropItemEvent event) {
        if (!playerMap.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }
        if (playerMap.get(event.getPlayer().getUniqueId()) != PlayStyle.HUNTER) {
            return;
        }
        if (event.getItemDrop().getItemStack().getType() != Material.COMPASS) {
            return;
        }
        event.setCancelled(true);
    }

    private Player findNearestRunner(Location location) {
        double closest = 0;
        Player closestPlayer = null;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (playerMap.containsKey(online.getUniqueId()) && playerMap.get(online.getUniqueId()) == PlayStyle.SPEED_RUNNER) {
                double onlineX = online.getLocation().getX();
                double onlineY = online.getLocation().getY();
                double onlineZ = online.getLocation().getZ();

                if (online.getWorld().getEnvironment() == World.Environment.NETHER) {
                    onlineX *= 8;
                    onlineZ *= 8;
                }

                double playerX = location.getX();
                double playerY = location.getY();
                double playerZ = location.getZ();

                if (location.getWorld().getEnvironment() == World.Environment.NETHER) {
                    playerX *= 8;
                    playerZ *= 8;
                }

                double d = NumberConversions.square(playerX - onlineX) + NumberConversions.square(playerY - onlineY) + NumberConversions.square(playerZ - onlineZ);

                if ((closestPlayer == null) || (d < closest)) {
                    closest = d;
                    closestPlayer = online;
                }
            }
        }
        return closestPlayer;
    }

    public void addPlayer(Player player, PlayStyle playStyle) {
        playerMap.put(player.getUniqueId(), playStyle);
    }

    public PlayStyle getPlayStyle(Player player) {
        return playerMap.getOrDefault(player.getUniqueId(), PlayStyle.NONE);
    }

    public Map<UUID, PlayStyle> getPlayerMap() {
        return playerMap;
    }
}
