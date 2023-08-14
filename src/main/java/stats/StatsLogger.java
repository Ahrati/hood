package stats;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class StatsLogger {
    private final int intervalInMinutes = 15;
    private final Plugin plugin;
    private final int intervalInTicks;

    public StatsLogger(Plugin plugin){
        this.plugin = plugin;
        intervalInTicks = 20 * 60 * intervalInMinutes;
    }

    private void LogPlayerData(Player player) {
        String name = player.getName();
        int timePlayed = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60; // Convert to minutes
        int mobsKilled = player.getStatistic(Statistic.MOB_KILLS);
        int diamondsMined = player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE) + player.getStatistic(Statistic.MINE_BLOCK,Material.DEEPSLATE_DIAMOND_ORE);
        int travel = ((player.getStatistic(Statistic.AVIATE_ONE_CM) +
                player.getStatistic(Statistic.BOAT_ONE_CM) +
                player.getStatistic(Statistic.CROUCH_ONE_CM) +
                player.getStatistic(Statistic.FLY_ONE_CM) +
                player.getStatistic(Statistic.HORSE_ONE_CM) +
                player.getStatistic(Statistic.MINECART_ONE_CM) +
                player.getStatistic(Statistic.PIG_ONE_CM) +
                player.getStatistic(Statistic.SPRINT_ONE_CM) +
                player.getStatistic(Statistic.STRIDER_ONE_CM) +
                player.getStatistic(Statistic.SWIM_ONE_CM) +
                player.getStatistic(Statistic.WALK_ON_WATER_ONE_CM) +
                player.getStatistic(Statistic.WALK_ONE_CM) +
                player.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM)) / 100);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try {
            appendDataToCsv(name, timePlayed, mobsKilled, diamondsMined, travel, date);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendDataToCsv(String name, int timePlayed, int mobsKilled, int diamondsMined, int travel, String date) throws IOException {
        String csvFilePath = plugin.getDataFolder().getAbsolutePath() + "/player_stats.csv";

        File csvFile = new File(csvFilePath);
        csvFile.getParentFile().mkdirs();
        boolean isNewFile = !csvFile.exists();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(csvFilePath, true)))) {
            if (isNewFile) {
                writer.println("Name;Time Played (minutes);Mobs Killed;Diamonds Mined;Meters Traveled;Date");
            }
            writer.println(name + ";" + timePlayed + ";" + mobsKilled + ";" + diamondsMined + ";" + travel + ";" + date);
        }
    }

    private void LogActivity(int onlineCount){
        if (onlineCount == 0) return;

        String csvFilePath = plugin.getDataFolder().getAbsolutePath() + "/activity_stats.csv";

        File csvFile = new File(csvFilePath);
        csvFile.getParentFile().mkdirs();
        boolean isNewFile = !csvFile.exists();

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(csvFilePath, true)))) {
            if (isNewFile) {
                writer.println("Players;Date");
            }
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.println(onlineCount + ";" + formattedDate);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                //System.out.println("[STATS] Writing stats data to CSV!");
                Collection<? extends Player> onlinePlayer = Bukkit.getOnlinePlayers();

                for (Player player : Bukkit.getOnlinePlayers()){
                    LogPlayerData(player);
                }

                LogActivity(onlinePlayer.size());

                //System.out.println("[STATS] Data written! Running again in " + intervalInMinutes + " minutes!");
            }
        }.runTaskTimer(plugin, 0, intervalInTicks);
    }
}
