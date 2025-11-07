# OmniLogin-Lite FAQ

Frequently Asked Questions about the OmniLogin-Lite plugin.

---

## Is it safe for production?
No, this is an ALPHA version intended for testing and development. It is not recommended for production environments.

## How do I add users?
Use the `/register` command in-game or the `register` method from the `AuthService` class to add new users.

## How are passwords stored?
Passwords are encrypted using SHA-256 and stored securely in a JSON file.

## What Java versions are supported?
The plugin is compatible with Java 21 and any Minecraft server supporting Spigot 1.20+.

## Can I customize messages and commands?
Yes, all user-facing messages can be customized in the `messages.yml` file. Command requirements can be configured in `config.yml`.

## Is there anti-bot protection?
Yes, the plugin includes configurable anti-bot protection to limit failed login and registration attempts.

## What language is the code written in?
The plugin code and in-game messages are written in Spanish. Documentation is provided in English for convenience.

## Where can I find more details?
See the main wiki, README, and configuration files for further information and advanced usage.

---
