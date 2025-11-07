package com.joshlucem;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.json.simple.JSONArray;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servicio de autenticación para OmniLogin-Lite
 */
public class AuthService {
    public java.util.Set<String> getUsernames() {
        return users.keySet();
    }

    public boolean deleteUser(String username) {
        if (!users.containsKey(username)) return false;
        users.remove(username);
        saveUsers();
        sessions.remove(username);
        saveSessions();
        return true;
    }
    public boolean checkPassword(String username, String password) {
        String hash = hashPassword(password);
        return users.containsKey(username) && users.get(username).equals(hash);
    }

    public boolean changePassword(String username, String newPassword) {
        if (!users.containsKey(username)) return false;
        String hash = hashPassword(newPassword);
        users.put(username, hash);
        saveUsers();
        return true;
    }
    private final Map<String, String> users = new HashMap<>();
    private final Map<String, Boolean> sessions = new HashMap<>();
    private final File usersFile;
    private final File sessionsFile;
    private final JavaPlugin plugin;

    public AuthService(JavaPlugin plugin) {
        this.plugin = plugin;
        File dataDir = new File(plugin.getDataFolder(), "data");
        if (!dataDir.exists()) dataDir.mkdirs();
        this.usersFile = new File(dataDir, "users.json");
        this.sessionsFile = new File(dataDir, "sessions.json");
        loadUsers();
        loadSessions();
    }

    public boolean authenticate(String username, String password) {
        String hash = hashPassword(password);
        if (users.containsKey(username) && users.get(username).equals(hash)) {
            sessions.put(username, true);
            saveSessions();
            return true;
        }
        return false;
    }

    public void logout(String username) {
        sessions.remove(username);
        saveSessions();
    }

    public boolean isLoggedIn(String username) {
        return sessions.getOrDefault(username, false);
    }

    public boolean isRegistered(String username) {
        return users.containsKey(username);
    }
    @SuppressWarnings("unchecked")
    private void saveSessions() {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, Boolean> entry : sessions.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }
        try (FileWriter file = new FileWriter(sessionsFile)) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar sessions.json: " + e.getMessage());
        }
    }

    private void loadSessions() {
        if (!sessionsFile.exists()) return;
        try (FileReader reader = new FileReader(sessionsFile)) {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(reader);
            for (Object key : obj.keySet()) {
                sessions.put((String) key, (Boolean) obj.get(key));
            }
        } catch (IOException | ParseException e) {
            plugin.getLogger().warning("No se pudo cargar sessions.json: " + e.getMessage());
        }
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) return false;
        String hash = hashPassword(password);
        users.put(username, hash);
        saveUsers();
        return true;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            plugin.getLogger().warning("No se pudo encriptar la contraseña: " + e.getMessage());
            return password;
        }
    }
    @SuppressWarnings("unchecked")
    private void saveUsers() {
        JSONObject obj = new JSONObject();
        for (Map.Entry<String, String> entry : users.entrySet()) {
            obj.put(entry.getKey(), entry.getValue());
        }
        try (FileWriter file = new FileWriter(usersFile)) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            plugin.getLogger().warning("No se pudo guardar users.json: " + e.getMessage());
        }
    }

    private void loadUsers() {
        if (!usersFile.exists()) return;
        try (FileReader reader = new FileReader(usersFile)) {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(reader);
            for (Object key : obj.keySet()) {
                users.put((String) key, (String) obj.get(key));
            }
        } catch (IOException | ParseException e) {
            plugin.getLogger().warning("No se pudo cargar users.json: " + e.getMessage());
        }
    }
}
