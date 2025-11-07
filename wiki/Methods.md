# OmniLogin-Lite Methods

This document lists and describes the main public methods and command handlers for the OmniLogin-Lite plugin. Each method is briefly explained for clarity and quick reference.

---

## OmniLoginLite
- `parsePlaceholders(Player player, String message)`: Parses PlaceholderAPI placeholders in a message for a player, allowing dynamic content in messages.
- `getAuthService()`: Returns the `AuthService` instance used for authentication and session management.
- `getMessages()`: Returns the plugin's messages configuration for customizing user-facing text.
- `getPluginConfig()`: Returns the main plugin configuration.

## AuthService
- `authenticate(String username, String password)`: Verifies credentials and logs in the user if correct.
- `logout(String username)`: Removes the session for a user, logging them out.
- `isLoggedIn(String username)`: Checks if the user is currently logged in.
- `isRegistered(String username)`: Checks if the user is registered in the system.
- `register(String username, String password)`: Registers a new user with encrypted password.
- `getUsernames()`: Returns a set of all registered usernames.
- `deleteUser(String username)`: Deletes a user and their session from the system.
- `checkPassword(String username, String password)`: Checks if the password matches for a user.
- `changePassword(String username, String newPassword)`: Changes the password for a user.

## AdminTabCompleter
- `onTabComplete(CommandSender sender, Command command, String alias, String[] args)`: Provides tab completion for admin commands, suggesting subcommands and usernames for deletion.

## LoginCommand
- `onCommand(CommandSender sender, Command command, String label, String[] args)`: Handles the `/login` command, including configurable requirements, anti-bot logic, and login process. Returns feedback messages for success or failure.

## RegisterCommand
- `onCommand(CommandSender sender, Command command, String label, String[] args)`: Handles the `/register` command, including configurable requirements, anti-bot logic, and registration process. Returns feedback messages for success or failure.

## OLoginLAdminCommand
- `onCommand(CommandSender sender, Command command, String label, String[] args)`: Handles the `/ologinl` admin command with subcommands for setting spawn, listing users, deleting users, showing plugin info, checking updates, and displaying help.

## SetWorldSpawnCommand
- `onCommand(CommandSender sender, Command command, String label, String[] args)`: Handles the `/ologinl setworldspawn` command to set the world spawn location in the configuration.

---

### Notes
- All command handler methods follow the Bukkit API conventions and return feedback to the user.
- The plugin is designed for extensibility and customization, with most user-facing text configurable via `messages.yml`.
- For more details on usage and configuration, see the main wiki and README files.
