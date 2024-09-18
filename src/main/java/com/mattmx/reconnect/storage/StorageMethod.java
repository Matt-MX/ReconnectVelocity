package com.mattmx.reconnect.storage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class StorageMethod {

    /**
     * Called when we initialize this storage method.
     * Should handle login logic for DB, e.g initializing connection.
     */
    public abstract void init();

    /**
     * Set the last server a player was in.
     *
     * @param uuid the [UUID] of the player as a [String]
     * @param servername the last server's name.
     */
    @Deprecated(forRemoval = true)
    public abstract void setLastServer(String uuid, String servername);
    public void setLastServer(@NotNull UUID uniqueId, @NotNull String serverName) {
        setLastServer(uniqueId.toString(), serverName);
    }

    /**
     * Get the previous server a player was in.
     *
     * @param uuid the [UUID] of the player as a [String]
     * @return The name of the last server a player was in.
     */
    @Deprecated(forRemoval = true)
    public abstract String getLastServer(String uuid);
    public @Nullable String getLastServer(@NotNull UUID uniqueId) {
        return getLastServer(uniqueId.toString());
    }

    /**
     * Get the [StorageMethod]'s String identifier.
     * @return The String identifier for this method.
     */
    @Deprecated
    public abstract String getMethod();
    public String getId() {
        return getMethod();
    }

    /**
     * Called when we want to end this storage method.
     * Use this to close any open connections or save a file.
     */
    public void save() {

    }
}
