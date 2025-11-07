# OmniLogin-Lite Classes

This document describes the main classes in the OmniLogin-Lite plugin, their responsibilities, and their relationships.

---

## OmniLoginLite
The core plugin class. Responsible for:
- Initializing the plugin and its configuration
- Registering commands and event listeners
- Managing authentication and session logic via `AuthService`
- Providing access to configuration and message files

## AuthService
Handles all authentication and user management. Responsibilities include:
- Storing and verifying user credentials
- Managing login/logout sessions
- Registering new users
- Deleting users and changing passwords
- Persisting user and session data securely

## AdminTabCompleter
Provides tab completion for admin commands. Responsibilities:
- Suggesting available subcommands
- Suggesting usernames for deletion

## LoginCommand
Handles the `/login` command. Responsibilities:
- Validating login requirements
- Managing anti-bot logic
- Authenticating users and providing feedback

## RegisterCommand
Handles the `/register` command. Responsibilities:
- Validating registration requirements
- Managing anti-bot logic
- Registering new users and providing feedback

## OLoginLAdminCommand
Handles the `/ologinl` admin command. Responsibilities:
- Managing subcommands for admin tasks (set spawn, list users, delete user, info, update, help)
- Interacting with `AuthService` for user management
- Updating plugin configuration

## SetWorldSpawnCommand
Handles the `/ologinl setworldspawn` command. Responsibilities:
- Setting the world spawn location in the plugin configuration

---

### Notes
- All classes follow Bukkit API conventions and are designed for extensibility.
- For method details, see the `Methods.md` file.
- The plugin code is written in Spanish, but documentation is provided in English for convenience.
