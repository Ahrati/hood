package economy;

import economy.handler.MoneyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class orgCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;

    public orgCommand(MoneyHandler moneyHandler) {
        this.moneyHandler = moneyHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
