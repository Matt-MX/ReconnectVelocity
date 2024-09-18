package com.mattmx.reconnect.util.updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {

    private String content;
    private String latest;
    private String link;
    private boolean isLatest;

    public UpdateChecker get(String url) {
        try {
            StringBuilder builder = new StringBuilder();
            URL u = new URI(url).toURL();
            URLConnection connection = u.openConnection();

            String line;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            }

            content = builder.toString();

            JsonElement root = JsonParser.parseString(content);

            if (!root.isJsonObject()) throw new RuntimeException("Unable to get latest version!");

            JsonObject obj = root.getAsJsonObject();

            if (obj.get("tag_name") != null) {
                this.latest = obj.get("tag_name").getAsString();
                this.link = obj.get("html_url").getAsString();
            }

            return this;
        } catch (IOException | URISyntaxException ex) {
            ex.printStackTrace();
        }
        return this;
    }

    public boolean isLatest(String version) {
        isLatest = version.equalsIgnoreCase(latest);
        return isLatest();
    }

    public boolean isLatest() {
        return isLatest;
    }

    public String getLatest() {
        return latest;
    }

    public String getLink() {
        return link;
    }
}

