# Wiki OmniLogin-Lite


> ⚠️ **Notice:** The plugin's code, configuration, and all in-game messages are written in Spanish. The wiki is provided in English for convenience and accessibility.

# OmniLogin-Lite Wiki

## Description
OmniLogin-Lite is an authentication plugin for Minecraft servers (Spigot 1.20+, Java 21), focused on simplicity, security, and customization.

## Installation
1. Requires Java 21 and Spigot 1.20+
2. Place the JAR file in your server's `plugins` folder
3. Start the server to generate configuration files

## Configuration
- Edit `config.yml` to customize registration and login methods:
  ```yaml
  method-register:
    first-requirement: <user>
    second-requirement: <password>
  method-login:
    first-requirement: <user>
    second-requirement: <password>
  ```
- You can use `none` to skip a field:
  ```yaml
  method-login:
    first-requirement: none
    second-requirement: <password>
  ```
  The command will be `/login <password>`.

## Commands
- `/login <username> <password>`
- `/register <username> <password>`
- `/ologinl setworldspawn` — Set the world spawn
- `/ologinl listusers` — List registered users
- `/ologinl deleteuser <username>` — Delete user

## Messages
- Customize all messages in `messages.yml`
- Optional support for PlaceholderAPI

## Security
- Encrypted passwords and JSON persistence
- Configurable antibot protection

## Visual Experience
- Visual reminders and experience bar as countdown
- Automatic kick if not registered/logged in within configured time

## Tab-completer
- Commands only suggest active arguments according to configuration

## Advanced configuration example
```yaml
method-register:
  first-requirement: <user>
  second-requirement: none
method-login:
  first-requirement: <user>
  second-requirement: <password>
```

## Recommendations
- Test the commands and configure messages for your community
- See the README for technical details

---
JoshLucem (@joshlucem) | OmniLogin-Lite

---

## Quick Index
- [Main Classes](Classes.md)
- [Methods](Methods.md)
- [Frequently Asked Questions](FAQ_en.md)


## Main Classes
See [Classes.md](Classes.md) for a description of the main classes:
- `OmniLoginLite`: manages authentication and sessions
- `AuthService`: manages users and login/logout
- Commands: `LoginCommand`, `RegisterCommand`, `OLoginLAdminCommand`, `SetWorldSpawnCommand`, `AdminTabCompleter`


## Methods
See [Methods.md](Methods.md) for key methods:
- `login`, `logout`, `authenticate`, `register`, `isLoggedIn`, `deleteUser`, `changePassword`


## Frequently Asked Questions
See [FAQ.md](FAQ.md) for common questions about security, versions, and usage

