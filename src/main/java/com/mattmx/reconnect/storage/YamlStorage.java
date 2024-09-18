package com.mattmx.reconnect.storage;

import com.mattmx.reconnect.ReconnectVelocity;
import com.velocitypowered.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class YamlStorage extends StorageMethod {

    public @Nullable YamlConfiguration data;
    public @NotNull File dataPath = ReconnectVelocity.get()
        .getDataDirectory()
        .resolve("data.yml")
        .toFile();

    private @Nullable ScheduledTask autoSaveTask;

    @Override
    public void init() {
        if (!dataPath.exists()) {
            try {
                dataPath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        data = YamlConfiguration.loadConfiguration(dataPath);

        autoSaveTask = ReconnectVelocity.get()
            .getProxy()
            .getScheduler()
            .buildTask(ReconnectVelocity.get(), this::saveData)
            .repeat(Duration.ofMinutes(5L))
            .schedule();
    }

    @Override
    public void setLastServer(String uuid, String servername) {
        Objects.requireNonNull(data).set(uuid, servername);
    }

    @Override
    public String getLastServer(String uuid) {
        return Objects.requireNonNull(data).getString(uuid);
    }

    @Override
    public void save() {
        saveData();
        Optional.ofNullable(autoSaveTask).ifPresent(ScheduledTask::cancel);
    }

    private void saveData() {
        try {
            Objects.requireNonNull(data).save(dataPath);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public String getMethod() {
        return "yaml";
    }
}
