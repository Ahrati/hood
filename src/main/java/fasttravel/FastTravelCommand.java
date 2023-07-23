package fasttravel;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import db.database;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


import java.sql.SQLException;

public class FastTravelCommand implements CommandExecutor {

    private final database db;
    private static Plugin plugin = null;

    public FastTravelCommand(database db, Plugin instance){
        this.db = db;
        plugin = instance;
    }
    private int getRandomOffset(int radius) {
        Random random = new Random();
        return random.nextInt(2 * radius + 1) - radius;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        System.out.println("running ft command");


        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("This command is for players only!");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /fasttravel <Name>");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            commandSender.sendMessage("You can only fast travel in the overworld.");
            return true;
        }

        String name = args[0];

        FastTravelRepository fastTravelRepository = new FastTravelRepository(db);

        FastTravelPoint fastTravelPoint;

        try {
            fastTravelPoint = fastTravelRepository.GetFastTravelPoint(name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (fastTravelPoint == null){
            commandSender.sendMessage("That Fast Travel Point does not exist.");
            return true;
        }

        int x, y, z;

        x = fastTravelPoint.getX() + getRandomOffset(fastTravelPoint.getRadius());
        y = fastTravelPoint.getY();
        z = fastTravelPoint.getZ() + getRandomOffset(fastTravelPoint.getRadius());

        int delay = 5;

        player.sendMessage("Fast Traveling in " + delay + " seconds");


        new BukkitRunnable() {
            @Override
            public void run() {
                Location teleportLocation = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
                player.teleport(teleportLocation);
                player.sendMessage("Fast Traveled to " + name);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                for (int i = 0; i < 100; i++) {
                    double offsetX = Math.random() * 2.0 - 1.0;
                    double offsetY = Math.random() * 2.0 - 1.0;
                    double offsetZ = Math.random() * 2.0 - 1.0;
                    player.spawnParticle(
                            Particle.REDSTONE,
                            teleportLocation.getX(),
                            teleportLocation.getY(),
                            teleportLocation.getZ(),
                            1,
                            offsetX, offsetY, offsetZ,
                            1.0,
                            new Particle.DustOptions(Color.fromRGB(
                                    (int) (Math.random() * 255),
                                    (int) (Math.random() * 255),
                                    (int) (Math.random() * 255)
                            ), 2.0f)
                    );
                }
            }
        }.runTaskLater(plugin, 20 * delay);


        return true;
    }
}
