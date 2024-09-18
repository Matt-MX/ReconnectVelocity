package com.mattmx.reconnect.storage;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class StorageManager {
    private static final HashMap<String, StorageMethod> methods = new HashMap<>();
    private @Nullable StorageMethod currentStorageMethod;

    public void init() {
        Objects.requireNonNull(currentStorageMethod, "Storage not initialized! Can't call init until it is initialized.").init();
    }

    public void end() {
        Objects.requireNonNull(currentStorageMethod, "Storage not initialized! Can't call end until it is initialized.").save();
    }

    public @NotNull StorageMethod getStorageMethod() {
        if (this.currentStorageMethod == null) {
            throw new RuntimeException("There has been no Storage Method set!");
        }

        return this.currentStorageMethod;
    }

    public void setCurrentStorageMethod(@NotNull StorageMethod currentStorageMethod) {
        this.currentStorageMethod = currentStorageMethod;
    }

    public static void registerStorageMethod(@NotNull StorageMethod method) {
        methods.put(method.getId(), method);
    }

    public static StorageManager createStorageManager(@NotNull StorageMethod method) {
        StorageManager manager = new StorageManager();
        manager.setCurrentStorageMethod(method);

        manager.init();

        return manager;
    }

    public static @Nullable StorageMethod getStorageMethodById(@NotNull String id) {
        return methods.get(id);
    }
}
