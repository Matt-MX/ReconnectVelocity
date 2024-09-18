package com.mattmx.reconnect;

import com.mattmx.reconnect.util.MessageHelper;
import com.mattmx.reconnect.util.VelocityChat;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;

public class ReconnectCommand {

    public static void register(@NotNull ReconnectVelocity plugin) {

        // TODO add debug commands
        LiteralCommandNode<CommandSource> node = BrigadierCommand.literalArgumentBuilder("vreconnect")
            .then(BrigadierCommand.literalArgumentBuilder("reload")
                .requires((source) -> source.hasPermission("velocity.reconnect.reload"))
                .executes((context) -> {

                    context.getSource().sendMessage(VelocityChat.color("<green>Reloading plugin"));
                    plugin.saveDefaultConfig();
                    plugin.loadStorage();
                    context.getSource().sendMessage(VelocityChat.color("<green>Reloaded!"));

                    return Command.SINGLE_SUCCESS;
                })
            ).build();

        CommandMeta meta = plugin.getProxy()
            .getCommandManager()
            .metaBuilder(node.getName())
            .plugin(plugin)
            .build();

        plugin.getProxy()
            .getCommandManager()
            .register(meta, new BrigadierCommand(node));

    }

}
