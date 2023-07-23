package economy.repository;

import db.database;
import economy.model.User;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PlayerRepository {
    private final database db;
    public PlayerRepository(database db) {
        this.db = db;
    }

    public User fetchPlayer(String username) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM user WHERE username = ?");
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();
        User player;
        if(resultSet.next()) {
            player = new User(UUID.fromString(resultSet.getString("player_uuid")),
                                resultSet.getString("username"),
                                resultSet.getInt("money"));
            statement.close();
            return player;
        }

        statement.close();
        return null;
    }
    public User getPlayer(Player player) throws SQLException {
        User user = fetchPlayer(player.getName());
        if(user == null) {
            user = new User(player.getUniqueId(), player.getName(), 0);
            createPlayer(user);
        }
        return user;
    }

    public void transferMoney(String from, String to, int amount) throws SQLException {
        Player sender = getServer().getPlayer(from);
        Player receiver = getServer().getPlayer(to);

        if (receiver == null) {
            sender.sendMessage("Player not found!");
            return;
        }

        updateMoney(fetchPlayer(from), fetchPlayer(from).getMoney()-amount);
        updateMoney(fetchPlayer(to), fetchPlayer(to).getMoney()+amount);

        sender.sendMessage("Transferred $" + amount + " to " + to);
        receiver.sendMessage("Received $" + amount + " from " + from);
    }

    public void updateMoney(User player, int amount) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE user SET player_uuid = ?, username = ?, money = ? WHERE username = ?");
        statement.setString(1, player.getUuid().toString());
        statement.setString(2, player.getUsername());
        statement.setInt(3, amount);
        statement.setString(4, player.getUsername());
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
