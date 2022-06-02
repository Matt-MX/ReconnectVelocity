package com.mattmx.reconnect.util.storage;

public abstract class StorageMethod {
    public abstract void init();
    public abstract void setLastServer(String uuid, String servername);
    public abstract String getLastServer(String uuid);
    public abstract String getMethod();
    public void save() {

    }
}
