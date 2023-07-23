package economy;

import economy.model.User;
import economy.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
public class payCommand implements TabExecutor {
    private final PlayerRepository playerRepository;
    public payCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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

        Player player = (Player) sender;
        String username = player.getName();

        String reciever = args[0];
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            return false;
        }

        if(sender.getName().equalsIgnoreCase(reciever)) {
            sender.sendMessage("You cannot transfer to yourself!");
            return true;
        }

        if(amount < 1) {
            sender.sendMessage("The amount must be a positive number");
            return true;
        }
        int maxAmount;
        try {
            maxAmount = playerRepository.fetchPlayer(username).getMoney();
        } catch(SQLException e) {
            maxAmount = 0;
            e.printStackTrace();
        }
        if(amount > maxAmount) {
            sender.sendMessage("You dont have enough funds");
            return true;
        }

        try {
            playerRepository.transferMoney(username, reciever, amount);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Couldn't transfer money");
            System.out.println("Could not transfer money from " + username + " to " + reciever + "!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

