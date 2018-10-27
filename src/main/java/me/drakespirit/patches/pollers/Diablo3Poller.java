package me.drakespirit.patches.pollers;

import com.overzealous.remark.Options;
import com.overzealous.remark.Remark;
import me.drakespirit.patches.DiscordPusher;
import me.drakespirit.patches.Patchnote;
import me.drakespirit.patches.config.Config;
import me.drakespirit.patches.readers.Diablo3PatchnoteReader;
import me.drakespirit.patches.readers.Item;

import java.io.IOException;
import java.util.List;

public class Diablo3Poller implements Poller {
    
    private Config config;
    private Diablo3PatchnoteReader patchnoteReader;
    
    public Diablo3Poller() {
        patchnoteReader = new Diablo3PatchnoteReader();
    }
    
    @Override
    public void init(Config config) {
        this.config = config;
    }
    
    @Override
    public void poll() {
        System.out.println("Diablo 3: Polling...");
        
        List<Item> posts = patchnoteReader.attemptRead();
        if(posts.isEmpty()) {
            System.out.println("Diablo 3: No notes found.");
            return;
        }
    
        Item mostRecent = posts.get(0);
        if(config.isNewer(mostRecent.getPubDate())) {
            System.out.println("Diablo 3: New patchnote found, pushing to Discord.");
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
