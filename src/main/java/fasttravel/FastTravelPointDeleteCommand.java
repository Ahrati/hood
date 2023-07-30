package fasttravel;

import db.database;
import org.bukkit.command.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FastTravelPointDeleteCommand implements TabExecutor{
    private final database db;
    FastTravelRepository fastTravelRepository;
    public FastTravelPointDeleteCommand(database db){
        this.db = db;
        fastTravelRepository = new FastTravelRepository(db);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /fasttravelpointdelete <Name>");
            return true;
        }

        String name = args[0];

        try {
            fastTravelRepository.DeleteFastTravelPoint(name);
            commandSender.sendMessage("[§dFast Travel§r] §aFast Travel Point by the name of §6" + name + "§a was deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            FastTravelRepository fastTravelRepository = new FastTravelRepository(db);
            try {
                return fastTravelRepository.GetFastTravelPointNames();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not fetch ftp names");
            }
            return null;
        }
        return null;
    }
}
