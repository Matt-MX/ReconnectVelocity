package com.mattmx.reconnect.listener;

import com.mattmx.reconnect.ReconnectVelocity;
import com.mattmx.reconnect.util.Config;
import com.mattmx.reconnect.util.ReconnectUtil;
import com.mattmx.reconnect.util.VelocityChat;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import litebans.api.Database;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.simpleyaml.configuration.Configuration;
import org.simpleyaml.configuration.file.FileConfiguration;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public class Listener {
    private static boolean litebans = false;

    static {
        if (Config.DEFAULT.getBoolean("litebans")) {
            try {
                Class.forName("litebans.api.Database");
                litebans = true;
            } catch (final ClassNotFoundException exception) {
            }
        }
    }

    @Subscribe
    public void choose(PlayerChooseInitialServerEvent e) {
        if (isForcedHost(e)) return;
        Player player = e.getPlayer();
        String prev = ReconnectVelocity.get().getStorageManager().get().getLastServer(player.getUniqueId().toString());

        if (this.isBanned(player, prev))
            return;

        RegisteredServer server;
        FileConfiguration config = Config.DEFAULT;
        // Check if they have the basic permission node
        if (!player.hasPermission("velocity.reconnect")) return;
        // Check if per-server-premissions is enabled, and check if they have permissions
        if (config.getBoolean("per-server-permissions") && !player.hasPermission("velocity.reconnect." + prev)) return;
        // Check if the server is blacklisted
        if (config.getStringList("blacklist").contains(prev)) return;
        // Not null check
        if (prev != null) {
            // Get the RegisteredServer
            server = ReconnectUtil.getServer(prev);
            // Not null check
            if (server != null) {
                try {
                    // Make sure they can join
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

    private boolean isBanned(final Player p, final String prev) {
        if (!litebans)
            return false;

        return Database.get().isPlayerBanned(p.getUniqueId(), p.getRemoteAddress().getAddress().getHostAddress(), prev);
    }

    private boolean isForcedHost(PlayerChooseInitialServerEvent e) {
        Logger logger = ReconnectVelocity.get().logger();

        List<String> attemptConnectionOrder = ReconnectVelocity.get().getServer()
                .getConfiguration().getAttemptConnectionOrder();
        Optional<String> defaultServerOptional = getFirstAvailableServer(attemptConnectionOrder);
        logger.debug("Default server: {}", defaultServerOptional);

        Optional<RegisteredServer> joiningServerOptional = e.getInitialServer();
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

    private Optional<String> getFirstAvailableServer(List<String> serverList) {
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

    /**
     * Prevents switching to a fallback server if the server is not on the blacklist.
     * Off by default and enabled in the configuration.
     * Also uses alternative message, if available.
     *
     * @param e injected event
     */
    @Subscribe
    public void kicked(KickedFromServerEvent e) {
        Configuration config = Config.DEFAULT;
        if (!config.getBoolean("prevent-fallback", false)) return;

        RegisteredServer server = e.getServer();
        if (config.getStringList("blacklist").contains(server.getServerInfo().getName())) return;

        KickedFromServerEvent.ServerKickResult result = e.getResult();
        if (result instanceof KickedFromServerEvent.RedirectPlayer)  {
            String string = config.getString("prevent-fallback-message", "");
            Component reason = string.isEmpty() ? e.getServerKickReason().orElse(Component.empty())
                    : VelocityChat.color(string);
            e.setResult(KickedFromServerEvent.DisconnectPlayer.create(reason));
        }
    }
}
