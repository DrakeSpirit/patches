package me.drakespirit.patches.pollers;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import me.drakespirit.patches.config.Config;
import me.drakespirit.patches.DiscordPusher;
import me.drakespirit.patches.Patchnote;
import me.drakespirit.patches.readers.Item;
import me.drakespirit.patches.readers.RSSReader;

import java.io.IOException;
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
        if(feed.isEmpty()) {
            return;
        }

        Item mostRecent = feed.get(0);
        if(config.isNewer(mostRecent.getPubDate())) {
            Patchnote patchnote = convertToPatchnote(mostRecent);
            try {
                DiscordPusher.push(patchnote, config.getWebhook());
                config.setLastUpdate(mostRecent.getPubDate());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
