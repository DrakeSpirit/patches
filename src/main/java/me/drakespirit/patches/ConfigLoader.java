package me.drakespirit.patches;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConfigLoader {

    private Map<String, Config> configs = new HashMap<>();
    private File configFile = new File("config.json");

    public ConfigLoader() {
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
            String webhook = gameconfig.getString("webhook");
            ZonedDateTime lastUpdate = null;
            if(gameconfig.has("lastUpdate")) {
                lastUpdate = ZonedDateTime.parse(gameconfig.getString("lastUpdate"));
            }
            configs.put(game, new Config(webhook, lastUpdate));
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

}
