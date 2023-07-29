package economy;

import economy.handler.MoneyHandler;
import economy.handler.OrganisationHandler;
import economy.model.Organisation;
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
    private final MoneyHandler moneyHandler;
    private final OrganisationHandler organisationHandler;
    public balopCommand(MoneyHandler moneyHandler, OrganisationHandler organisationHandler) {
        this.moneyHandler = moneyHandler;
        this.organisationHandler = organisationHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(args.length != 3) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if(!(sender.isOp())) {
            sender.sendMessage("§cThis command can only be used by operators!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return false;
        }

        if(amount < 0) {
            sender.sendMessage("§cAmount can't be a negative number!");
            return true;
        }

        switch (args[0]) {
            case "set" -> {
                try {
                    moneyHandler.setBalance(args[1], amount, "p");
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't fetch player from database!");
                    System.out.println("§cCould not set money for §b" + args[1] + "!");
                }
                sender.sendMessage("[§dEconomy§r] §aSuccess!");
                return true;
            }
            case "add" -> {
                try {
                    moneyHandler.setBalance(args[1], moneyHandler.getBalance(args[1], "p") + amount, "p");
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't fetch player from database!");
                    System.out.println("§cCould not add money for §b" + args[1] + "!");
                }
                sender.sendMessage("[§dEconomy§r] §aSuccess!");
                return true;
            }
            case "sub" -> {
                try {
                    if(amount > moneyHandler.getBalance(args[1], "p")) {
                        sender.sendMessage("§cCan't put user into negative balance!");
                        return true;
                    }
                    moneyHandler.setBalance(args[1], moneyHandler.getBalance(args[1], "p") - amount, "p");
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't fetch player from database!");
                    System.out.println("§cCould not subtract money for §b" + args[1] + "!");
                }
                sender.sendMessage("[§dEconomy§r] §aSuccess!");
                return true;
            }
            case "list" -> {
                StringBuilder sb = new StringBuilder();
                sb.append("[§dEconomy§r] LIST\n");
                try {
                    sb.append("PLAYERS:\n");
                    List<User> players = moneyHandler.getBalances();
                    for(User user : players) {
                        sb.append(user.getUsername()).append(" - $").append(user.getMoney()).append("\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    sb.append("ORGS:\n");
                    List<Organisation> orgs = organisationHandler.getOrganisations();
                    for(Organisation org : orgs) {
                        sb.append(org.getName()).append(" - $").append(org.getMoney()).append("\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                sender.sendMessage(sb.toString());

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
            String[] SUBCOMMANDS = {"set", "sub", "add", "list"};
            final List<String> arguments = new ArrayList<>();
            for (String string : SUBCOMMANDS) if (string.toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(string);
            return arguments;
        } else {
            return null;
        }
    }
}
