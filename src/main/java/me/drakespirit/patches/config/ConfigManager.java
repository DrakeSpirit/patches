package me.drakespirit.patches.config;

import org.json.JSONObject;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigManager {

    private static final String JSON_CONFIG_WEBHOOK = "webhook";
    private static final String JSON_CONFIG_LASTUPDATE = "lastUpdate";

    private Map<String, Config> configs = new HashMap<>();
    private File configFile = new File("config.json");

    public ConfigManager() {
        if(configFile.exists()) {
            String jsonString = readJson();
            parseJson(jsonString);
        }
        else {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJson(String jsonString) {
        JSONObject configJson = new JSONObject(jsonString);
        for(String game : configJson.keySet()) {
            JSONObject gameconfig = configJson.getJSONObject(game);
            String webhook = gameconfig.getString(JSON_CONFIG_WEBHOOK);
            ZonedDateTime lastUpdate = null;
            if(gameconfig.has("lastUpdate")) {
                lastUpdate = ZonedDateTime.parse(gameconfig.getString(JSON_CONFIG_LASTUPDATE));
            }
            configs.put(game, new Config(this, webhook, lastUpdate));
        }
    }

    private String readJson() {
        StringBuilder json = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public Optional<Config> getConfig(String game) {
        return Optional.ofNullable(configs.get(game));
    }

    public void saveConfigs() {
        JSONObject json = new JSONObject();
        configs.forEach((game, config) -> {
            JSONObject gameJson = new JSONObject();
            gameJson.put(JSON_CONFIG_WEBHOOK, config.getWebhook());
            if(config.getLastUpdate() != null) {
                gameJson.put(JSON_CONFIG_LASTUPDATE, config.getLastUpdate().toString());
            }
            json.put(game, gameJson);
        });

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(json.toString(4));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
