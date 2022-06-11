package com.mattmx.reconnect.util.storage;

import com.mattmx.reconnect.ReconnectVelocity;
import com.mattmx.reconnect.util.Config;
import org.simpleyaml.configuration.file.FileConfiguration;

import java.io.IOException;
import java.sql.*;

public class MySqlStorage extends StorageMethod {
    private Statement statement;

    @Override
    public void init() {
        try {
            FileConfiguration config = Config.DEFAULT;
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://" +
                            config.getString("storage.data.address", "localhost:3306") + "/" +
                            config.getString("storage.data.database", "reconnect"),
                    config.getString("storage.data.username"), config.getString("storage.data.password")
            );
            statement = conn.createStatement();
            statement.setQueryTimeout(0);
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS reconnect_data(" +
                                        "uuid VARCHAR(255)," +
                                        "lastserver MEDIUMTEXT," +
                                        "PRIMARY KEY(uuid))");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLastServer(String uuid, String servername) {
        try {
            statement.executeUpdate(
                    "INSERT INTO reconnect_data VALUES ('" + uuid + "','" + servername + "')" +
                            "ON DUPLICATE KEY UPDATE lastserver = '" + servername + "'"
            );
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
        return "mysql";
    }
}
