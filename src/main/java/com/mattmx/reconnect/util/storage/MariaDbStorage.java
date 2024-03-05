package com.mattmx.reconnect.util.storage;

import com.mattmx.reconnect.ReconnectVelocity;
import com.mattmx.reconnect.util.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.simpleyaml.configuration.file.FileConfiguration;

import java.sql.*;

public class MariaDbStorage extends StorageMethod {
    private HikariDataSource ds;

    @Override
    public void init() {
        FileConfiguration config = Config.DEFAULT;
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(org.mariadb.jdbc.Driver.class.getName());
        if (config.getBoolean("storage.data.connection-parameters.useJdbcString", false)) {
            hikariConfig.setJdbcUrl(config.getString("storage.data.connection-parameters.jdbcString", ""));
        } else {
            hikariConfig.setJdbcUrl("jdbc:mariadb://" + config.getString("storage.data.address", "localhost:3306") + "/"
                    + config.getString("storage.data.database", "reconnect"));
        }
        hikariConfig.setUsername(config.getString("storage.data.username"));
        hikariConfig.setPassword(config.getString("storage.data.password"));
        hikariConfig.setConnectionTimeout(config.getLong("storage.data.connection-parameters.connectionTimeout", 30000));
        hikariConfig.setIdleTimeout(config.getLong("storage.data.connection-parameters.idleTimeout", 600000));
        hikariConfig.setKeepaliveTime(config.getLong("storage.data.connection-parameters.keepaliveTime", 0));
        hikariConfig.setMaxLifetime(config.getLong("storage.data.connection-parameters.maxLifetime", 1800000));
        hikariConfig.setMinimumIdle(config.getInt("storage.data.connection-parameters.minimumIdle", 10));
        hikariConfig.setMaximumPoolSize(config.getInt("storage.data.connection-parameters.maximumPoolSize", 10));
        hikariConfig.setPoolName(ReconnectVelocity.get().getName());
        ds = new HikariDataSource(hikariConfig);
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS reconnect_data(" +
                    "uuid VARCHAR(255)," +
                    "lastserver MEDIUMTEXT," +
                    "PRIMARY KEY(uuid))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLastServer(String uuid, String servername) {
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            statement.executeUpdate(
                    "INSERT INTO reconnect_data VALUES ('" + uuid + "','" + servername + "')" +
                            "ON DUPLICATE KEY UPDATE lastserver = '" + servername + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLastServer(String uuid) {
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("SELECT lastserver FROM reconnect_data WHERE uuid = '" + uuid + "'");
            if (rs.next()) {
                return rs.getString("lastserver");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void save() {
        ds.close();
    }

    @Override
    public String getMethod() {
        return "mariadb";
    }
}
