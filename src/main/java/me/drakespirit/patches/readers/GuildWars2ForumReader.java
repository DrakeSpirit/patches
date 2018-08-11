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

public class GuildWars2ForumReader {

    private final DateTimeFormatter pubDateFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    private final String UPDATES_CATEGORY = "/categories/game-release-notes";
    private final String FORUM = "https://en-forum.guildwars2.com";

    public List<Item> attemptRead() {
        List<Item> list = new ArrayList<>();
        try {
            String discussionUrl = getLatestDiscussionUrl();
            Item latest = getLatestPost(discussionUrl);
            list.add(latest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String getLatestDiscussionUrl() throws IOException {
        Document document = Jsoup.connect(FORUM + UPDATES_CATEGORY).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0").get();
        Elements discussions = document.getElementsByClass("Title");
        Element latest = discussions.get(0);
        return latest.attributes().get("href");
    }

    private Item getLatestPost(String discussionUrl) throws IOException {
        Item item = new Item();
        Document document = Jsoup.connect(discussionUrl).userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0").get();

        Elements posts = document.getElementsByClass("Message");
        Element latest = posts.last();
        item.setDescription(latest.html());

        Elements datetimes = document.getElementsByAttribute("datetime");
        String pubTime = datetimes.last().attributes().get("datetime");
        item.setPubDate(ZonedDateTime.parse(pubTime, pubDateFormatter));

        Elements pageTitle = document.getElementsByClass("PageTitle");
        String title = pageTitle.first().getElementsByTag("h1").first().html();
        item.setTitle(title);

        Elements links = document.getElementsByClass("Permalink");
        String link = links.last().attributes().get("href");
        if(link.startsWith("/")) {
            link = FORUM + link;
        }
        item.setLink(link);

        return item;
    }

}
