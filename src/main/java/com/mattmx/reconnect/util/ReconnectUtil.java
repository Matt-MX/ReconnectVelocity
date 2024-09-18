package com.mattmx.reconnect.util;

import com.mattmx.reconnect.ReconnectVelocity;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReconnectUtil {

    public static @Nullable RegisteredServer getServer(@NotNull String name) {
        return ReconnectVelocity.getInstance()
            .getProxy()
            .getAllServers()
            .stream()
            .filter((s) -> s.getServerInfo().getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
}
