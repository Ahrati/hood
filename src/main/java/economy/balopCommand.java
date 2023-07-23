package economy;

import economy.model.User;
import economy.repository.PlayerRepository;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class balopCommand implements TabExecutor {
    private final PlayerRepository playerRepository;
    public balopCommand(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length != 3) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        if(!(sender.isOp())) {
            sender.sendMessage("This command can only be used by operators!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sender.sendMessage("Not a valid number");
            return true;
        }

        if(amount < 0) {
            sender.sendMessage("Amount cant be negative");
            return true;
        }

        switch (args[0]) {
            case "set" -> {
                try {
                    User receiver = playerRepository.fetchPlayer(args[1]);
                    playerRepository.updateMoney(receiver, amount);
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Could not fetch player from database");
                }
                return true;
            }
            case "add" -> {
                try {
                    User receiver = playerRepository.fetchPlayer(args[1]);
                    playerRepository.updateMoney(receiver, receiver.getMoney() + amount);
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Could not fetch player from database");
                }
                return true;
            }
            case "sub" -> {
                try {
                    User receiver = playerRepository.fetchPlayer(args[1]);
                    playerRepository.updateMoney(receiver, receiver.getMoney() - amount);
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Could not fetch player from database");
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            String[] SUBCOMMANDS = {"set", "sub", "add"};
            final List<String> arguments = new ArrayList<>();
            for (String string : SUBCOMMANDS) if (string.toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(string);
            return arguments;
        } else {
            return null;
        }
    }
}
