package com.mattmx.reconnect.util;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VelocityChat {
    private static LegacyComponentSerializer legacy = LegacyComponentSerializer.builder()
        .hexCharacter('#')
        .character('&')
        .hexColors()
        .build();

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component color(String s, Player p, RegisteredServer server) {
        if (server != null) {
            s = s.replace("%server-name%", server.getServerInfo().getName())
                .replace("%server-connected%", Integer.toString(server.getPlayersConnected().size()))
                .replace("%server-online%", Integer.toString(server.getPlayersConnected().size()))
                .replace("%server-players%", Integer.toString(server.getPlayersConnected().size()));
        }
        return color(s, p);
    }

    public static void clearChat(Player p) {
        for (int i = 0; i < 100; i++) {
            p.sendMessage(Component.newline());
        }
    }

    public static Component color(String s, Player p) {
        s = format(s, p);
        return color(s);
    }

    public static Component color(String s) {
        return miniMessage.deserialize(s);
    }

    public static String format(String s, Player p) {
        s = s.replace("%player%", p.getUsername())
            .replace("%username%", p.getUsername())
            .replace("%server%", p.getCurrentServer().isPresent() ? p.getCurrentServer().get().getServerInfo().getName() : "none")
            .replace("%ping%", "" + p.getPing());
        return s;
    }

    private static final Pattern pattern = Pattern.compile("&#[a-f0-9]{6}|&[a-f0-9k-o]|&r", Pattern.CASE_INSENSITIVE);

    public static String strip(String s) {
        Matcher match = pattern.matcher(s);
        while (match.find()) {
            String color = s.substring(match.start(), match.end());
            s = s.replace(color, "");
            match = pattern.matcher(s);
        }
        return s;
    }
}
