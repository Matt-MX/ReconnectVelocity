package com.mattmx.reconnect.util.storage;

import com.mattmx.reconnect.util.Config;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.sql.*;

public class SQLiteStorage extends StorageMethod {
    private Statement statement;

    @Override
    public void init() {
        try {
            FileConfiguration config = Config.DEFAULT;
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection(
                    "jbdc:sqlite:" + config.getString("storage.data.database", "reconnect.db")
            );
            statement.setQueryTimeout(0);
            statement = conn.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS reconnect_data(" +
                    "uuid TEXT," +
                    "lastserver TEXT)");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLastServer(String uuid, String servername) {
        try {
            statement.executeUpdate(
                    "IF EXISTS (SELECT * FROM reconnect_data WHERE uuid = '" + uuid + "')\n" +
                            "UPDATE reconnect_data SET lastserver = " + servername + "\n" +
                            "ELSE\n" +
                            "INSERT INTO reconnect_data ('" + uuid + "', '" + servername + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getLastServer(String uuid) {
        try {
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
        try {
            statement.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getMethod() {
        return "sqlite";
    }
}
