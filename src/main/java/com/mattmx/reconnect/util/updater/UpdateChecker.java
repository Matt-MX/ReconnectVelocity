package com.mattmx.reconnect.util.updater;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            URL u = new URL(url);
            URLConnection connection = u.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            content = builder.toString();
            getData();
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    private void getData() {
        if (content == null) return;
        JsonElement root = JsonParser.parseString(content);

        if (root.isJsonObject()) return;

        JsonObject obj = root.getAsJsonObject();

        if (obj.get("tag_name") != null) {
            this.latest = obj.get("tag_name").getAsString();
            this.link = obj.get("html_url").getAsString();
        }
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

