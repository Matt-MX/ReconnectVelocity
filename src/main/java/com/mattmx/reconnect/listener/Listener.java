package com.mattmx.reconnect.listener;

import com.mattmx.reconnect.ReconnectVelocity;
import com.mattmx.reconnect.util.Config;
import com.mattmx.reconnect.util.ReconnectUtil;
import com.mattmx.reconnect.util.VelocityChat;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class Listener {

    @Subscribe
    public void choose(PlayerChooseInitialServerEvent e) {
        Player player = e.getPlayer();
        String prev = ReconnectUtil.getLastServer(player);
        RegisteredServer server;
        FileConfiguration config = Config.DEFAULT;
        if (!player.hasPermission("velocity.reconnect")) return;
        if (prev != null) {
            server = ReconnectUtil.getServer(prev);
            if (server != null) {
                try {
                    server.ping().join();
                } catch (CancellationException | CompletionException exception) {
                    if (config.getBoolean("not-available")) {
                        ReconnectVelocity.get().getServer().getScheduler().buildTask(ReconnectVelocity.get(), () -> {
                            config.getStringList("not-available-message").forEach(l -> player.sendMessage(VelocityChat.color(l, player)));
                        }).delay(1, TimeUnit.SECONDS).schedule();

                    }
                    return;
                }
                e.setInitialServer(server);
                if (config.getBoolean("message-on-reconnect") && !prev.equalsIgnoreCase(config.getString("fallback"))) {
                    ReconnectVelocity.get().getServer().getScheduler().buildTask(ReconnectVelocity.get(), () -> {
                        config.getStringList("reconnect-message").forEach(l -> player.sendMessage(VelocityChat.color(l, player)));
                    }).delay(1, TimeUnit.SECONDS).schedule();
                }
            }
        }
    }

    @Subscribe
    public void change(ServerConnectedEvent e) {
        ReconnectUtil.setLastServer(e.getPlayer(), e.getServer());
    }
}
