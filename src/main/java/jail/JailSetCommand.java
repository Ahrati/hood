package jail;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import db.database;

import java.sql.SQLException;

public class JailSetCommand implements CommandExecutor {

    private final database db;
    public JailSetCommand(database db){
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        System.out.println("inside jailset command");

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is for players only!");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission("yourpluginname.jailset")) {
            player.sendMessage("You don't have permission to use this command!");
            return true;
        }

        if (args.length != 4) {
            player.sendMessage("Usage: /jailset <Name> <X> <Y> <Z>");
            return true;
        }

        String name = args[0];
        int x, y, z;

        try {
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            player.sendMessage("§cInvalid coordinates.");
            return true;
        }

        Jail jail = new Jail(name, x, y, z);

        JailRepository jailRepository = new JailRepository(db);

        try {
            jailRepository.CreateJail(jail);
        } catch (SQLException e) {
            e.printStackTrace();
            player.sendMessage("§cCould not create jail. Please try again later.");
            throw new RuntimeException(e);
        }

        player.sendMessage("§aJail §rset for §6" + name + " §rat coordinates (§b" + x + ", " + y + ", " + z + "§r).");
        return true;
    }
}
