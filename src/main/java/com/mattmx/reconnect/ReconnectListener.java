package com.mattmx.reconnect;

import com.mattmx.reconnect.hook.LiteBansHook;
import com.mattmx.reconnect.util.MessageHelper;
import com.mattmx.reconnect.util.VelocityChat;
import com.mattmx.reconnect.util.updater.UpdateChecker;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReconnectListener {

    private final @NotNull ReconnectVelocity plugin;

    public ReconnectListener(@NotNull ReconnectVelocity plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void onChooseInitialServer(@NotNull PlayerChooseInitialServerEvent event) {
        if (EventUtils.isForcedHost(event)) return;

        Player player = event.getPlayer();

        String previousServerName = plugin.getStorageManager()
            .getStorageMethod()
            .getLastServer(player.getUniqueId());

        if (previousServerName == null) return;

        // Check if per-server-permissions is enabled, and check if they have permissions
        if (plugin.getConfig().perServerPermission && !player.hasPermission("velocity.reconnect." + previousServerName))
            return;

        // Check if the server is blacklisted
        if (plugin.getConfig().blacklist.contains(previousServerName)) return;

        RegisteredServer server = plugin.getProxy()
            .getServer(previousServerName)
            .orElse(null);

        // Server was likely unregistered
        if (server == null) return;

        if (LiteBansHook.isBannedFromServer(player, previousServerName)) {
            return;
        }

        try {
            server.ping().get();
        } catch (Exception failure) {

            if (plugin.getConfig().notAvailable) {
                plugin.getProxy()
                    .getScheduler()
                    .buildTask(plugin, () -> MessageHelper.sendMessage(player, plugin.getConfig().notAvailableMessage))
                    .delay(1, TimeUnit.SECONDS)
                    .schedule();
            }

            return;
        }

        event.setInitialServer(server);

        if (plugin.getConfig().messageOnReconnect) {
            plugin.getProxy()
                .getScheduler()
                .buildTask(plugin, () -> MessageHelper.sendMessage(player, plugin.getConfig().reconnectMessage))
                .delay(1, TimeUnit.SECONDS)
                .schedule();
        }
    }

    @Subscribe
    public void onChangeServer(@NotNull ServerConnectedEvent event) {
        ReconnectVelocity.get()
            .getStorageManager()
            .getStorageMethod()
            .setLastServer(event.getPlayer().getUniqueId(), event.getServer().getServerInfo().getName());
    }

    @Subscribe
    public void onPlayerLogin(@NotNull LoginEvent event) {
        if (!plugin.getConfig().checkUpdates) return;

        UpdateChecker checker = plugin.getUpdateChecker();

        if (!event.getPlayer().hasPermission("velocity.reconnect.admin")) return;
        if (checker.isLatest()) return;

        event.getPlayer()
            .sendMessage(VelocityChat.color("<gold><bold>Reconnect</bold> <gray>» <blue>Newer version available! <white>Reconnect v" + checker.getLatest())
                .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, checker.getLink()))
                .hoverEvent(HoverEvent.showText(VelocityChat.color("<gold>Click to update!"))));
    }

    /**
     * Prevents switching to a fallback server if the server is not on the blacklist.
     * Off by default and enabled in the configuration.
     * Also uses alternative message, if available.
     *
     * @param event injected event
     */
    @Subscribe
    public void onPlayerKicked(@NotNull KickedFromServerEvent event) {
        if (!plugin.getConfig().preventFallback) return;

        RegisteredServer server = event.getServer();
        if (plugin.getConfig().blacklist.contains(server.getServerInfo().getName())) return;

        KickedFromServerEvent.ServerKickResult result = event.getResult();
        if (result instanceof KickedFromServerEvent.RedirectPlayer) {
            List<String> stringMsg = plugin.getConfig().preventFallbackMessage;

            Component msg = MessageHelper.toComponent(event.getPlayer(), stringMsg);

            event.setResult(KickedFromServerEvent.DisconnectPlayer.create(msg));
        }
    }
}
