package db;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
public class database {
    private Connection connection;
    FileConfiguration config;

    public Connection getConnection() throws SQLException {
        if(connection != null){
            return connection;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("jdbc:mysql://%s:%s/%s", config.getString("ip address"), config.getString("port"), config.getString("database")));

        String url = sb.toString();
        String user = config.getString("user");
        String password = config.getString("password");

        Connection connection = DriverManager.getConnection(url, user, password);

        this.connection = connection;

        System.out.println("Connected to database.");

        return connection;
    }

    public void initializeDatabase(FileConfiguration config) throws SQLException {
        this.config = config;
    }

    public void initializeTable(String sql) throws SQLException {
        Statement statement = getConnection().createStatement();
        statement.execute(sql);
        statement.close();
    }
}
