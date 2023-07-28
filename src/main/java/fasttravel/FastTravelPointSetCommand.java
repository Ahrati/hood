package fasttravel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import db.database;

import java.sql.SQLException;

public class FastTravelPointSetCommand implements CommandExecutor {

    private final database db;
    public FastTravelPointSetCommand(database db){
        this.db = db;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length != 5) {
            commandSender.sendMessage("Usage: /fasttravelpointset <Name> <X> <Y> <Z> <Radius>");
            return true;
        }

        String name = args[0];
        int x, y, z, radius;

        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            radius = Integer.parseInt(args[4]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            commandSender.sendMessage("[§dFast Travel§r] §cInvalid coordinates or radius.");
            return true;
        }

        FastTravelPoint fastTravelPoint = new FastTravelPoint(name,x,y,z,radius);

        FastTravelRepository fastTravelRepository = new FastTravelRepository(db);

        try {
            if (fastTravelRepository.GetFastTravelPoint(name) != null){
                commandSender.sendMessage("[§dFast Travel§r] §cFast travel point with that name already exists!");
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            fastTravelRepository.CreateFastTravelPoint(fastTravelPoint);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        commandSender.sendMessage("[§dFast Travel§r] §aFast Travel Point set for §6" + name + " §aat coordinates §r(§b" + x + ", " + y + ", " + z + "§r)§a with a radius of §b" + radius);
        return true;
    }
}
