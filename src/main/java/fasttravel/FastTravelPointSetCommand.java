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
        System.out.println("inside ftps command");

        if (args.length != 5) {
            System.out.println("wrong args no");
            commandSender.sendMessage("Usage: /fasttravelpointset <Name> <X> <Y> <Z> <Radius>");
            return true;
        }

        String name = args[0];
        System.out.println("name set");
        int x, y, z, radius;

        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
            radius = Integer.parseInt(args[4]);
            System.out.println("coords set");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            commandSender.sendMessage("Invalid coordinates or radius.");
            System.out.println("wrong coords");
            return true;
        }

        FastTravelPoint fastTravelPoint = new FastTravelPoint(name,x,y,z,radius);

        FastTravelRepository fastTravelRepository = new FastTravelRepository(db);

        try {
            System.out.println("creating ftp");
            fastTravelRepository.CreateFastTravelPoint(fastTravelPoint);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("cant write ftp into db");
            throw new RuntimeException(e);
        }

        System.out.println("Fast travel point set for " + name + " at coordinates (" + x + ", " + y + ", " + z + ") with a radius of " + radius);
        commandSender.sendMessage("Fast travel point set for " + name + " at coordinates (" + x + ", " + y + ", " + z + ") with a radius of " + radius);
        return true;
    }
}
