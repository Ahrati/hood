package fasttravel;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FastTravelBanCommand implements CommandExecutor {

    private final FastTravelBanMaster banMaster;

    public FastTravelBanCommand(Plugin plugin) {
        this.banMaster = new FastTravelBanMaster(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("[Fast Travel]This command is for players only!");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /fasttravelban <name>");
            return true;
        }

        String playerName = args[0];
        Player targetPlayer = commandSender.getServer().getPlayer(playerName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            commandSender.sendMessage("[§dFast Travel§r] §cPlayer §b" + playerName + "§c not found or not online.");
            return true;
        }

        banMaster.FastTravelBan(targetPlayer);
        commandSender.sendMessage("[§dFast Travel§r] §aPlayer §b" + playerName + " §ahas been banned from fast travelling.");
        targetPlayer.sendMessage("[§dFast Travel§r] §aYou have been banned from fast travelling.");

        return true;
    }
}
