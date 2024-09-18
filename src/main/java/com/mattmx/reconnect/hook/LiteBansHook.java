package com.mattmx.reconnect.hook;

import com.mattmx.reconnect.ReconnectVelocity;
import com.velocitypowered.api.proxy.Player;
import litebans.api.Database;
import org.jetbrains.annotations.NotNull;

public class LiteBansHook {

    public static boolean isBannedFromServer(@NotNull Player player, @NotNull String previousServerName) {
        if (!isEnabled()) return false;
        if (!isLiteBansInstalled()) return false;

        return Database.get()
            .isPlayerBanned(player.getUniqueId(), player.getRemoteAddress().getAddress().getHostAddress(), previousServerName);
    }

    public static boolean isEnabled() {
        return ReconnectVelocity.getInstance()
            .getConfig()
            .liteBansHook;
    }

    public static boolean isLiteBansInstalled() {
        try {
            Class.forName("litebans.api.Database");
            return true;
        } catch (final ClassNotFoundException exception) {
            return false;
        }
    }

}
