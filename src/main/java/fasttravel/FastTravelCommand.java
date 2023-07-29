package fasttravel;

import economy.handler.MoneyHandler;
import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import economy.repository.TransactionLogRepository;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import db.database;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.Random;


import java.sql.SQLException;

public class FastTravelCommand implements TabExecutor {
    private PlayerRepository playerRepository;
    private TransactionLogRepository transactionRepository;
    private FastTravelBanMaster fastTravelBanMaster;
    private MoneyHandler moneyHandler;
    private final database db;
    private OrganisationRepository organisationRepository;
    private static Plugin plugin = null;
    private final Map<UUID, Long> lastFastTravelTimestamps = new HashMap<>();
    private int cooldownMinutes = 1;
    private int taxPerMinute = 10;
    private final int cooldownDuration = cooldownMinutes * 60 * 1000; //cooldown in milliseconds


    public FastTravelCommand(database db, Plugin instance){
        this.db = db;
        plugin = instance;
        playerRepository = new PlayerRepository(db);
        transactionRepository = new TransactionLogRepository(db);
        organisationRepository = new OrganisationRepository(db);
        moneyHandler = new MoneyHandler(playerRepository, organisationRepository, transactionRepository, instance);
        fastTravelBanMaster = new FastTravelBanMaster(plugin);
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

    private Boolean checkPlayerSober(Player player) {
        Set<PotionEffectType> soberEffects = new HashSet<>();
        soberEffects.add(PotionEffectType.POISON);
        soberEffects.add(PotionEffectType.CONFUSION);
        soberEffects.add(PotionEffectType.WITHER);
        soberEffects.add(PotionEffectType.SLOW);
        soberEffects.add(PotionEffectType.BAD_OMEN);
        soberEffects.add(PotionEffectType.BLINDNESS);
        soberEffects.add(PotionEffectType.HUNGER);
        soberEffects.add(PotionEffectType.WEAKNESS);

        boolean sober = true;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (soberEffects.contains(effect.getType())) {
                sober = false;
                break; // No need to continue checking if we already found a sobering effect
            }
        }

        return sober;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {

        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("[Fast Travel]This command is for players only!");
            return true;
        }

        if (args.length != 1) {
            commandSender.sendMessage("Usage: /fasttravel <Name>");
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.getWorld().getEnvironment().equals(World.Environment.NORMAL)) {
            commandSender.sendMessage("[§dFast Travel§r] §cYou can only fast travel in the overworld.");
            return true;
        }

        if (fastTravelBanMaster.IsFastTravelBanned(player)){
            commandSender.sendMessage("[§dFast Travel§r] §cYou are banned from fast traveling.");
            return true;
        }

        if ((int)player.getHealth() < 19) {
            player.sendMessage("[§dFast Travel§r] §cHeal before fast travelling!");
            return true;
        }

        if (player.getFallDistance() > 0){
            player.sendMessage("[§dFast Travel§r] §cCan not fast travel while falling!");
            return true;
        }

        if (!checkPlayerSober(player)){
            player.sendMessage("[§dFast Travel§r] §cYou need to be sober to fast travel!");
            return true;
        }


        String name = args[0];

        FastTravelRepository fastTravelRepository = new FastTravelRepository(db);

        if (!fastTravelRepository.ExistsLocal(player.getUniqueId(), name)){
            commandSender.sendMessage("[§dFast Travel§r] §cThat Fast Travel Point does not exist.");
            return true;
        }

        UUID playerUUID = player.getUniqueId();
        boolean isUndiscovered = false;

        List<FastTravelPoint> undiscoveredPoints = fastTravelRepository.GetUndiscoveredFastTravelPointsLocal(playerUUID);

        for (FastTravelPoint undiscoveredPoint : undiscoveredPoints) {
            if (undiscoveredPoint.getName().equalsIgnoreCase(name)) {
                isUndiscovered = true;
                break;
            }
        }

        if (isUndiscovered) {
            commandSender.sendMessage("[§dFast Travel§r] §cYou haven't discovered this location yet!");
            return true;
        }

        List<FastTravelPoint> discovered = fastTravelRepository.GetDiscoveredFastTravelPointsLocal(playerUUID);
        FastTravelPoint fastTravelPoint = null;

        for (FastTravelPoint point : discovered) {
            if (point.getName().equalsIgnoreCase(name)) {
                fastTravelPoint = point;
                break;
            }
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
            int playerBalance;
            try {
                playerBalance = moneyHandler.getBalance(player);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if(playerBalance >= taxAmount) {
                try {
                    if(moneyHandler.transferMoney(player.getName(), "government", taxAmount, "Fast travel tax","p2o") != 0) {
                        return true;
                    }
                    else {
                        player.sendMessage("[§dFast Travel§r] §aPaid $" + taxAmount + " fast travel tax");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                player.sendMessage("[§dFast Travel§r] §cYou don't have enough money to fast travel. The tax is $" + taxAmount);
                return true;
            }
        }

        playParticleEffects(player.getLocation(), player, player.getWorld());

        Location teleportLocation = new Location(overworld, x, y, z, player.getLocation().getYaw(), player.getLocation().getPitch());
        player.teleport(teleportLocation);

        player.sendMessage("[§dFast Travel§r] §aFast Traveled to §6" + fastTravelPoint.getName());
        player.sendMessage("[§dFast Travel§r] §aYou can fast travel for free again in " + cooldownMinutes + " minutes");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        playParticleEffects(teleportLocation, player, player.getWorld());

        lastFastTravelTimestamps.put(player.getUniqueId(), currentTime);

        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                UUID playerUUID = player.getUniqueId();
                List<FastTravelPoint> discoveredPoints = FastTravelRepository.DiscoveredFTP.getOrDefault(playerUUID, Collections.emptyList());

                List<String> completions = new ArrayList<>();
                for (FastTravelPoint point : discoveredPoints) {
                    String name = point.getName();
                    if (name.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(name);
                    }
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }
}
