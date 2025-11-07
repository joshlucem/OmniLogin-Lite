package com.joshlucem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Comando /ologinl <setworldspawn|listusers|deleteuser>
 */
public class OLoginLAdminCommand implements CommandExecutor {
	private final AuthService authService;
	private final OmniLoginLite plugin;

	public OLoginLAdminCommand(AuthService authService, OmniLoginLite plugin) {
		this.authService = authService;
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("omnilogin.admin")) {
			sendMiniMessage(sender, plugin.getMessages().getString("admin-no-permission", "§cNo tienes permisos para usar este comando."));
			return true;
		}
		if (args.length == 0) {
			String helpMsg = plugin.getMessages().getString("help-admin",
				"§eComandos disponibles:\n§6/ologinl setworldspawn §7- Establece el spawn actual\n§6/ologinl listusers §7- Lista todos los usuarios registrados\n§6/ologinl deleteuser <usuario> §7- Elimina un usuario\n§6/ologinl info §7- Muestra información del plugin\n§6/ologinl update §7- Verifica si hay actualizaciones\n§6/ologinl help §7- Muestra esta ayuda");
			for (String line : helpMsg.split("\n")) {
				sendMiniMessage(sender, line);
			}
			return true;
		}
		String sub = args[0].toLowerCase();
		switch (sub) {
			case "setworldspawn":
				if (!(sender instanceof Player)) {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-player-only", "Solo jugadores pueden usar este comando."));
					return true;
				}
				Player player = (Player) sender;
				Location loc = player.getLocation();
				World world = loc.getWorld();
				FileConfiguration config = plugin.getPluginConfig();
				config.set("spawn-location.world", world.getName());
				config.set("spawn-location.x", loc.getX());
				config.set("spawn-location.y", loc.getY());
				config.set("spawn-location.z", loc.getZ());
				config.set("spawn-location.yaw", loc.getYaw());
				config.set("spawn-location.pitch", loc.getPitch());
				plugin.saveConfig();
				sendMiniMessage(sender, plugin.getMessages().getString("admin-setworldspawn-success", "§aSpawn del mundo configurado correctamente."));
				break;
			case "listusers":
				sendMiniMessage(sender, plugin.getMessages().getString("admin-listusers-title", "§eUsuarios registrados:"));
				for (String user : authService.getUsernames()) {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-listusers-entry", "§7- {user}").replace("{user}", user));
				}
				break;
			case "deleteuser":
				if (args.length != 2) {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-deleteuser-usage", "§eUso: /ologinl deleteuser <usuario>"));
					return true;
				}
				String username = args[1];
				if (authService.deleteUser(username)) {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-deleteuser-success", "§aUsuario eliminado: {user}").replace("{user}", username));
				} else {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-deleteuser-fail", "§cNo se encontró el usuario: {user}").replace("{user}", username));
				}
				break;
			case "info":
				sendMiniMessage(sender, "<aqua><bold>OmniLogin-Lite</bold></aqua>");
				sendMiniMessage(sender, "<gray>Autor: JoshLucem (@joshlucem)</gray>");
				sendMiniMessage(sender, "<gray>Versión: " + plugin.getDescription().getVersion() + "</gray>");
				break;
			case "update":
				// Simulación de verificación de actualización
				String currentVersion = plugin.getDescription().getVersion();
				String latestVersion = "0.0.2-ALFA"; // Aquí podrías consultar una API externa
				if (currentVersion.equals(latestVersion)) {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-update-none", "§aNo hay actualizaciones pendientes. Versión actual: {version}").replace("{version}", currentVersion));
				} else {
					sendMiniMessage(sender, plugin.getMessages().getString("admin-update-new", "§eHay una nueva versión disponible: {version}").replace("{version}", latestVersion));
				}
				break;
			case "help":
								String helpMsg = plugin.getMessages().getString("help-admin",
									"§eComandos disponibles:\n§6/ologinl setworldspawn §7- Establece el spawn actual\n§6/ologinl listusers §7- Lista todos los usuarios registrados\n§6/ologinl deleteuser <usuario> §7- Elimina un usuario\n§6/ologinl info §7- Muestra información del plugin\n§6/ologinl debug §7- Muestra la configuración actual\n§6/ologinl help §7- Muestra esta ayuda");
								for (String line : helpMsg.split("\n")) {
									sendMiniMessage(sender, line);
								}
				break;
			default:
				sendMiniMessage(sender, plugin.getMessages().getString("admin-unknown-subcommand", "§eSubcomando desconocido. Usa: /ologinl help"));
						break;
				}
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
