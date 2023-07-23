package jail;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import db.database;

import java.sql.SQLException;

public class JailFreeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is for players only!");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /jailfree <Player>");
            return true;
        }

        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);

        if (targetPlayer == null) {
            commandSender.sendMessage("Player " + playerName + " not found!");
            return true;
        }

        if (!targetPlayer.isOnline()) {
            commandSender.sendMessage("Player " + playerName + " not online!");
            return true;
        }

        targetPlayer.removePotionEffect(PotionEffectType.SLOW);
        targetPlayer.removePotionEffect(PotionEffectType.WEAKNESS);
        //TO DO Remove the fast travel ban

        commandSender.sendMessage("§a" + playerName + " §rhas been freed from jail.");
        targetPlayer.sendMessage("§aYou have been freed from jail.");
        return true;
    }
}
