package me.drakespirit.patches;


import me.drakespirit.patches.config.Config;
import me.drakespirit.patches.config.ConfigManager;
import me.drakespirit.patches.pollers.FactorioPoller;
import me.drakespirit.patches.pollers.GuildWars2Poller;
import me.drakespirit.patches.pollers.Poller;
import me.drakespirit.patches.pollers.WarframePoller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {

    private static List<Poller> pollers = new ArrayList<>();

    public static void main(String[] args) {
        ConfigManager configManager = new ConfigManager();

        initPoller(configManager, new FactorioPoller(), "factorio");
        initPoller(configManager, new GuildWars2Poller(), "guildwars2");
        initPoller(configManager, new WarframePoller(), "warframe");

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Main::pollAll, 0, 1, TimeUnit.MINUTES);
    }

    private static void pollAll() {
        pollers.forEach(Poller::poll);
    }

    private static void initPoller(ConfigManager configManager, Poller poller, String game) {
        Optional<Config> gameConfig = configManager.getConfig(game);
        gameConfig.ifPresent(config -> {
                poller.init(config);
                pollers.add(poller);
        });
    }

}
