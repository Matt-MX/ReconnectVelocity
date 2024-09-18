package com.mattmx.reconnect.storage;

import com.mattmx.reconnect.ReconnectConfig;
import com.mattmx.reconnect.ReconnectVelocity;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteStorage extends StorageMethod {
    private HikariDataSource ds;

    @Override
    public void init() {
        ReconnectConfig config = ReconnectVelocity.get().getConfig();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(org.sqlite.JDBC.class.getName());
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + ReconnectVelocity.get().getDataDirectory() + "/" + config.storage.data.database);
        hikariConfig.setPoolName("reconnect");

        ds = new HikariDataSource(hikariConfig);
        try (Connection con = ds.getConnection()) {
            Statement statement = con.createStatement();
            statement.setQueryTimeout(0);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS reconnect_data(" +
                    "uuid TEXT," +
                    "lastserver TEXT," +
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
                    "INSERT OR IGNORE INTO reconnect_data VALUES ('" + uuid + "', '" + servername + "');" +
                            "UPDATE reconnect_data SET lastserver = '" + servername + "' where uuid ='" + uuid + "'"
            );
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
        return "sqlite";
    }
}
