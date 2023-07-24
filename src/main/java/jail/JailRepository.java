package jail;

import db.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class works with the database related to jails.
 */
public class JailRepository {
    private final database db;

    public JailRepository(database db) {
        this.db = db;
    }

    /**
     * Creates a jail.
     */
    public void CreateJail(Jail jail) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO jails(name, x, y, z) VALUES (?, ?, ?, ?)");
        statement.setString(1, jail.getName());
        statement.setInt(2, jail.getX());
        statement.setInt(3, jail.getY());
        statement.setInt(4, jail.getZ());

        statement.executeUpdate();

        statement.close();
    }

    /**
     * Returns a jail with a given name.
     */
    public Jail GetJail(String name) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM jails WHERE name = ?");
        statement.setString(1, name);

        ResultSet resultSet = statement.executeQuery();

        Jail jail = null;

        if (resultSet.next()) {
            String jailName = resultSet.getString("name");
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            int z = resultSet.getInt("z");

            jail = new Jail(jailName, x, y, z);
        }

        statement.close();

        return jail;
    }

    /**
     * Deletes a jail with a given name.
     */
    public void DeleteJail(String name) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM jails WHERE name = ?");
        statement.setString(1, name);

        statement.executeUpdate();

        statement.close();
    }

    /**
     * Returns a list of all jail names.
     */
    public List<String> GetJailNames() throws SQLException {
        List<String> names = new ArrayList<>();

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT name FROM jails");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            names.add(resultSet.getString("name"));
        }

        statement.close();

        return names;
    }

    /**
     * Returns a list of all jails.
     */
    public List<Jail> GetJails() throws SQLException {
        List<Jail> jails = new ArrayList<>();

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM jails");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            int z = resultSet.getInt("z");

            Jail jail = new Jail(name, x, y, z);
            jails.add(jail);
        }

        statement.close();

        return jails;
    }
}
