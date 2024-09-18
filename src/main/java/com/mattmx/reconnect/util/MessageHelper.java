package com.mattmx.reconnect.util;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MessageHelper {

    public static void sendMessage(@NotNull Player to, @NotNull List<String> msg) {
        to.sendMessage(toComponent(to, msg));
    }

    public static Component toComponent(@NotNull Player to, @NotNull List<String> msg) {
        if (msg.isEmpty()) return Component.empty();

        return msg.stream()
            .map(line -> VelocityChat.color(line, to))
            .reduce((a, b) -> a.append(Component.newline()).append(b))
            .orElse(Component.empty());
    }

}
