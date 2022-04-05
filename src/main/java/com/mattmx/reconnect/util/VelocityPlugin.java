package com.mattmx.reconnect.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VelocityPlugin {
    private String namespace;
    private Logger logger;
    private ProxyServer server;
    private File dataFolder;

    public File getDataFolder() {
        File dataFolder = this.dataFolder;
        if (dataFolder == null) {
            String path = "plugins/" + namespace + "/";
            try {
                dataFolder = new File(path);
                dataFolder.mkdir();
                return dataFolder;
            } catch (Exception e) {
                return null;
            }
        } else {
            return dataFolder;
        }
    }

    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(this.dataFolder, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(this.dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {

            }
        } catch (IOException ex) {

        }
    }

    public InputStream getResource(@NotNull String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = getClass().getResource(filename);
            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    private HashMap<String, YamlConfiguration> configs = new HashMap<>();

    public YamlConfiguration getConfig(String id) {
        return configs.get(id);
    }

    public void unloadConfigs() {
        configs.clear();
    }

    public void loadConfigs() {

    }

    public void reloadConfigs() {
        unloadConfigs();
        loadConfigs();
    }

    public void createConfig(String path, String defaultLocation) {
        File file = new File(path);
        if (!file.exists()) {
            try {
                if (defaultLocation != null) {
                    this.saveResource(defaultLocation, false);
                } else {
                    file.createNewFile();
                }
                configs.put(path, YamlConfiguration.loadConfiguration(file));
                logger.info("[Config Created] '" + path + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setServer(ProxyServer server) {
        this.server = server;
    }

    public void init(ProxyServer server, Logger logger, String namespace) {
        setLogger(logger);
        setServer(server);
        this.namespace = namespace;
        this.dataFolder = getDataFolder();
    }

    public String getName() {
        return namespace;
    }

    public Logger logger() {
        return this.logger;
    }

    public ProxyServer getServer() {
        return this.server;
    }
}