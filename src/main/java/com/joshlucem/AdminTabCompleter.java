package com.joshlucem;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminTabCompleter implements TabCompleter {

    private final OmniLoginLite plugin;
    private final List<String> subcommands;

    private List<String> loadSubcommands() {
        List<String> keys = Arrays.asList("setworldspawn", "listusers", "deleteuser", "info", "update", "help");
        List<String> result = new ArrayList<>();
        for (String key : keys) {
            String msgKey = "admin-tab-" + key;
            String custom = plugin.getMessages().getString(msgKey);
            result.add(custom != null && !custom.isEmpty() ? custom : key);
        }
        return result;
    }

    public AdminTabCompleter(OmniLoginLite plugin) {
        this.plugin = plugin;
        this.subcommands = loadSubcommands();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("omnilogin.admin")) {
            return new ArrayList<>();
        }

    if (args.length == 1) {
        return subcommands.stream()
            .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
            .collect(Collectors.toList());
    }

        if (args.length == 2 && args[0].equalsIgnoreCase("deleteuser")) {
            return plugin.getAuthService().getUsernames().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
