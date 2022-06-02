package com.mattmx.reconnect.util;
import com.mattmx.reconnect.ReconnectVelocity;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    public static YamlConfiguration DEFAULT;
    public static String DEFAULT_PATH = ReconnectVelocity.get().getDataFolder() + "/config.yml";

    public static void init() {
        DEFAULT = get(DEFAULT_PATH, "config.yml");
    }

    public static void save(YamlConfiguration config, String dest) {
        try {
            config.save(dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration get(String path) {
        return get(path, null);
    }

    public static YamlConfiguration get(String path, String def) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (def != null) {
                    ReconnectVelocity.get().saveResource(def, false);
                } else {
                    file.createNewFile();
                }
                ReconnectVelocity.get().logger().info("Created " + path);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        return yml;
    }
}