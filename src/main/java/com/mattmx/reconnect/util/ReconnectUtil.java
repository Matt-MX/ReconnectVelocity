package com.mattmx.reconnect.util;

import com.mattmx.reconnect.ReconnectVelocity;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.IOException;

public class ReconnectUtil {

    public static RegisteredServer getServer(String name) {
        for (RegisteredServer server : ReconnectVelocity.get().getServer().getAllServers()) {
            if (server.getServerInfo().getName().equalsIgnoreCase(name)) {
                return server;
            }
        }
        return null;
    }
}
