package com.mattmx.reconnect.storage;

import com.mattmx.reconnect.ReconnectVelocity;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import org.jetbrains.annotations.NotNull;

public class LuckPermsStorage extends StorageMethod {
    private static final @NotNull String NODE_NAME = "velocity.reconnect";

    @Override
    public void init() {
        try {
            Class.forName("net.luckperms.api.LuckPermsProvider");
        } catch (ClassNotFoundException exception) {
            ReconnectVelocity.get().getLogger().warn("LuckPerms is not installed!");
            exception.printStackTrace();
        }
    }

    @Override
    public void setLastServer(String uuid, String servername) {
        User user = LuckPermsProvider.get()
            .getUserManager()
            .getUser(uuid);

        if (user == null) return;

        MetaNode node = MetaNode.builder(NODE_NAME, servername)
            .build();

        user.data().clear(NodeType.META.predicate((mn) -> mn.getMetaKey().equals(NODE_NAME)));
        user.data().add(node);

        LuckPermsProvider.get()
            .getUserManager()
            .saveUser(user);
    }

    @Override
    public String getLastServer(String uuid) {

        User user = LuckPermsProvider.get()
            .getUserManager()
            .getUser(uuid);

        if (user == null) return null;

        return user.getCachedData()
            .getMetaData()
            .getMetaValue(NODE_NAME);
    }

    @Override
    public String getMethod() {
        return null;
    }
}