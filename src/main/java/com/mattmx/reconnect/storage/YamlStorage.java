package com.mattmx.reconnect.storage;

import com.mattmx.reconnect.ReconnectVelocity;
import com.mattmx.reconnect.util.Config;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.IOException;

public class YamlStorage extends StorageMethod {

    public YamlConfiguration data;
    public String dataPath = ReconnectVelocity.get().getDataFolder() + "/data.yml";

    @Override
    public void init() {
        data = Config.get(dataPath);
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
