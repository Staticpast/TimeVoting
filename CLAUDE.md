# Plugin Template Project Guidelines

## Build Commands
```bash
# Build the plugin
mvn clean package

# Install to local Maven repository
mvn install
```

## Code Style Guidelines
- **Packages**: Follow `io.mckenz.template` domain pattern (replace with your own)
- **Imports**: Java imports first, then external libraries (Bukkit/Spigot)
- **Naming**:
  - Classes: PascalCase (PluginTemplate, CommandHandler)
  - Methods/variables: camelCase (isPluginEnabled, configValue)
  - Constants: UPPER_SNAKE_CASE
- **Formatting**: 4-space indentation, braces on same line
- **Documentation**: Javadoc for all public classes/methods
- **Error Handling**: Use try/catch with specific exception logging
- **Commits**: Use conventional commits format - type(scope): message
  - Types: feat, fix, docs, style, refactor, test, chore
  - Scopes: core, api, cmd, event, config

## Technology Stack
- Java 21+
- Spigot/Paper API (Minecraft 1.21.4+)
- Maven for dependency management