package me.drakespirit.patches.readers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Diablo3PatchnoteReader {
    
    private final DateTimeFormatter pubDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss Z",Locale.ENGLISH);
    
    private final String URL_BASE = "https://eu.diablo3.com";
    private final String URL_PATCHNOTES = "/en/game/patch-notes/";
    
    public List<Item> attemptRead() {
        List<Item> list = new ArrayList<>();
        try {
            String subpatchUrl = getLatestSubpatchUrl();
            Item latest = getLatestSubpatch(subpatchUrl);
            list.add(latest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private String getLatestSubpatchUrl() throws IOException {
        Document document = Jsoup.connect(URL_BASE + URL_PATCHNOTES).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0").get();
        Elements subpatches = document.getElementsByClass("subpatches-nav");
        Element latest = subpatches.get(0).getElementsByTag("a").last();
        return URL_BASE + latest.attributes().get("href");
    }
    
    private Item getLatestSubpatch(String subpatchUrl) throws IOException {
        Item item = new Item();
        Document document = Jsoup.connect(subpatchUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0").get();
        Element patchnote = document.getElementsByClass("sub-patches").first();
        
        item.setLink(subpatchUrl);
    
        Element title = patchnote.getElementsByClass("subpatch-title").first();
        item.setTitle(title.childNode(0).toString().replaceAll("\n", "").replaceAll("\t", "").trim());
    
        String date = title.getElementsByTag("em").text() + " 00:00:00 +0000";
        item.setPubDate(ZonedDateTime.parse(date, pubDateFormatter));
    
        item.setDescription(patchnote.html());
        
        return item;
    }
    
}
