package economy;

import economy.handler.MoneyHandler;
import economy.model.User;
import economy.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

public class balCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    public balCommand(MoneyHandler moneyHandler) {
        this.moneyHandler = moneyHandler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0){
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        try {
            sender.sendMessage("Balance: " + moneyHandler.getBalance((Player) sender) + "$");
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
