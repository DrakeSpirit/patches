package me.drakespirit.patches.config;

import java.time.ZonedDateTime;

public class Config {

    private ConfigManager configManager;
    private String webhook;
    private ZonedDateTime lastUpdate;

    public Config(ConfigManager configManager, String webhook, ZonedDateTime lastUpdate) {
        this.configManager = configManager;
        this.webhook = webhook;
        this.lastUpdate = lastUpdate;
    }

    public String getWebhook() {
        return webhook;
    }

    public boolean isNewer(ZonedDateTime dateTime) {
        if(lastUpdate == null) {
            return true;
        }
        else {
            return lastUpdate.compareTo(dateTime) < 0;
        }
    }

    ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
        configManager.saveConfigs();
    }
}
