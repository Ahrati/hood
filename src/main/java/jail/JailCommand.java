package jail;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import db.database;

import java.sql.SQLException;
import java.util.List;

public class JailCommand implements TabExecutor {

    private final database db;
    public JailCommand(database db) {
        this.db = db;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("This command is for players only!");
            return true;
        }

        if (args.length != 2) {
            commandSender.sendMessage("Usage: /jail <Player> <Name>");
            return true;
        }

        String playerName = args[0];
        String jailName = args[1];

        Player targetPlayer = commandSender.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            commandSender.sendMessage("§cPlayer not found!");
            return true;
        }

        JailRepository jailRepository = new JailRepository(db);
        Jail jail;
        try {
            jail = jailRepository.GetJail(jailName);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        if (jail == null) {
            commandSender.sendMessage("§cJail not found!");
            return true;
        }

        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1, false, false));
        targetPlayer.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0, false, false));
        //TO DO add fast travel ban

        int x, y, z;

        x = jail.getX();
        y = jail.getY();
        z = jail.getZ();

        World overworld = Bukkit.getWorld("world");

        Location teleportLocation = new Location(overworld, x, y, z);

        targetPlayer.teleport(teleportLocation);

        commandSender.sendMessage("§aPlayer §b" + playerName + " §ahas been jailed in §6" + jailName + ".");
        targetPlayer.sendMessage("§cYOU ARE UNDER ARREST!");
        targetPlayer.sendMessage("§aYou have been jailed in §6" + jailName + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 2) {
            JailRepository jailRepository = new JailRepository(db);
            try {
                return jailRepository.GetJailNames();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not fetch jail names");
            }
        }
        return null;
    }
}
