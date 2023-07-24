package jail;

import db.database;
import java.sql.SQLException;

import jail.Jail;
import jail.JailRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.List;

public class JailListCommand implements CommandExecutor {
    private final database db;
    private final JailRepository jailRepository;

    public JailListCommand(database db){
        this.db = db;
        this.jailRepository = new JailRepository(db);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        List<Jail> jails;
        try {
            jails = jailRepository.GetJails();
        } catch (SQLException e) {
            commandSender.sendMessage("An error occurred while fetching jail locations.");
            e.printStackTrace();
            return true;
        }

        if (jails.isEmpty()) {
            commandSender.sendMessage("§cThere are no jails set.");
        } else {
            commandSender.sendMessage("§aJAIL LOCATIONS");
            commandSender.sendMessage("---------------------");
            for (Jail jail : jails) {
                commandSender.sendMessage("- §6" + jail.getName() + " §r" +
                        " (§b" + jail.getX() +
                        ", " + jail.getY() +
                        ", " + jail.getZ() + "§r)");
            }
        }
        return true;
    }
}
