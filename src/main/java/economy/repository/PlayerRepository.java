package economy.repository;

import db.database;
import economy.handler.MoneyHandler;
import economy.model.User;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PlayerRepository {
    private List<User> cache;
    private final database db;
    public PlayerRepository(database db) {
        this.db = db;
        this.cache = new ArrayList<>();
    }
    public User cached(String username) {
        for(User user : cache) {
            if(Objects.equals(user.getUsername(), username)) {
                return user;
            }
        }
        return null;
    }
    public User fetchPlayer(String username) throws SQLException {

        User cached = cached(username);
        if(cached != null) {
            return cached;
        }

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM user WHERE username = ?");
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();
        User player;
        if(resultSet.next()) {
            player = new User(UUID.fromString(resultSet.getString("player_uuid")),
                                resultSet.getString("username"),
                                resultSet.getInt("money"));
            statement.close();

            cache.add(player);
            return player;
        }

        statement.close();
        return null;
    }

    public List<User> fetchPlayers() throws SQLException {
/*
        User cached = cached(username);
        if(cached != null) {
            return cached;
        }
*/
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM user");

        ResultSet resultSet = statement.executeQuery();
        List<User> players = new ArrayList<>();
        while(resultSet.next()) {
            User player = new User(UUID.fromString(resultSet.getString("player_uuid")),
                    resultSet.getString("username"),
                    resultSet.getInt("money"));
            players.add(player);
            //  cache.add(player);

        }
        
        statement.close();
        statement.close();
        return players;
    }

    public User getPlayer(Player player) throws SQLException {
        User user = fetchPlayer(player.getName());
        if(user == null) {
            user = new User(player.getUniqueId(), player.getName(), 0);
            createPlayer(user);
            cache.add(user);
        }
        return user;
    }

    public void updatePlayer(User player) throws SQLException {

        User cached = cached(player.getUsername());
        if(cached != null) {
            cache.set(cache.indexOf(cached), player);
        }

        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE user SET player_uuid = ?, username = ?, money = ? WHERE player_uuid = ?");
        statement.setString(1, player.getUuid().toString());
        statement.setString(2, player.getUsername());
        statement.setInt(3, player.getMoney());
        statement.setString(4, player.getUuid().toString());
        statement.executeUpdate();
        statement.close();
    }

    private void createPlayer(User user) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO user (player_uuid, username, money) VALUES (?, ?, ?)");
        statement.setString(1, user.getUuid().toString());
        statement.setString(2, user.getUsername());
        statement.setInt(3, user.getMoney());
        statement.executeUpdate();
        statement.close();
    }
}
