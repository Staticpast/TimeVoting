# TimeVoting

A Minecraft Spigot plugin that allows players to vote on changing the current time of day on the server.

[![SpigotMC](https://img.shields.io/badge/SpigotMC-TimeVoting-orange)](https://www.spigotmc.org/resources/timevoting.122960/)
[![Donate](https://img.shields.io/badge/Donate-PayPal-blue.svg)](https://www.paypal.com/paypalme/mckenzio)

## Features

* 🗳️ Players can vote to change the current time in the server
* 📊 Configurable voting threshold based on percentage of online players
* ⏱️ Cooldown system prevents spam voting and frequent time changes
* ⌛ Control how long each time type lasts after being voted in
* 📢 Broadcast announcements when players vote for time changes
* 💬 Fully customizable messages for all plugin text

## Installation

1. Download the latest release from [Spigot](https://www.spigotmc.org/resources/timevoting.122960/) or [GitHub Releases](https://github.com/McKenzieJDan/TimeVoting/releases)
2. Place the JAR file in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in the `config.yml` file

## Usage

Players can vote for their preferred time of day using simple commands. When enough players vote for a specific time, it will change automatically.

### Commands

* `/votetime` - Shows the current vote status
* `/votetime day` - Vote for day time
* `/votetime night` - Vote for night time
* `/votetime sunrise` - Vote for sunrise
* `/votetime sunset` - Vote for sunset
* `/timeforecast` - View the current time and when it will change naturally
* `/timevoting` - Admin commands for managing the plugin

### Permissions

* `timevoting.vote` - Permission to vote for time changes
* `timevoting.forecast` - Permission to use the forecast command
* `timevoting.admin` - Access to all admin commands
* `timevoting.status` - Permission to check plugin status
* `timevoting.toggle` - Permission to enable/disable the plugin
* `timevoting.reload` - Permission to reload the configuration
* `timevoting.debug` - Permission to toggle debug mode
* `timevoting.update` - Permission to receive update notifications

## Configuration

The plugin's configuration file (`config.yml`) is organized into logical sections:

```yaml
# Percentage of online players needed to change the time
voting:
  threshold-percentage: 50
  minimum-players: 2

# Time values in Minecraft ticks
time:
  day: 1000
  night: 13000
  sunrise: 23000
  sunset: 12000
  
  # How long each voted time lasts before returning to normal cycle
  duration: 300

# Cooldown settings to prevent spam
cooldowns:
  between-changes: 300
  between-votes: 60
```

For detailed configuration options, see the comments in the generated config.yml file.

## Requirements

- Spigot/Paper 1.21.5
- Java 21+

## Used By

- [SuegoFaults](https://suegofaults.com) - A collaborative adult Minecraft server where TimeVoting lets players shape the day together without chaos.


## Support

If you find this plugin helpful, consider [buying me a coffee](https://www.paypal.com/paypalme/mckenzio) ☕

## License

[MIT License](LICENSE)

Made with ❤️ by [McKenzieJDan](https://github.com/McKenzieJDan)