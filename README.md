# Patches
A discord bot for posting patchnotes
### Install:
0. Make sure Docker is installed (with docker-compose)
1. Clone or download
2. Add a config.json file to your project directory
3. Add your games and discord webhooks to it (see example below)
4. Run ```gradlew dockerComposeUp```

### Config example:
```json
{
    "warframe": {
        "webhook": "https://discordapp.com/api/webhooks/123456789/y0UrWarframeW3bhOok"
    },
    "guildwars2": {
        "webhook": "https://discordapp.com/api/webhooks/123456789/y0UrGuildWars2W3bhOok"
    },
    "diablo3": {
        "webhook": "https://discordapp.com/api/webhooks/123456789/y0UrDiablo3W3bhOok"
    },
    "factorio": {
        "webhook": "https://discordapp.com/api/webhooks/123456789/y0UrFactorioW3bhOok"
    }
}
```