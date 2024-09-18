package com.mattmx.reconnect.storage;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Objects;

public class StorageManager {
    private static final HashMap<String, StorageMethod> methods = new HashMap<>();
    private final @NotNull StorageMethod currentStorageMethod;

    public StorageManager(@NotNull StorageMethod method) {
        this.currentStorageMethod = method;
    }

    public void init() {
        currentStorageMethod.init();
    }

    public void end() {
        currentStorageMethod.save();
    }

    public @NotNull StorageMethod getStorageMethod() {
        return this.currentStorageMethod;
    }

    public static void registerStorageMethod(@NotNull StorageMethod method) {
        methods.put(method.getId(), method);
    }

    public static StorageManager createStorageManager(@NotNull StorageMethod method) {
        StorageManager manager = new StorageManager(method);
        manager.init();
        return manager;
    }

    public static @Nullable StorageMethod getStorageMethodById(@NotNull String id) {
        return methods.get(id);
    }
}
