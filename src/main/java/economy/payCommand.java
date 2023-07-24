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

import static org.bukkit.Bukkit.getServer;

public class payCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    public payCommand(MoneyHandler moneyHandler) {
        this.moneyHandler = moneyHandler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 2) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            return false;
        }

        if(amount < 1) {
            sender.sendMessage("The amount must be a positive number");
            return true;
        }

        String receiver = args[0];
        try {
            if(moneyHandler.transferMoney((Player) sender, receiver, amount, "p2p")) {
                sender.sendMessage("Transferred $" + amount + " to " + receiver);
                Player target = getServer().getPlayer(receiver);
                if(target != null) {
                    target.sendMessage("Received $" + amount + " from " + sender.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Couldn't transfer money");
            System.out.println("Could not transfer money from " + sender.getName() + " to " + receiver + "!");
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

