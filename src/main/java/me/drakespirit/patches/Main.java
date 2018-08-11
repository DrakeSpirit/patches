package me.drakespirit.patches;


import me.drakespirit.patches.pollers.FactorioPoller;
import me.drakespirit.patches.pollers.GuildWars2Poller;
import me.drakespirit.patches.pollers.Poller;
import me.drakespirit.patches.pollers.WarframePoller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {

    private static List<Poller> pollers = new ArrayList<>();

    public static void main(String[] args) {
        ConfigLoader configLoader = new ConfigLoader();

        initPoller(configLoader, new FactorioPoller(), "factorio");
        initPoller(configLoader, new GuildWars2Poller(), "guildwars2");
        initPoller(configLoader, new WarframePoller(), "warframe");
    }

    private static void initPoller(ConfigLoader configLoader, Poller poller, String game) {
        Optional<Config> gameConfig = configLoader.getConfig(game);
        gameConfig.ifPresent(config -> {
                poller.init(config);
                pollers.add(poller);
        });
    }

}
