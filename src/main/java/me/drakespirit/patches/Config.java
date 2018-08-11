package me.drakespirit.patches;

import java.time.ZonedDateTime;

public class Config {

    private String webhook;
    private ZonedDateTime lastUpdate;

    public Config(String webhook, ZonedDateTime lastUpdate) {
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

    public void setLastUpdate(ZonedDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
