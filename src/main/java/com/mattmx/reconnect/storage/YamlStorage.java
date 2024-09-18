package com.mattmx.reconnect.storage;

import com.mattmx.reconnect.ReconnectVelocity;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlStorage extends StorageMethod {

    public YamlConfiguration data;
    public File dataPath = ReconnectVelocity.get()
        .getDataDirectory()
        .resolve("data.yml")
        .toFile();

    @Override
    public void init() {
        if (!dataPath.exists()) {
            try {
                dataPath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        data = YamlConfiguration.loadConfiguration(dataPath);
    }

    @Override
    public void setLastServer(String uuid, String servername) {
        data.set(uuid, servername);
    }

    @Override
    public String getLastServer(String uuid) {
        return data.getString(uuid);
    }

    @Override
    public void save() {
        try {
            data.save(dataPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMethod() {
        return "yaml";
    }
}
