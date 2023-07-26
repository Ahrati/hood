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
    private FastTravelRepository fastTravelRepository;
    public FastTravelListCommand(database db){
        this.db = db;
        fastTravelRepository = new FastTravelRepository(db);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        Player player = (Player) commandSender;
        List<FastTravelPoint> discovered, undiscovered;

        try {
            discovered = fastTravelRepository.GetDiscoveredFastTravelPoints(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            undiscovered = fastTravelRepository.GetUndiscoveredFastTravelPoints(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (discovered.isEmpty() && undiscovered.isEmpty()) {
            commandSender.sendMessage("§cThere are no fast travel locations available.");
        } else {
            commandSender.sendMessage("§aFAST TRAVEL LOCATIONS");
            commandSender.sendMessage("---------------------");
            for (FastTravelPoint point : discovered) {
                commandSender.sendMessage("- §6" + point.getName() + " §r" +
                        " (§b" + point.getX() +
                        ", " + point.getY() +
                        ", " + point.getZ() + "§r)");
            }
            for (FastTravelPoint point : undiscovered) {
                commandSender.sendMessage("- §7" + point.getName() + " " +
                        " (" + point.getX() +
                        ", " + point.getY() +
                        ", " + point.getZ() + ")");
            }
        }
        return true;
    }
}
