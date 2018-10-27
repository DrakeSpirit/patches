package me.drakespirit.patches.readers;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AtomReader {

    private DateTimeFormatter pubDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
    private URL atomURL;

    public AtomReader(String url) {
        try {
            atomURL = new URL(url);
            if(pubDateFormatter.getZone() == null) {
                pubDateFormatter = pubDateFormatter.withZone(ZoneId.systemDefault());
            }
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
        InputStream in = atomURL.openStream();
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(in);
        List<Item> items = new ArrayList<>();
        Item currentItem = new Item();
        while (xmlEventReader.hasNext()) {
            XMLEvent event = xmlEventReader.nextEvent();
            if(event.isStartElement()) {
                switch (event.asStartElement().getName().getLocalPart()) {
                    case "entry":
                        currentItem = new Item();
                        items.add(currentItem);
                        break;
                    case "title":
                        String title = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setTitle(title);
                        break;
                    case "link":
                        String link = event.asStartElement().getAttributeByName(new QName("href")).getValue();
                        currentItem.setLink(link);
                        break;
                    case "content":
                        String description = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setDescription(description);
                        break;
                    case "published":
                        String pubDate = getEventCharacters(xmlEventReader.nextEvent());
                        currentItem.setPubDate(ZonedDateTime.parse(pubDate, pubDateFormatter.withZone(ZoneId.systemDefault())));
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
