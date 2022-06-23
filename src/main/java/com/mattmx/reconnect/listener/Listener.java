package com.mattmx.reconnect.listener;

import com.mattmx.reconnect.ReconnectVelocity;
import com.mattmx.reconnect.util.Config;
import com.mattmx.reconnect.util.ReconnectUtil;
import com.mattmx.reconnect.util.VelocityChat;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.audience.MessageType;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class Listener {

    @Subscribe
    public void choose(PlayerChooseInitialServerEvent e) {
        Player player = e.getPlayer();
        String prev = ReconnectVelocity.get().getStorageManager().get().getLastServer(player.getUniqueId().toString());
        RegisteredServer server;
        FileConfiguration config = Config.DEFAULT;
        if (!player.hasPermission("velocity.reconnect")) return;
        if (prev != null) {
            server = ReconnectUtil.getServer(prev);
            if (server != null) {
                try {
                    server.ping();
                } catch (CancellationException | CompletionException exception) {
                    if (config.getBoolean("not-available")) {
                        ReconnectVelocity.get().getServer().getScheduler().buildTask(ReconnectVelocity.get(), () -> {
                            config.getStringList("not-available-message").forEach(l -> player.sendMessage(VelocityChat.color(l, player), MessageType.SYSTEM));
                        }).delay(1, TimeUnit.SECONDS).schedule();
                    }
                    return;
                }
                e.setInitialServer(server);
                if (config.getBoolean("message-on-reconnect") && !prev.equalsIgnoreCase(config.getString("fallback"))) {
                    ReconnectVelocity.get().getServer().getScheduler().buildTask(ReconnectVelocity.get(), () -> {
                        config.getStringList("reconnect-message").forEach(l -> player.sendMessage(VelocityChat.color(l, player) ,MessageType.SYSTEM));
                    }).delay(1, TimeUnit.SECONDS).schedule();
                }
            }
        }
    }

    @Subscribe
    public void change(ServerConnectedEvent e) {
        ReconnectVelocity.get().getStorageManager().get().setLastServer(e.getPlayer().getUniqueId().toString(), e.getServer().getServerInfo().getName());
    }

    @Subscribe
    public void login(LoginEvent e) {
        if (e.getPlayer().hasPermission("velocity.reconnect.admin")) {
            if (!ReconnectVelocity.get().getUpdateChecker().isLatest()) {
                e.getPlayer().sendMessage(VelocityChat.color("&6&lReconnect &7» &9Newer version available! &fReconnect v" + ReconnectVelocity.get().getUpdateChecker().getLatest())
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, ReconnectVelocity.get().getUpdateChecker().getLink()))
                        .hoverEvent(HoverEvent.showText(VelocityChat.color("&6Click to update!"))), MessageType.SYSTEM);
            }
        }
    }
}
