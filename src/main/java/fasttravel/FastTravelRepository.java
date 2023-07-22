package fasttravel;
import db.database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class works with the database related to fast travel points.
 */
public class FastTravelRepository {
    private database db = new database();
    private FastTravelRepository(){

    }

    /**
     * Creates a fast travel point.
     */
    public void CreateFastTravelPoint(FastTravelPoint fastTravelPoint) throws SQLException {

        PreparedStatement statement = db.getConnection()
                .prepareStatement("INSERT INTO fasttravelpoints(name,x,y,z,radius) VALUES (?, ?, ?, ?, ?, ?, ?)");
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
}
