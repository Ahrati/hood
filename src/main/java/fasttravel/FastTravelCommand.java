package fasttravel;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import db.database;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;


import java.sql.SQLException;

public class FastTravelCommand implements TabExecutor {

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

    private void playParticleEffects(Location location, Player player, World world) {
        for (int i = 0; i < 100; i++) {
            double offsetX = Math.random() * 2.0 - 1.0;
            double offsetY = Math.random() * 2.0 - 1.0;
            double offsetZ = Math.random() * 2.0 - 1.0;
            player.spawnParticle(
                    Particle.REDSTONE,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    1,
                    offsetX, offsetY, offsetZ,
                    1.0,
                    new Particle.DustOptions(Color.fromRGB(
                            (int) (Math.random() * 255),
                            (int) (Math.random() * 255),
                            (int) (Math.random() * 255)
                    ), 2.0f)
            );
            world.spawnParticle(
                    Particle.REDSTONE,
                    location.getX(),
                    location.getY(),
                    location.getZ(),
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
            commandSender.sendMessage("§cYou can only fast travel in the overworld.");
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
            commandSender.sendMessage("§cThat Fast Travel Point does not exist.");
            return true;
        }

        int x, y, z;

        x = fastTravelPoint.getX() + getRandomOffset(fastTravelPoint.getRadius());
        y = fastTravelPoint.getY();
        z = fastTravelPoint.getZ() + getRandomOffset(fastTravelPoint.getRadius());
        Location teleportLocation = new Location(player.getWorld(), x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());


        int delay = 3;

        player.sendMessage("§aFast Traveling in §d" + delay + " §aseconds");

        new BukkitRunnable() {
            @Override
            public void run() {
                playParticleEffects(player.getLocation(), player, player.getWorld());
                player.teleport(teleportLocation);
                player.sendMessage("§aFast Traveled to §6" + fastTravelPoint.getName());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                playParticleEffects(teleportLocation, player, player.getWorld());
            }
        }.runTaskLater(plugin, 20 * delay);


        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            FastTravelRepository fastTravelRepository = new FastTravelRepository(db);
            try {
                return fastTravelRepository.GetFastTravelPointNames();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not fetch ftp names");
            }
            return null;
        }
        return null;
    }
}
