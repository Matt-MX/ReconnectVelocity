package com.mattmx.reconnect.storage;

import com.mattmx.reconnect.ReconnectConfig;
import com.mattmx.reconnect.ReconnectVelocity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MariaDbStorage extends StorageMethod {
    private HikariDataSource ds;

    @Override
    public void init() {
        ReconnectConfig config = ReconnectVelocity.get().getConfig();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(org.mariadb.jdbc.Driver.class.getName());
        if (config.storage.data.connectionParameters.useJdbcString) {
            hikariConfig.setJdbcUrl(config.storage.data.connectionParameters.jdbcString);
        } else {
            hikariConfig.setJdbcUrl("jdbc:mariadb://" + config.storage.data.address + "/" + config.storage.data.database);
        }
        hikariConfig.setUsername(config.storage.data.username);
        hikariConfig.setPassword(config.storage.data.password);
        hikariConfig.setConnectionTimeout(config.storage.data.connectionParameters.connectionTimeout);
        hikariConfig.setIdleTimeout(config.storage.data.connectionParameters.idleTimeout);
        hikariConfig.setKeepaliveTime(config.storage.data.connectionParameters.keepAliveTime);
        hikariConfig.setMaxLifetime(config.storage.data.connectionParameters.maxLifetime);
        hikariConfig.setMinimumIdle(config.storage.data.connectionParameters.minimumIdle);
        hikariConfig.setMaximumPoolSize(config.storage.data.connectionParameters.maximumPoolSize);
        hikariConfig.setPoolName("reconnect");

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
