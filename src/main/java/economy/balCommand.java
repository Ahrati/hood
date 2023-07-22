package economy;

import economy.model.User;
import economy.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class balCommand implements TabExecutor {
    private final PlayerRepository playerRepository;
    public balCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 1){
            sender.sendMessage("Invalid use of command bal");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        User player;
        try {
            player = playerRepository.getPlayer(sender.getName());
            sender.sendMessage("Balance: " + player.getMoney() + "$");
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Couldn't fetch balance");
            System.out.println("Could not get Player from database.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
