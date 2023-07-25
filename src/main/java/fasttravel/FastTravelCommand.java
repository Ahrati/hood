package fasttravel;

import economy.handler.MoneyHandler;
import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import db.database;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Random;


import java.sql.SQLException;

public class FastTravelCommand implements TabExecutor {
    private PlayerRepository playerRepository;
    private OrganisationRepository organisationRepository = new OrganisationRepository();;
    private MoneyHandler moneyHandler;
    private final database db;
    private static Plugin plugin = null;
    private final Map<UUID, Long> lastFastTravelTimestamps = new HashMap<>();
    private int cooldownMinutes = 5;
    private int taxPerMinute = 10;
    private final int cooldownDuration = cooldownMinutes * 60 * 1000; //cooldown in milliseconds


    public FastTravelCommand(database db, Plugin instance){
        this.db = db;
        plugin = instance;
        playerRepository = new PlayerRepository(db);
        moneyHandler = new MoneyHandler(playerRepository, organisationRepository);
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

        if (!player.hasPermission("fasttravel.use")){
            commandSender.sendMessage("§cYou are banned from fast traveling.");
            return true;
        }

        if ((int)player.getHealth() < 19) {
            player.sendMessage("§cHeal before fast travelling!");
            return true;
        }

        if (player.getFallDistance() > 0){
            player.sendMessage("§cCan not fast travel while falling!");
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

        World overworld = Bukkit.getWorld("world");

        long currentTime = System.currentTimeMillis();

        Long lastTravelTimestamp = lastFastTravelTimestamps.get(player.getUniqueId());
        boolean isWithinCooldown = lastTravelTimestamp != null && currentTime - lastTravelTimestamp < cooldownDuration;
        int elapsedTimeMinutes = cooldownMinutes;

        if (isWithinCooldown) {
            long elapsedMilliseconds = currentTime - lastTravelTimestamp;
            elapsedTimeMinutes = (int) (elapsedMilliseconds / (60 * 1000)); // Convert elapsed time to minutes
        }

        int taxAmount = Math.max(0, (cooldownMinutes - elapsedTimeMinutes) * taxPerMinute);


        if (taxAmount > 0) {
            int playerBalance = 0;
            try {
                playerBalance = moneyHandler.getBalance(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (playerBalance >= taxAmount) {
                // moneyHandler.transferMoney(player, government, "p2o");
            } else {
                player.sendMessage("§cYou don't have enough money to fast travel. The tax is $" + taxAmount);
                return true;
            }
        }

        playParticleEffects(player.getLocation(), player, player.getWorld());
        Location teleportLocation = new Location(overworld, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
        player.teleport(teleportLocation);
        player.sendMessage("§aFast Traveled to §6" + fastTravelPoint.getName());
        player.sendMessage("§aYou can fast travel for free again in " + cooldownMinutes + " minutes");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        playParticleEffects(teleportLocation, player, player.getWorld());
        lastFastTravelTimestamps.put(player.getUniqueId(), currentTime);

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
