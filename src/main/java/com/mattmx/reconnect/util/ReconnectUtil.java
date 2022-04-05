package com.mattmx.reconnect.util;

import com.mattmx.reconnect.ReconnectVelocity;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.io.IOException;

public class ReconnectUtil {

    public static String getLastServer(Player p) {
        return Config.DATA.getString(p.getUniqueId().toString());
    }

    public static void setLastServer(Player p, RegisteredServer server) {
        Config.DATA.set(p.getUniqueId().toString(), server.getServerInfo().getName());
    }

    public static void saveData() {
        try {
            Config.DATA.save(Config.DATA_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RegisteredServer getServer(String name) {
        for (RegisteredServer server : ReconnectVelocity.get().getServer().getAllServers()) {
            if (server.getServerInfo().getName().equalsIgnoreCase(name)) {
                return server;
            }
        }
        return null;
    }
}
