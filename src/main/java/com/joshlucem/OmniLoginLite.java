package com.joshlucem;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import java.io.InputStreamReader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.util.Map;
import java.util.HashMap;

/**
 * OmniLogin-Lite v0.0.1-ALFA
 * Autor: JoshLucem (@joshlucem)
 * Plugin de autenticación simple y seguro para Java 21.
 */
public class OmniLoginLite extends JavaPlugin {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
    // Contadores separados para login y registro
    private final Map<String, Integer> loginWaitCounter = new HashMap<>();
    private final Map<String, Integer> registerWaitCounter = new HashMap<>();
    private AuthService authService;
    private FileConfiguration config;
    private FileConfiguration messages;
    // Utilidad para procesar placeholders si PlaceholderAPI está presente
    public String parsePlaceholders(Player player, String message) {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            try {
                Class<?> clazz = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                java.lang.reflect.Method method = clazz.getMethod("setPlaceholders", Player.class, String.class);
                Object result = method.invoke(null, player, message);
                message = (String) result;
            } catch (Exception e) {
                getLogger().warning("Error procesando PlaceholderAPI: " + e.getMessage());
            }
        }
        return message;
    }

    // TabCompleter dinámico para login/register
    public static class DynamicTabCompleter implements org.bukkit.command.TabCompleter {
        private final OmniLoginLite plugin;
        private final String methodKey;
        public DynamicTabCompleter(OmniLoginLite plugin, String methodKey) {
            this.plugin = plugin;
            this.methodKey = methodKey;
        }
        @Override
        public java.util.List<String> onTabComplete(org.bukkit.command.CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
            java.util.List<String> suggestions = new java.util.ArrayList<>();
            java.util.List<String> requirements = new java.util.ArrayList<>();

            String firstReq = plugin.getPluginConfig().getString("authentication." + methodKey + ".first-requirement", "<user>");
            String secondReq = plugin.getPluginConfig().getString("authentication." + methodKey + ".second-requirement", "<password>");

            if (!"none".equalsIgnoreCase(firstReq)) requirements.add(firstReq);
            if (!"none".equalsIgnoreCase(secondReq)) requirements.add(secondReq);

            int currentArgIndex = args.length - 1;

            if (currentArgIndex >= 0 && currentArgIndex < requirements.size()) {
                String requirement = requirements.get(currentArgIndex);

                if (!"none".equalsIgnoreCase(requirement)) {
                    String key = "requirement-" + requirement.replace("<", "").replace(">", "").toLowerCase();
                    String translated = plugin.getMessages().getString(key, requirement);
                    String currentInput = args[currentArgIndex].toLowerCase();

                    if (translated.toLowerCase().startsWith(currentInput)) {
                        suggestions.add(translated);
                    }
                }
            }
            
            return suggestions;
        }
    }

    @Override
    public void onEnable() {
        this.authService = new AuthService(this);
        saveDefaultConfig();
        this.config = getConfig();
        this.messages = YamlConfiguration.loadConfiguration(new InputStreamReader(getResource("messages.yml")));
        getCommand("login").setExecutor(new LoginCommand(authService, this));
        getCommand("login").setTabCompleter(new DynamicTabCompleter(this, "login"));
        getCommand("register").setExecutor(new RegisterCommand(authService, this));
        getCommand("register").setTabCompleter(new DynamicTabCompleter(this, "register"));
        Bukkit.getPluginManager().registerEvents(new PlayerBlockerListener(this), this);
        getCommand("ologinl").setExecutor(new OLoginLAdminCommand(authService, this));
        getCommand("ologinl").setTabCompleter(new AdminTabCompleter(this));
    getLogger().info("[OmniLogin-Lite] Plugin activado ✅ | Versión: " + getDescription().getVersion());
    getLogger().info("[OmniLogin-Lite] Autor: JoshLucem | GitHub: joshlucem/OmniLogin-Lite");
    }

    @Override
    public void onDisable() {
    getLogger().info("[OmniLogin-Lite] Plugin desactivado ❌ | Versión: " + getDescription().getVersion());
    getLogger().info("[OmniLogin-Lite] ¡Gracias por usar OmniLogin-Lite!");
    }

    public AuthService getAuthService() {
        return authService;
    }

    public FileConfiguration getMessages() {
        return messages;
    }
    public FileConfiguration getPluginConfig() {
        return config;
    }
    // Listener para bloquear movimiento y acciones
    public static class PlayerBlockerListener implements Listener {
        @EventHandler
        public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
            Player player = event.getPlayer();
            plugin.getAuthService().logout(player.getName());
        }
        private final OmniLoginLite plugin;
        public PlayerBlockerListener(OmniLoginLite plugin) {
            this.plugin = plugin;
        }
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            if (!plugin.getAuthService().isLoggedIn(player.getName())) {
                event.setCancelled(true);
            }
        }
        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            Player player = event.getPlayer();
            if (!plugin.getAuthService().isLoggedIn(player.getName())) {
                event.setCancelled(true);
            }
        }
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (!plugin.getAuthService().isLoggedIn(player.getName())) {
                int maxWaitLogin = plugin.getPluginConfig().getInt("visual-experience.max-wait-login", 45);
                int maxWaitRegister = plugin.getPluginConfig().getInt("visual-experience.max-wait-register", 45);
                plugin.loginWaitCounter.put(player.getName(), maxWaitLogin);
                plugin.registerWaitCounter.put(player.getName(), maxWaitRegister);
                startReminder(player, plugin);
            }
        }

        private void startReminder(Player player, OmniLoginLite plugin) {
            int maxWaitLogin = plugin.getPluginConfig().getInt("visual-experience.max-wait-login", 45);
            int maxWaitRegister = plugin.getPluginConfig().getInt("visual-experience.max-wait-register", 45);
            String playerName = player.getName();

            plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                if (plugin.getAuthService().isLoggedIn(playerName)) {
                    player.setLevel(0);
                    plugin.loginWaitCounter.remove(playerName);
                    plugin.registerWaitCounter.remove(playerName);
                    return;
                }

                boolean isRegistered = plugin.getAuthService().isRegistered(playerName);
                int timeLeft;
                if (isRegistered) {
                    timeLeft = plugin.loginWaitCounter.getOrDefault(playerName, maxWaitLogin);
                } else {
                    timeLeft = plugin.registerWaitCounter.getOrDefault(playerName, maxWaitRegister);
                }

                if (timeLeft <= 0) {
                    String kickMsg = plugin.getMessages().getString("kick-message", "Tiempo de espera agotado. Debes registrarte o iniciar sesión.");
                    try {
                        player.getClass().getMethod("kick", net.kyori.adventure.text.Component.class)
                            .invoke(player, plugin.getMiniMessage().deserialize(kickMsg));
                    } catch (Exception e) {
                        player.kickPlayer(kickMsg);
                    }
                    plugin.loginWaitCounter.remove(playerName);
                    plugin.registerWaitCounter.remove(playerName);
                    return;
                }

                player.setLevel(timeLeft);
                // Solo enviar recordatorio cada 5 segundos
                if (timeLeft % 5 == 0) {
                    String reminder;
                    if (isRegistered) {
                        reminder = plugin.getMessages().getString("reminder-login", "<yellow>Recuerda: debes iniciar sesión para jugar.</yellow>");
                    } else {
                        reminder = plugin.getMessages().getString("reminder-register", "<yellow>Recuerda: debes registrarte para jugar.</yellow>");
                    }
                    Component component = plugin.getMiniMessage().deserialize(reminder);
                    // Enviar correctamente el mensaje según API disponible
                    try {
                        // Paper API: Player#sendMessage(Component)
                        player.getClass().getMethod("sendMessage", Component.class).invoke(player, component);
                    } catch (Exception e) {
                        // Fallback: enviar como string plano si no está disponible
                        player.sendMessage(reminder);
                    }
                }
                // Decrementar solo el contador relevante
                if (isRegistered) {
                    plugin.loginWaitCounter.put(playerName, timeLeft - 1);
                } else {
                    plugin.registerWaitCounter.put(playerName, timeLeft - 1);
                }
            }, 0L, 20L);
        }
    }
}
