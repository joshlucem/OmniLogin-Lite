package com.joshlucem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Comando para establecer el spawn del mundo en config.yml
 */
public class SetWorldSpawnCommand implements CommandExecutor {
    private final OmniLoginLite plugin;

    public SetWorldSpawnCommand(OmniLoginLite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMiniMessage(sender, plugin.getMessages().getString("admin-player-only", "Solo jugadores pueden usar este comando."));
            return true;
        }
        Player player = (Player) sender;
        Location loc = player.getLocation();
        World world = loc.getWorld();
        FileConfiguration config = plugin.getPluginConfig();
        config.set("spawn.world", world.getName());
        config.set("spawn.x", loc.getX());
        config.set("spawn.y", loc.getY());
        config.set("spawn.z", loc.getZ());
        config.set("spawn.yaw", loc.getYaw());
        config.set("spawn.pitch", loc.getPitch());
        plugin.saveConfig();
    sendMiniMessage(sender, plugin.getMessages().getString("admin-setworldspawn-success", "§aSpawn del mundo configurado correctamente."));
        return true;
    }

    private void sendMiniMessage(CommandSender sender, String msg) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String parsedMsg = plugin.parsePlaceholders(player, msg);
            try {
                player.getClass().getMethod("sendMessage", net.kyori.adventure.text.Component.class)
                        .invoke(player, plugin.getMiniMessage().deserialize(parsedMsg));
            } catch (Exception e) {
                player.sendMessage(parsedMsg);
            }
        } else {
            sender.sendMessage(msg.replaceAll("<[^>]+>", "")); // Remove MiniMessage tags for console
        }
    }
}