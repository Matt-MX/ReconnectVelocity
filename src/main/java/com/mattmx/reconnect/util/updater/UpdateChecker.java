package com.mattmx.reconnect.util.updater;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UpdateChecker {

    private boolean validData;
    private String content;
    private String latest;
    private String link;
    private boolean isLatest;

    /**
     * Instantiates a new UpdateChecker instance to check the given URL against the current plugin version.
     *
     * @param url update check URL
     * @param currentVersion current plugin version
     * @return UpdateChecker instance
     */
    public UpdateChecker get(String url, String currentVersion) {
        try {
            StringBuilder builder = new StringBuilder();
            URL u = new URL(url);
            URLConnection connection = u.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            content = builder.toString();
            getData();
            validData = true;
            isLatest = currentVersion.equalsIgnoreCase(this.latest);
            return this;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Fetches the latest version and download URL from the internet
     */
    private void getData() {
        if (content == null) return;
        JSONObject obj = new JSONObject(content);
        if (obj.get("tag_name") != null) {
            this.latest = obj.getString("tag_name");
            this.link = obj.getString("html_url");
        }
    }

    /**
     * Returns true if the running the latest version.
     * Additionally, returns true if unable to find a later version.
     *
     * @return if this is the latest version
     */
    public boolean isLatest() {
        return !validData || isLatest;
    }

    /**
     * Returns true if the update checker was able to successfully check for updates.
     *
     * @return if able to check for updates
     */
    public boolean isValid() {
        return validData;
    }

    /**
     * Returns the name of the latest version
     *
     * @return latest version
     */
    public String getLatest() {
        return validData ? latest : "";
    }

    /**
     * Returns the download link to the latest version
     *
     * @return download URL
     */
    public String getLink() {
        return validData ? link : "";
    }
}

