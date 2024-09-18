package com.mattmx.reconnect;

import com.google.inject.Inject;
import com.mattmx.reconnect.storage.*;
import com.mattmx.reconnect.util.updater.UpdateChecker;
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
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

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

    private final File configLocation;
    private final YamlConfigurationLoader loader;
    private @Nullable ReconnectConfig config;
    private @Nullable StorageManager storage;
    private UpdateChecker checker;

    @Inject
    public ReconnectVelocity(@Nullable ProxyServer server, @Nullable Logger logger, @DataDirectory @Nullable Path dataDirectory) {
        this.proxy = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;

        configLocation = getDataDirectory().resolve("config.yml").toFile();

        loader = YamlConfigurationLoader.builder()
            .file(configLocation)
            .build();

        instance = this;

        saveDefaultConfig();

        StorageManager.registerStorageMethod(new MySqlStorage());
        StorageManager.registerStorageMethod(new MariaDbStorage());
        StorageManager.registerStorageMethod(new SQLiteStorage());
        StorageManager.registerStorageMethod(new YamlStorage());
        StorageManager.registerStorageMethod(new PostgreSQLStorage());

        if (proxy.getPluginManager().isLoaded("luckperms")) {
            StorageManager.registerStorageMethod(new LuckPermsStorage());
        }

        ReconnectCommand.register(this);

        checker = new UpdateChecker();

        if (getConfig().checkUpdates) {
            String url = "https://api.github.com/repos/Matt-MX/ReconnectVelocity/releases/latest";
            try {
                if (checker.get(url).isLatest(this.getClass().getAnnotation(Plugin.class).version())) {
                    getLogger().info("Running the latest version! ReconnectVelocity " + checker.getLatest());
                } else {
                    getLogger().info("Newer version available! ReconnectVelocity " + checker.getLatest());
                    getLogger().info("Get it here: " + checker.getLink());
                }
            } catch (Exception failure) {
                getLogger().info("Unable to get latest release!");
            }
        }
    }

    public void loadStorage() {
        StorageMethod method = StorageManager.getStorageMethodById(getConfig().storage.method);

        Objects.requireNonNull(method, "That storage method is invalid!");

        // Shutdown current manager
        if (storage != null) {
            storage.end();
        }

        storage = StorageManager.createStorageManager(method);

        getLogger().info("Using {} as storage method!", storage.getStorageMethod().getId());
    }

    @SuppressWarnings({"ResultOfMethodCallIgnored"})
    public void saveDefaultConfig() {
        if (!configLocation.exists()) {
            configLocation.getParentFile().mkdirs();
            try {

                ConfigurationNode node = loader.load();
                node.set(ReconnectConfig.class, new ReconnectConfig());
                loader.save(node);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            this.config = loader.load().get(ReconnectConfig.class);
        } catch (ConfigurateException e) {
            getLogger().error("There were errors when loading the existing config. Renaming and resetting!");
            configLocation.renameTo(configLocation.getParentFile().toPath().resolve("config-old.yml").toFile());
            saveDefaultConfig();
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        loadStorage();

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
