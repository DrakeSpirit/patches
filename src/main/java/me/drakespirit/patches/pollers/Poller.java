package me.drakespirit.patches.pollers;

import me.drakespirit.patches.config.Config;

public interface Poller {

    void init(Config config);
    void poll();

}
