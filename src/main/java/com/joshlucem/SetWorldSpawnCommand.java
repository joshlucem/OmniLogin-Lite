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
            sender.sendMessage(plugin.getMessages().getString("admin-player-only", "Solo jugadores pueden usar este comando."));
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
    sender.sendMessage(plugin.getMessages().getString("admin-setworldspawn-success", "§aSpawn del mundo configurado correctamente."));
        return true;
    }
}