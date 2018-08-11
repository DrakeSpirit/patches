package me.drakespirit.patches.pollers;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import me.drakespirit.patches.Config;
import me.drakespirit.patches.DiscordPusher;
import me.drakespirit.patches.Patchnote;
import me.drakespirit.patches.readers.Item;
import me.drakespirit.patches.readers.RSSReader;

import java.util.List;

public class WarframePoller implements Poller {

    private final String RSS_FEED = "https://forums.warframe.com/forum/3-pc-update-build-notes.xml";
    private Config config;
    private RSSReader rssReader;

    public WarframePoller() {
        rssReader = new RSSReader(RSS_FEED);
    }

    @Override
    public void init(Config config) {
        this.config = config;
    }

    @Override
    public void poll() {
        List<Item> feed = rssReader.attemptRead();
        Patchnote patchnote = convertToPatchnote(feed.get(0));
        DiscordPusher.push(patchnote, config.getWebhook());
    }

    private Patchnote convertToPatchnote(Item item) {
        String description = formatDescription(item.getDescription());
        return new Patchnote(item.getTitle(), item.getLink(), description);
    }

    private String formatDescription(String description) {
        Options options = Options.markdown();
        options.inlineLinks = true;
        Remark remark = new Remark(options);
        description = remark.convertFragment(description);

        return description;
    }

}
