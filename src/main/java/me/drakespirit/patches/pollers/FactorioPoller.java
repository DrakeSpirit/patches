package me.drakespirit.patches.pollers;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import me.drakespirit.patches.config.Config;
import me.drakespirit.patches.DiscordPusher;
import me.drakespirit.patches.Patchnote;
import me.drakespirit.patches.readers.Item;
import me.drakespirit.patches.readers.AtomReader;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FactorioPoller implements Poller {

    private final String ATOM_FEED = "https://forums.factorio.com/feed.php?mode=news";
    private Config config;
    private AtomReader atomReader;

    public FactorioPoller() {
        atomReader = new AtomReader(ATOM_FEED);
    }

    @Override
    public void init(Config config) {
        this.config = config;
    }

    @Override
    public void poll() {
        List<Item> feed = filterToPatchnotes(atomReader.attemptRead());
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

    private List<Item> filterToPatchnotes(List<Item> feed) {
        return feed.stream().filter(item -> item.getTitle().startsWith("Version")).collect(Collectors.toList());
    }

    private Patchnote convertToPatchnote(Item item) {
        String description = formatDescription(item.getDescription());
        return new Patchnote(item.getTitle(), item.getLink(), description);
    }

    private String formatDescription(String description) {
        int end = description.lastIndexOf("<p>Statistics: Posted by");
        Options options = Options.markdown();
        options.inlineLinks = true;
        Remark remark = new Remark(options);
        description = remark.convertFragment(description.substring(0, end));
        return description;
    }

}
