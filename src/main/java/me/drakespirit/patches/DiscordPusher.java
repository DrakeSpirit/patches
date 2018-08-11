package me.drakespirit.patches;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DiscordPusher {

    public static void push(Patchnote patchnote, String url) {
        List<String> jsons = convertPatchnoteToJson(patchnote);
        jsons.forEach(json -> {
            try {
                sendPost(json, url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static List<String> convertPatchnoteToJson(Patchnote patchnote) {
        String description = patchnote.getDescription().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\R", "\\\\n");
        List<String> descriptionSegments = new ArrayList<>();
        if(description.length() <= 2048) {
            descriptionSegments.add(description);
        }
        else {
            int cursor = 0;
            while(cursor < description.length()) {
                String section = description.substring(cursor, Math.min(cursor + 1800, description.length()));
                int end = section.lastIndexOf("\\n");
                if(end == -1) {
                    end = section.length();
                }
                descriptionSegments.add(section.substring(0, end));
                cursor += end + 2;
            }
        }

        int processed = 0;

        StringBuilder json = new StringBuilder();
        json.append("{\"embeds\":[{\"title\":\"")
            .append(patchnote.getTitle())
            .append("\",\"url\":\"")
            .append(patchnote.getLink())
            .append("\",\"description\":\"")
            .append(descriptionSegments.get(processed))
            .append("\"}");
        processed++;
        for(int i = 0; i < 2 && processed < descriptionSegments.size(); i++, processed++) {
            json.append(",{\"description\":\"")
                .append(descriptionSegments.get(processed))
                .append("\"}");
        }
        json.append("]}");
        List<String> jsons = new ArrayList<>();
        jsons.add(json.toString());

        while(processed < descriptionSegments.size()) {
            json = new StringBuilder();
            json.append("{\"embeds\":[{\"description\":\"")
                    .append(descriptionSegments.get(processed))
                    .append("\"}");
            processed++;
            for (int i = 0; i < 2 && processed < descriptionSegments.size(); i++, processed++) {
                json.append(",{\"description\":\"")
                        .append(descriptionSegments.get(processed))
                        .append("\"}");
            }
            jsons.add(json.toString());
        }

        return jsons;
    }

    private static void sendPost(String json, String webhookURL) throws IOException {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(webhookURL);
            httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");
            StringEntity payload = new StringEntity(json, ContentType.APPLICATION_JSON);
            httpPost.setEntity(payload);

            httpClient.execute(httpPost);
        }
    }

}
