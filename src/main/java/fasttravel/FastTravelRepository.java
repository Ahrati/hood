package fasttravel;
import db.database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class works with the database related to fast travel points.
 */
public class FastTravelRepository {
    private final database db;

    public FastTravelRepository(database db){
        this.db = db;
    }

    /**
     * Populates the fasttraveldiscovery table.
     */
    public void PopulateFastTravelDiscovery() throws SQLException {

        PreparedStatement statement = db.getConnection().prepareStatement("INSERT IGNORE INTO fasttraveldiscovery (ftpname, playername)\n" +
                "SELECT ftp.name, u.username\n" +
                "FROM fasttravelpoints AS ftp\n" +
                "CROSS JOIN user AS u;");

        statement.executeUpdate();

        statement.close();
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
