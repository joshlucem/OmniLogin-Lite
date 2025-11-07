package com.joshlucem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import java.util.HashMap;
import java.util.Map;

/**
 * Comando /login para OmniLogin-Lite
 */
public class LoginCommand implements CommandExecutor {
    private final Map<String, Integer> attempts = new HashMap<>();
    private final Map<String, Long> blockedUntil = new HashMap<>();
    private final AuthService authService;
    private final OmniLoginLite plugin;

    public LoginCommand(AuthService authService, OmniLoginLite plugin) {
        this.authService = authService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sendMiniMessage(sender, plugin.getMessages().getString("login-player-only", "Solo jugadores pueden usar este comando."), null);
            return true;
        }
        // Leer requisitos configurables
    String firstReq = plugin.getPluginConfig().getString("authentication.login.first-requirement", "<password>");
    String secondReq = plugin.getPluginConfig().getString("authentication.login.second-requirement", "<code>");
       int codeLength = plugin.getPluginConfig().getInt("authentication.register.code-length", 4);
        // Construir lista de requirements activos
        java.util.List<String> requirements = new java.util.ArrayList<>();
        if (!"none".equalsIgnoreCase(firstReq)) requirements.add(firstReq);
        if (!"none".equalsIgnoreCase(secondReq)) requirements.add(secondReq);
        if (args.length != requirements.size()) {
            String firstReqVal = requirements.size() > 0 ? requirements.get(0) : "none";
            String secondReqVal = requirements.size() > 1 ? requirements.get(1) : "none";
            String firstKey = "requirement-" + firstReqVal.replace("<","").replace(">","").toLowerCase();
            String secondKey = "requirement-" + secondReqVal.replace("<","").replace(">","").toLowerCase();
            String firstTranslated = "none".equalsIgnoreCase(firstReqVal) ? "" : plugin.getMessages().getString(firstKey, firstReqVal);
            String secondTranslated = "none".equalsIgnoreCase(secondReqVal) ? "" : plugin.getMessages().getString(secondKey, secondReqVal);
            String helpMsg = plugin.getMessages().getString("help-login", "Uso: /login {first-requirement} {second-requirement}");
            helpMsg = helpMsg.replace("{first-requirement}", firstTranslated).replace("{second-requirement}", secondTranslated).replaceAll(" +", " ").trim();
            sendMiniMessage(sender, helpMsg, null);
            return true;
        }
        // Asignar argumentos según requirements
        Player player = (Player) sender;
        String playerName = player.getName();
        String username = playerName;
        String password = "";
        String code = "";
        int argIndex = 0;
        for (String req : requirements) {
            if ("<password>".equalsIgnoreCase(req)) password = args[argIndex++];
            else if ("<code>".equalsIgnoreCase(req)) code = args[argIndex++];
        }
        // Validar código si es requerido
        if (!"none".equalsIgnoreCase(secondReq)) {
            if (!code.matches("^\\d+$")) {
                sendMiniMessage(sender, plugin.getMessages().getString("code-not-numeric", "§cEl código solo puede contener dígitos numéricos."), null);
                return true;
            }
            if (code.length() != codeLength) {
                Map<String, String> vars = new HashMap<>();
                vars.put("length", String.valueOf(codeLength));
                String msg = plugin.getMessages().getString("code-invalid-length", "§cEl código debe tener exactamente {length} dígitos.");
                sendMiniMessage(sender, msg, vars);
                return true;
            }
        }
        boolean antibot = plugin.getPluginConfig().getBoolean("security.antibot.enabled", true);
        int maxAttempts = plugin.getPluginConfig().getInt("security.antibot.max-attempts", 5);
        int blockSeconds = plugin.getPluginConfig().getInt("security.antibot.block-seconds", 30);
        if (antibot) {
            long now = System.currentTimeMillis();
            if (blockedUntil.containsKey(playerName) && blockedUntil.get(playerName) > now) {
                sendMiniMessage(sender, plugin.getMessages().getString("error-too-many-attempts", "Demasiados intentos fallidos. Espera antes de volver a intentarlo."), null);
                return true;
            }
        }
        Location lastLocation = player.getLocation();
        if (authService.isLoggedIn(username)) {
            sendMiniMessage(sender, plugin.getMessages().getString("already-logged-in", "§eYa has iniciado sesión. ¡Disfruta del juego!"), null);
            return true;
        }
        if (authService.authenticate(username, password)) {
            attempts.remove(playerName);
            blockedUntil.remove(playerName);
                Map<String, String> vars = new HashMap<>();
                vars.put("user", username);
                String welcomeMsg = plugin.getMessages().getString("login-success-welcome", "¡Bienvenido, {user}! Has iniciado sesión correctamente.");
                sendMiniMessage(sender, welcomeMsg, vars);
            // El jugador queda libre tras login exitoso
            // Opcional: teletransportar al spawn solo si es la primera vez
        } else {
            int att = attempts.getOrDefault(playerName, 0) + 1;
            attempts.put(playerName, att);
            if (antibot && att >= maxAttempts) {
                blockedUntil.put(playerName, System.currentTimeMillis() + blockSeconds * 1000L);
                sendMiniMessage(sender, plugin.getMessages().getString("error-too-many-attempts", "Demasiados intentos fallidos. Espera antes de volver a intentarlo."), null);
            } else {
                sendMiniMessage(sender, plugin.getMessages().getString("login-fail", "Credenciales incorrectas."), null);
            }
        }
        return true;
    }

    // Reemplazo: método sendMiniMessage con variables
    private void sendMiniMessage(CommandSender sender, String msg, Map<String, String> variables) {
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
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
            sender.sendMessage(msg.replaceAll("<[^>]+>", ""));
        }
    }
}
