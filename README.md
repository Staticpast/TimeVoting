# Minecraft Plugin Template

A flexible template for creating Minecraft Spigot/Paper plugins with a clean architecture and best practices.

[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://www.paypal.com/paypalme/mckenzio)

## Features

- Clean, modular architecture with separation of concerns
- Comprehensive API system for integration with other plugins
- Command framework with tab completion
- Configuration management with automatic reloading
- Debug logging system for troubleshooting
- Update checker integration with SpigotMC
- Event system with custom events
- Permission-based command access

## Getting Started

1. Clone or download this template
2. Rename the following:
   - Project name in `pom.xml`
   - Package structure (`io.mckenz.template` → your package)
   - Plugin name and commands in `plugin.yml`
   - Main class name and references

3. Update TODOs:
   - Add your plugin-specific settings in `config.yml`
   - Implement your plugin-specific functionality
   - Add your SpigotMC resource ID for update checking

4. Build your plugin:
   ```
   mvn clean package
   ```

5. Find the JAR file in the `target` directory

## Structure

- **Main Plugin Class** (`PluginTemplate.java`): Core functionality and lifecycle
- **Command System** (`commands/`): Command handlers and tab completers
- **Listeners** (`listeners/`): Event listeners for Bukkit events
- **API** (`api/`): API interfaces for plugin integration
- **Utilities** (`util/`): Utility classes


## Support

If you find this template helpful, consider [buying me a coffee](https://www.paypal.com/paypalme/mckenzio) ☕

## License

[MIT License](LICENSE)

Made with ❤️ by [McKenzieJDan](https://github.com/McKenzieJDan) 