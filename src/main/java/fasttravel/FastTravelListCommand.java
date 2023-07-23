package fasttravel;

import db.database;
import fasttravel.FastTravelRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.sql.SQLException;
import java.util.List;

public class FastTravelListCommand implements CommandExecutor {
    private final database db;
    FastTravelRepository fastTravelRepository;
    public FastTravelListCommand(database db){
        this.db = db;
        fastTravelRepository = new FastTravelRepository(db);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        List<FastTravelPoint> fastTravelPoints;

        try {
            fastTravelPoints = fastTravelRepository.GetFastTravelPoints();
        } catch (SQLException e) {
            commandSender.sendMessage("An error occurred while fetching fast travel locations.");
            e.printStackTrace();
            return true;
        }

        if (fastTravelPoints.isEmpty()) {
            commandSender.sendMessage("There are no fast travel locations available.");
        } else {
            commandSender.sendMessage("FAST TRAVEL LOCATIONS");
            for (FastTravelPoint point : fastTravelPoints) {
                commandSender.sendMessage("- " + point.getName() + " " +
                        " (" + point.getX() +
                        ", " + point.getY() +
                        ", " + point.getZ() + ")");
            }
        }
        return true;
    }
}
