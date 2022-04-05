package com.mattmx.reconnect;

import com.google.inject.Inject;
import com.mattmx.reconnect.listener.Listener;
import com.mattmx.reconnect.util.Config;
import com.mattmx.reconnect.util.ReconnectUtil;
import com.mattmx.reconnect.util.VelocityPlugin;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "reconnect",
        name = "ReconnectVelocity",
        version = "1.0",
        description = "Reconnect your players to their last server...",
        url = "https://www.mattmx.com/",
        authors = {"MattMX"}
)
public class ReconnectVelocity extends VelocityPlugin {
    static ReconnectVelocity instance;

    @Inject
    public ReconnectVelocity(ProxyServer server, Logger logger) {
        this.init(server, logger, "reconnect");
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        Config.init();
        getServer().getEventManager().register(this, new Listener());
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent e) {
        ReconnectUtil.saveData();
    }

    public static ReconnectVelocity get() {
        return instance;
    }
}
