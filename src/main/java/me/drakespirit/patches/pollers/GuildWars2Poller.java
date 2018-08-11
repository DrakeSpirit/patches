package me.drakespirit.patches.pollers;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import me.drakespirit.patches.config.Config;
import me.drakespirit.patches.DiscordPusher;
import me.drakespirit.patches.Patchnote;
import me.drakespirit.patches.readers.GuildWars2ForumReader;
import me.drakespirit.patches.readers.Item;

import java.io.IOException;
import java.util.List;

public class GuildWars2Poller implements Poller {

    private Config config;
    private GuildWars2ForumReader forumReader;

    public GuildWars2Poller() {
        forumReader = new GuildWars2ForumReader();
    }

    @Override
    public void init(Config config) {
        this.config = config;
    }

    @Override
    public void poll() {
        List<Item> posts = forumReader.attemptRead();
        if(posts.isEmpty()) {
            return;
        }

        Item mostRecent = posts.get(0);
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
