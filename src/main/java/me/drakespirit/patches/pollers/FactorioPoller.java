package me.drakespirit.patches.pollers;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import me.drakespirit.patches.Config;
import me.drakespirit.patches.DiscordPusher;
import me.drakespirit.patches.Patchnote;
import me.drakespirit.patches.readers.Item;
import me.drakespirit.patches.readers.AtomReader;

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
        List<Item> feed = atomReader.attemptRead();
        List<Item> filteredFeed = filterToPatchnotes(feed);
        Patchnote patchnote = convertToPatchnote(filteredFeed.get(0));
        DiscordPusher.push(patchnote, config.getWebhook());
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
        System.out.println(description);
        return description;
    }

}
