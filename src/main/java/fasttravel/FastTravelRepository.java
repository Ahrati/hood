package fasttravel;
import db.database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class works with the database related to fast travel points.
 */
public class FastTravelRepository {
    private final database db;
    public static HashMap<UUID, List<FastTravelPoint>> DiscoveredFTP = new HashMap<>();
    public static HashMap<UUID, List<FastTravelPoint>> UndiscoveredFTP = new HashMap<>();

    public FastTravelRepository(database db){
        this.db = db;
        updateAllFastTravelMaps();
    }

    /**
     * Populates the fasttraveldiscovery table.
     */
    public void PopulateFastTravelDiscovery() throws SQLException {
        System.out.println("populating fasttraveldiscovery");
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT IGNORE INTO fasttraveldiscovery (ftpname, playerid)\n" +
                "SELECT ftp.name, u.player_uuid\n" +
                "FROM fasttravelpoints AS ftp\n" +
                "CROSS JOIN user AS u;");

        statement.executeUpdate();

        statement.close();
    }

    /**
     * Returns a list of fast travel points that the player has discovered.
     */
    public List<FastTravelPoint> GetDiscoveredFastTravelPoints(UUID uuid) throws SQLException {
        System.out.println("getting player discovered fast travel points");
        List<FastTravelPoint> fastTravelPoints = new ArrayList<>();

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT ftpname FROM fasttraveldiscovery WHERE playerid = ? AND discovered = TRUE;");
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            fastTravelPoints.add(GetFastTravelPoint(resultSet.getString("ftpname")));
        }

        statement.close();

        return fastTravelPoints;
    }

    /**
     * Returns a list of fast travel points that the player has not discovered.
     */
    public List<FastTravelPoint> GetUndiscoveredFastTravelPoints(UUID uuid) throws SQLException {
        System.out.println("getting player undiscovered fast travel points");
        List<FastTravelPoint> fastTravelPoints = new ArrayList<>();

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT ftpname FROM fasttraveldiscovery WHERE playerid = ? AND discovered = FALSE;");
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            fastTravelPoints.add(GetFastTravelPoint(resultSet.getString("ftpname")));
        }

        statement.close();

        return fastTravelPoints;
    }

    /**
     * Private method to update the DiscoveredFTP and UndiscoveredFTP HashMaps for a player based on the database data.
     */
    private void updateFastTravelMaps(Player player) {
        System.out.println("updateing fast travel maps");
        UUID playerUUID = player.getUniqueId();
        try {
            List<FastTravelPoint> discoveredPoints = GetDiscoveredFastTravelPoints(playerUUID);
            List<FastTravelPoint> undiscoveredPoints = GetUndiscoveredFastTravelPoints(playerUUID);

            DiscoveredFTP.put(playerUUID, discoveredPoints);
            UndiscoveredFTP.put(playerUUID, undiscoveredPoints);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the DiscoveredFTP and UndiscoveredFTP HashMaps for all players.
     */
    public void updateAllFastTravelMaps() {
        System.out.println("updateing fast travel maps for players");
        Bukkit.getOnlinePlayers().forEach(this::updateFastTravelMaps);
    }

    /**
     * Sets a fast travel point as discovered for a player
     */
    public void SetDiscovered(UUID playerUUID, String ftpname) throws SQLException {
        System.out.println("setting as discovered");
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE fasttraveldiscovery SET discovered = TRUE WHERE playerid = ? AND ftpname = ?");

        statement.setString(1, playerUUID.toString());
        statement.setString(2, ftpname);

        statement.executeUpdate();

        statement.close();

        updateAllFastTravelMaps();
    }

    /**
     * Creates a fast travel point.
     */
    public void CreateFastTravelPoint(FastTravelPoint fastTravelPoint) throws SQLException {

        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO fasttravelpoints(name,x,y,z,radius) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, fastTravelPoint.getName());
        statement.setInt(2, fastTravelPoint.getX());
        statement.setInt(3, fastTravelPoint.getY());
        statement.setInt(4, fastTravelPoint.getZ());
        statement.setInt(5, fastTravelPoint.getRadius());

        statement.executeUpdate();

        statement.close();

        PopulateFastTravelDiscovery();
        updateAllFastTravelMaps();
    }

    /**
     * Returns a fast travel point with a given name.
     */
    public FastTravelPoint GetFastTravelPoint(String name)throws SQLException {

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM fasttravelpoints WHERE name = ?");
        statement.setString(1, name);

        ResultSet resultSet = statement.executeQuery();

        FastTravelPoint fastTravelPoint;

        if(resultSet.next()){
            fastTravelPoint = new FastTravelPoint(resultSet.getString("name"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"),resultSet.getInt("radius"));
            statement.close();
            return fastTravelPoint;
        }

        statement.close();

        return null;
    }

    /**
     * Deletes a fast travel point with a given name.
     */
    public void DeleteFastTravelPoint(String name) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM fasttravelpoints WHERE name = ?");
        statement.setString(1, name);

        statement.executeUpdate();

        statement.close();
        updateAllFastTravelMaps();
    }

    /**
     * Returns a list of all fast travel point names.
     */
    public List<String> GetFastTravelPointNames() throws SQLException {
        List<String> names = new ArrayList<>();

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT name FROM fasttravelpoints");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            names.add(resultSet.getString("name"));
        }

        statement.close();

        return names;
    }

    /**
     * Returns a list of all fast travel points.
     */
    public List<FastTravelPoint> GetFastTravelPoints() throws SQLException {
        List<FastTravelPoint> fastTravelPoints = new ArrayList<>();

        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM fasttravelpoints");
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            String name = resultSet.getString("name");
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            int z = resultSet.getInt("z");
            int radius = resultSet.getInt("radius");

            FastTravelPoint fastTravelPoint = new FastTravelPoint(name, x, y, z, radius);
            fastTravelPoints.add(fastTravelPoint);
        }

        statement.close();

        return fastTravelPoints;
    }
}
