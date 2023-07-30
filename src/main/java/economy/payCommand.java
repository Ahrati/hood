package economy;

import economy.handler.MoneyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class payCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    public payCommand(MoneyHandler moneyHandler) {
        this.moneyHandler = moneyHandler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 3) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if(Objects.equals(args[0], "org")) {

            String receiver = args[1];

            int amount;
            try {
                amount = Integer.parseInt(args[2]);
            } catch(NumberFormatException e) {
                return false;
            }

            if(amount < 1) {
                sender.sendMessage("§cAmount to pay can't be a negative number!");
                return true;
            }

            try {
                if(moneyHandler.transferMoney(sender.getName(), receiver, amount, "payment","p2o") == 0) {
                    sender.sendMessage("[§dEconomy§r] §aTransferred §6$" + NumberFormat.getInstance().format(amount) + "§a to §6" + receiver);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't transfer money!");
                System.out.println("§cCould not transfer money from §b" + sender.getName() + " to §b" + receiver + "!");
                return false;
            }
            return true;
        }
        if(args.length != 2) {
            return false;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch(NumberFormatException e) {
            return false;
        }

        if(amount < 1) {
            sender.sendMessage("§cAmount to pay can't be a negative number!");
            return true;
        }

        String receiver = args[0];
        try {
            if(moneyHandler.transferMoney(sender.getName(), receiver, amount, "payment","p2p") == 0) {
                sender.sendMessage("[§dEconomy§r] §aTransferred §6$" + NumberFormat.getInstance().format(amount) + "§a to §b" + receiver);
                Player target = getServer().getPlayer(receiver);
                if(target != null) {
                    target.sendMessage("[§dEconomy§r] §aReceived §6$" + NumberFormat.getInstance().format(amount) + "§a from §b" + sender.getName());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't transfer money!");
            System.out.println("§cCould not transfer money from §b" + sender.getName() + " to §b" + receiver + "!");
            return false;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}

