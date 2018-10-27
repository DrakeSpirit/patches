package me.drakespirit.patches.readers;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RSSReader {

    private final DateTimeFormatter pubDateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private URL rssUrl;

    public RSSReader(String url) {
        try {
            rssUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public List<Item> attemptRead() {
        try {
            return readFeed();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Item> readFeed() throws XMLStreamException, IOException {
        XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
        InputStream in = rssUrl.openStream();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(in);
        List<Item> items = new ArrayList<>();
        Item currentItem = new Item();
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                switch (event.asStartElement().getName().getLocalPart()) {
                    case "item":
                        currentItem = new Item();
                        items.add(currentItem);
                        break;
                    case "title":
                        String title = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setTitle(title);
                        break;
                    case "link":
                        String link = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setLink(link);
                        break;
                    case "description":
                        String description = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setDescription(description);
                        break;
                    case "pubDate":
                        String pubDate = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setPubDate(ZonedDateTime.parse(pubDate, pubDateFormatter));
                        break;
                }
            }
        }
        return items;
    }

    private String getEventCharacters(XMLEvent event) {
        if(event instanceof Characters) {
            return event.asCharacters().getData();
        }
        return "";
    }
}
