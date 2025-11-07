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
 * Comando /register para OmniLogin-Lite
 */
public class RegisterCommand implements CommandExecutor {
    private final Map<String, Integer> attempts = new HashMap<>();
    private final Map<String, Long> blockedUntil = new HashMap<>();
    private final AuthService authService;
    private final OmniLoginLite plugin;

    public RegisterCommand(AuthService authService, OmniLoginLite plugin) {
        this.authService = authService;
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessages().getString("register-player-only", "Solo jugadores pueden usar este comando."));
            return true;
        }
        // Leer requisitos configurables
        String firstReq = plugin.getPluginConfig().getString("authentication.register.first-requirement", "<user>");
        String secondReq = plugin.getPluginConfig().getString("authentication.register.second-requirement", "<password>");
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
            String helpMsg = plugin.getMessages().getString("help-register", "Por favor regístrate usando: /register {first-requirement} {second-requirement}");
            helpMsg = helpMsg.replace("{first-requirement}", firstTranslated).replace("{second-requirement}", secondTranslated).replaceAll(" +", " ").trim();
            sender.sendMessage(helpMsg);
            return true;
        }
        // Asignar argumentos según requirements
        Player player = (Player) sender;
        String playerName = player.getName();
        String username = playerName;
        String password = "";
        String repeatPassword = "";
        String code = "";
        int argIndex = 0;
        for (String req : requirements) {
            if ("<user>".equalsIgnoreCase(req)) username = args[argIndex++];
            else if ("<password>".equalsIgnoreCase(req)) password = args[argIndex++];
            else if ("<repeatpassword>".equalsIgnoreCase(req)) repeatPassword = args[argIndex++];
            else if ("<code>".equalsIgnoreCase(req)) code = args[argIndex++];
        }
        boolean antibot = plugin.getPluginConfig().getBoolean("security.antibot.enabled", true);
        int maxAttempts = plugin.getPluginConfig().getInt("security.antibot.max-attempts", 5);
        int blockSeconds = plugin.getPluginConfig().getInt("security.antibot.block-seconds", 30);
        if (antibot) {
            long now = System.currentTimeMillis();
            if (blockedUntil.containsKey(playerName) && blockedUntil.get(playerName) > now) {
                sender.sendMessage(plugin.getMessages().getString("error-too-many-attempts", "Demasiados intentos fallidos. Espera antes de volver a intentarlo."));
                return true;
            }
        }
        if (authService.isRegistered(username)) {
            sender.sendMessage(plugin.getMessages().getString("already-registered", "§eYa estás registrado. Si quieres entrar, usa §6/login§e."));
            return true;
        }
        if (authService.register(username, password)) {
            attempts.remove(playerName);
            blockedUntil.remove(playerName);
            authService.authenticate(username, password); // Marcar como loggeado tras registro
            sender.sendMessage(plugin.getMessages().getString("register-success", "Usuario registrado exitosamente."));
            // Ahora el jugador queda libre tras registrarse
        } else {
            int att = attempts.getOrDefault(playerName, 0) + 1;
            attempts.put(playerName, att);
            if (antibot && att >= maxAttempts) {
                blockedUntil.put(playerName, System.currentTimeMillis() + blockSeconds * 1000L);
                sender.sendMessage(plugin.getMessages().getString("error-too-many-attempts", "Demasiados intentos fallidos. Espera antes de volver a intentarlo."));
            } else {
                sender.sendMessage(plugin.getMessages().getString("register-fail", "El usuario ya existe."));
            }
        }
        return true;
    }
}
