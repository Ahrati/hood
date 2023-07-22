package fasttravel;

import db.database;
import org.bukkit.command.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FastTravelPointDeleteCommand implements CommandExecutor{
    private final database db;
    FastTravelRepository fastTravelRepository;
    public FastTravelPointDeleteCommand(database db){
        this.db = db;
        fastTravelRepository = new FastTravelRepository(db);
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        System.out.println("running ftp delete command");


        if (args.length != 1) {
            commandSender.sendMessage("Usage: /fasttravelpointdelete <Name>");
            return true;
        }

        String name = args[0];

        try {
            fastTravelRepository.DeleteFastTravelPoint(name);
            commandSender.sendMessage("Fast Travel Point by the name of " + name + " was deleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

}
