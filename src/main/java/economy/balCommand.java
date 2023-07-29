package economy;

import economy.handler.MoneyHandler;
import economy.model.TransactionLog;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class balCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    public balCommand(MoneyHandler moneyHandler) {
        this.moneyHandler = moneyHandler;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 2){
            return false;
        }

        if(args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cThis command can only be used by players!");
                return true;
            }

            try {
                sender.sendMessage("[§dEconomy§r] §aBalance: §6$" + NumberFormat.getInstance().format(moneyHandler.getBalance((Player) sender)));
            } catch (SQLException e) {
                e.printStackTrace();
                sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't fetch balance!");
                System.out.println("§cCould not get Player from database.");
            }
            return true;
        } else if(args.length == 1){
            if(Objects.equals(args[0], "history")) {
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("[§dEconomy§r] Transaction history\n");
                    for(TransactionLog log : moneyHandler.getHistory(sender.getName(), "p")) {
                        sb.append("§7> ").append("[§b").append(log.getFrom()).append("§7 -> §b").append(log.getTo()).append("§7] : §a$").append(log.getAmount()).append(" '").append(log.getDescription()).append(log.datetime).append("'\n");
                    }
                    sb.append("Transaction logs get deleted after 10 days!");

                    sender.sendMessage(sb.toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§dEconomy§r] §cCouldn't fetch history!");
                }
                return true;
            } else if(Objects.equals(args[0], "view")) {
                moneyHandler.flipActionBar((Player) sender);
                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            String[] SUBCOMMANDS = {"history", "view"};
            final List<String> arguments = new ArrayList<>();
            for (String string : SUBCOMMANDS) if (string.toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(string);
            return arguments;
        } else {
            return null;
        }
    }
}
