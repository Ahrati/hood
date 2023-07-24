package jail;

import db.database;
import jail.JailRepository;
import org.bukkit.command.*;
import java.sql.SQLException;
import java.util.List;

public class JailDeleteCommand implements TabExecutor {
    private final database db;
    private final JailRepository jailRepository;

    public JailDeleteCommand(database db) {
        this.db = db;
        this.jailRepository = new JailRepository(db);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /jaildelete <Name>");
            return true;
        }

        String name = args[0];

        try {
            jailRepository.DeleteJail(name);
            commandSender.sendMessage("§aJail§r by the name of §6" + name + "§r was §adeleted");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            try {
                return jailRepository.GetJailNames();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not fetch jail names");
            }
            return null;
        }
        return null;
    }
}
