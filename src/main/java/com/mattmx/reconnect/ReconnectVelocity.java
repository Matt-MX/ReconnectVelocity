package com.mattmx.reconnect;

import com.google.inject.Inject;
import com.mattmx.reconnect.listener.Listener;
import com.mattmx.reconnect.util.Config;
import com.mattmx.reconnect.util.ReconnectUtil;
import com.mattmx.reconnect.util.VelocityPlugin;
import com.mattmx.reconnect.util.storage.*;
import com.mattmx.reconnect.util.updater.UpdateChecker;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

@Plugin(
        id = "reconnect",
        name = "ReconnectVelocity",
        version = "1.1",
        description = "Reconnect your players to their last server...",
        url = "https://www.mattmx.com/",
        authors = {"MattMX"}
)
public class ReconnectVelocity extends VelocityPlugin {
    static ReconnectVelocity instance;
    private StorageManager storage;
    private UpdateChecker checker;

    @Inject
    public ReconnectVelocity(ProxyServer server, Logger logger) {
        this.init(server, logger, "reconnect");
        StorageManager.addMethod(new MySqlStorage());
        StorageManager.addMethod(new SQLiteStorage());
        storage = StorageManager.get(Config.DEFAULT.getString("storage.method"));
        checker = new UpdateChecker();
        if (checker.get("https://api.github.com/repos/Matt-MX/ReconnectVelocity/releases/latest")
                .isLatest(this.getClass().getAnnotation(Plugin.class).version())) {
            logger.info("Running the latest version! ReconnectVelocity " + checker.getLatest());
        } else {
            logger.info("Newer version available! ReconnectVelocity " + checker.getLatest());
            logger.info("Get it here: " + checker.getLink());
        }
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Config.init();
        getServer().getEventManager().register(this, new Listener());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent e) {
        storage.end();
    }

    public static ReconnectVelocity get() {
        return instance;
    }

    public StorageManager getStorageManager() {
        return storage;
    }

    public UpdateChecker getUpdateChecker() {
        return checker;
    }
}
