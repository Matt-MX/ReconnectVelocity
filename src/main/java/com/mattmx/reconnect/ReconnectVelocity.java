package com.mattmx.reconnect;

import com.google.inject.Inject;
import com.mattmx.reconnect.storage.*;
import com.mattmx.reconnect.util.Config;
import com.mattmx.reconnect.util.updater.UpdateChecker;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Plugin(
    id = "reconnect",
    name = "ReconnectVelocity",
    version = "2.0",
    description = "Reconnect your players to their last server...",
    url = "https://www.mattmx.com/",
    authors = {"MattMX"},
    dependencies = {
        @Dependency(id = "litebans", optional = true),
        @Dependency(id = "luckperms", optional = true)
    }
)
public class ReconnectVelocity {
    private static @Nullable ReconnectVelocity instance;
    private final @Nullable ProxyServer proxy;
    private final @Nullable Logger logger;
    private final @Nullable Path dataDirectory;

    private @Nullable ReconnectConfig config;
    private @Nullable StorageManager storage;
    private UpdateChecker checker;

    @Inject
    public ReconnectVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        instance = this;

        saveDefaultConfig();

        Config.init();

        StorageManager.registerStorageMethod(new MySqlStorage());
        StorageManager.registerStorageMethod(new MariaDbStorage());
        StorageManager.registerStorageMethod(new SQLiteStorage());

        loadStorage();

        checker = new UpdateChecker();

        if (checker.get("https://api.github.com/repos/Matt-MX/ReconnectVelocity/releases/latest")
            .isLatest(this.getClass().getAnnotation(Plugin.class).version())) {
            logger.info("Running the latest version! ReconnectVelocity " + checker.getLatest());
        } else {
            logger.info("Newer version available! ReconnectVelocity " + checker.getLatest());
            logger.info("Get it here: " + checker.getLink());
        }
    }

    public void loadStorage() {
        StorageMethod method = StorageManager.getStorageMethodById(getConfig().storage.method);

        Objects.requireNonNull(method, "That storage method is invalid!");

        storage = StorageManager.createStorageManager(method);
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public void saveDefaultConfig() {
        File filePath = getDataDirectory()
            .resolve("config.toml")
            .toFile();

        if (!filePath.exists()) {
            filePath.getParentFile().mkdirs();
            try {
                filePath.createNewFile();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(new TomlWriter().write(new ReconnectConfig()));
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        this.config = new Toml()
            .read(filePath)
            .to(ReconnectConfig.class);
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        getProxy().getEventManager().register(this, new ReconnectListener(this));
    }

    @Subscribe
    public void onDisable(ProxyShutdownEvent event) {
        getStorageManager().end();
    }

    public static ReconnectVelocity get() {
        return instance;
    }

    public @NotNull StorageManager getStorageManager() {
        return Objects.requireNonNull(storage);
    }

    public static @NotNull ReconnectVelocity getInstance() {
        return Objects.requireNonNull(instance, "ReconnectVelocity is not initialized yet!");
    }

    public @NotNull ProxyServer getProxy() {
        return Objects.requireNonNull(proxy);
    }

    public @NotNull Logger getLogger() {
        return Objects.requireNonNull(logger);
    }

    public @NotNull Path getDataDirectory() {
        return Objects.requireNonNull(dataDirectory);
    }

    public @NotNull ReconnectConfig getConfig() {
        return Objects.requireNonNull(config);
    }

    public UpdateChecker getUpdateChecker() {
        return checker;
    }
}
