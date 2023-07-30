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
    }

    /**
     * Populates the fasttraveldiscovery table.
     */
    public void PopulateFastTravelDiscovery() throws SQLException {
        //System.out.println("DATABASE PopulateFastTravelDiscovery");
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
        //System.out.println("DATABASE GetDiscoveredFastTravelPoints");
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
        //System.out.println("DATABASE GetUndiscoveredFastTravelPoints");
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
     * Returns a list of fast travel points that the player has discovered from the local hash map.
     */
    public List<FastTravelPoint> GetDiscoveredFastTravelPointsLocal(UUID uuid) {
        //System.out.println("LOCAL GetDiscoveredFastTravelPoints");
        return DiscoveredFTP.getOrDefault(uuid, new ArrayList<>());
    }

    /**
     * Returns a list of fast travel points that the player has not discovered from the local hash map.
     */
    public List<FastTravelPoint> GetUndiscoveredFastTravelPointsLocal(UUID uuid) {
        //System.out.println("LOCAL GetUndiscoveredFastTravelPoints");
        return UndiscoveredFTP.getOrDefault(uuid, new ArrayList<>());
    }

    /**
     * Checks if a FastTravelPoint with the given name exists in either the discovered or undiscovered hash map
     * for a given player UUID.
     *
     * @param uuid       The UUID of the player.
     * @param ftpName    The name of the FastTravelPoint to check.
     * @return true if the FastTravelPoint exists in either the discovered or undiscovered hash map, false otherwise.
     */
    public boolean ExistsLocal(UUID uuid, String ftpName) {
        List<FastTravelPoint> discoveredPoints = DiscoveredFTP.getOrDefault(uuid, new ArrayList<>());
        List<FastTravelPoint> undiscoveredPoints = UndiscoveredFTP.getOrDefault(uuid, new ArrayList<>());

        for (FastTravelPoint point : discoveredPoints) {
            if (point.getName().equalsIgnoreCase(ftpName)) {
                return true;
            }
        }

        for (FastTravelPoint point : undiscoveredPoints) {
            if (point.getName().equalsIgnoreCase(ftpName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Private method to update the DiscoveredFTP and UndiscoveredFTP HashMaps for a player based on the database data.
     */
    private void UpdateFastTravelMaps(Player player) {
        //System.out.println("updateFastTravelMaps");
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
    public void UpdateAllFastTravelMaps() {
        //System.out.println("updateAllFastTravelMaps");
        Bukkit.getOnlinePlayers().forEach(this::UpdateFastTravelMaps);
    }

    /**
     * Sets a fast travel point as discovered for a player
     */
    public void SetDiscovered(UUID playerUUID, String ftpname) throws SQLException {
        //System.out.println("DATABASE SetDiscovered");
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE fasttraveldiscovery SET discovered = TRUE WHERE playerid = ? AND ftpname = ?");

        statement.setString(1, playerUUID.toString());
        statement.setString(2, ftpname);

        statement.executeUpdate();

        statement.close();

        UpdateAllFastTravelMaps();
    }

    /**
     * Creates a fast travel point.
     */
    public void CreateFastTravelPoint(FastTravelPoint fastTravelPoint) throws SQLException {
        //System.out.println("DATABASE CreateFastTravelPoint");
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO fasttravelpoints(name,x,y,z,radius) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, fastTravelPoint.getName());
        statement.setInt(2, fastTravelPoint.getX());
        statement.setInt(3, fastTravelPoint.getY());
        statement.setInt(4, fastTravelPoint.getZ());
        statement.setInt(5, fastTravelPoint.getRadius());

        statement.executeUpdate();

        statement.close();

        PopulateFastTravelDiscovery();
        UpdateAllFastTravelMaps();
    }

    /**
     * Returns a fast travel point with a given name.
     */
    public FastTravelPoint GetFastTravelPoint(String name)throws SQLException {
        //System.out.println("DATABASE GetFastTravelPoint");
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
        //System.out.println("DATABASE DeleteFastTravelPoint");
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM fasttravelpoints WHERE name = ?");
        statement.setString(1, name);

        statement.executeUpdate();

        statement.close();
        UpdateAllFastTravelMaps();
    }

    /**
     * Returns a list of all fast travel point names.
     */
    public List<String> GetFastTravelPointNames() throws SQLException {
        //System.out.println("DATABASE GetFastTravelPointNames");
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
        //System.out.println("DATABASE GetFastTravelPoints");
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
