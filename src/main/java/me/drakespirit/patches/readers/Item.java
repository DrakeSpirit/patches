package me.drakespirit.patches.readers;

import java.time.ZonedDateTime;

public class Item {

    private String title;
    private String link;
    private String description;
    private ZonedDateTime pubDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getPubDate() {
        return pubDate;
    }

    public void setPubDate(ZonedDateTime pubDate) {
        this.pubDate = pubDate;
    }

    @Override
    public String toString() {
        return "title: " + title + "\nlink: " + link + "\npubDate: " + pubDate + "\ndescription:\n" + description;
    }
}
