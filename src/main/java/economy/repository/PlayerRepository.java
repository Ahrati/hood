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

    public User getPlayer(String username) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM balances WHERE username = ?");
        statement.setString(1, username);

        ResultSet resultSet = statement.executeQuery();
        User player;
        if(resultSet.next()) {
            player = new User(UUID.fromString(resultSet.getString("player_uuid")),
                                resultSet.getString("username"),
                                resultSet.getInt("money"));
            statement.close();
        }

        statement.close();
        return null;
    }

    public void transferMoney(String from, String to, int amount) throws SQLException {
        Player sender = getServer().getPlayer(from);
        Player reciever = getServer().getPlayer(to);

        if (reciever == null) {
            sender.sendMessage("Player not found!");
            return;
        }

        updateMoney(getPlayer(from), -amount);
        updateMoney(getPlayer(to), amount);

        sender.sendMessage("Transferred $" + amount + " to " + to);
        reciever.sendMessage("Received $" + amount + " from " + from);
    }

    public void updateMoney(User player, int amount) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE user SET player_uuid = ?, username = ?, money = ? WHERE username = ?");
        statement.setString(1, player.getUuid().toString());
        statement.setString(2, player.getUsername());
        statement.setInt(3, player.getMoney() + amount);
        statement.setString(5, player.getUsername());
        statement.executeUpdate();
        statement.close();
    }
}
