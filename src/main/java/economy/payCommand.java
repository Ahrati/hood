package economy;

import economy.model.User;
import economy.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
// /pay <name> <amount>
public class payCommand implements TabExecutor {
    private final PlayerRepository playerRepository;
    public payCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 3) {
            sender.sendMessage("Invalid use of command pay!");
            return true;
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
            sender.sendMessage("The amount must be a valid number!");
            return true;
        }

        if(sender.getName().equalsIgnoreCase(reciever)) {
            sender.sendMessage("You cannot transfer to yourself!");
            return true;
        }

        if(amount < 1) {
            sender.sendMessage("The amount must be a positive number");
            return true;
        }

        try {
            playerRepository.transferMoney(username, reciever, amount);
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("Couldn't transfer money");
            System.out.println("Could not transfer money from " + username + " to " + reciever + "!");
            return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

