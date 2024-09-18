package com.mattmx.reconnect;

import com.mattmx.reconnect.util.ReconnectUtil;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;

public class EventUtils {

    public static boolean isForcedHost(PlayerChooseInitialServerEvent event) {
        Logger logger = ReconnectVelocity.get().getLogger();

        List<String> attemptConnectionOrder = ReconnectVelocity.get()
            .getProxy()
            .getConfiguration()
            .getAttemptConnectionOrder();

        Optional<String> defaultServerOptional = getFirstAvailableServer(attemptConnectionOrder);
        logger.debug("Default server: {}", defaultServerOptional);

        Optional<RegisteredServer> joiningServerOptional = event.getInitialServer();
        logger.debug("Joining server: {}", joiningServerOptional);

        if (joiningServerOptional.isPresent() && defaultServerOptional.isPresent()) {
            if (joiningServerOptional.get().getServerInfo().getName().equals(defaultServerOptional.get())) {
                logger.debug("Player connecting to default server {}, assuming not a forced host",
                    defaultServerOptional.get());
                // TODO: It's possible that the player is connecting to the default server on purpose i.e.
                //  via a forced host. This method won't work in that case.
            } else {
                logger.debug("Player connecting to non-default server {}, is forced host",
                    joiningServerOptional.get().getServerInfo().getName());
                return true;
            }
        }

        return false;
    }

    public static Optional<String> getFirstAvailableServer(List<String> serverList) {
        for (String server : serverList) {
            try {
                final RegisteredServer registeredServer = ReconnectUtil.getServer(server);

                if (registeredServer != null) {
                    registeredServer.ping();
                    return Optional.of(server);
                }

            } catch (CancellationException | CompletionException ignored) {
            }
        }
        return Optional.empty();
    }

}
