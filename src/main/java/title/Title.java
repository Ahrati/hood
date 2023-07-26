package title;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Title {

    public static void sendActionBar(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public static void sendActionBar(List<Player> players, String text) {
        for (Player player : players) {
            sendActionBar(player, text);
        }
    }

    public static void sendActionBar(String text, Player... players) {
        sendActionBar(Arrays.asList(players), text);
    }

    @SuppressWarnings("unchecked")
    public static void sendActionBarAll(String text) {
        sendActionBar((List<Player>) Bukkit.getOnlinePlayers(), text);
    }

    public static void sendTitle(Player player, String title, String subTitle, int fadeInTime, int stayTime,
                                 int fadeOutTime) {
        player.sendTitle(title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    public static void sendTitle(Player player, String title, int fadeInTime, int stayTime, int fadeOutTime) {
        sendTitle(player, title, "", fadeInTime, stayTime, fadeOutTime);
    }

    public static void sendTitle(Player player, String title, String subTitle, int stayTime) {
        sendTitle(player, title, subTitle, 1, stayTime, 1);
    }

    public static void sendTitle(Player player, String title, int stayTime) {
        sendTitle(player, title, "", stayTime);
    }

    public static void sendTitle(Player player, String title, String subTitle) {
        sendTitle(player, title, subTitle, 20);
    }

    public static void sendTitle(Player player, String title) {
        sendTitle(player, title, 20);
    }

    public static void sendTitle(List<Player> players, String title, String subTitle, int fadeInTime, int stayTime,
                                 int fadeOutTime) {
        for (Player player : players) {
            sendTitle(player, title, subTitle, fadeInTime, stayTime, fadeOutTime);
        }
    }

    @SuppressWarnings("unchecked")
    public static void sendTitleAll(String title, String subTitle, int fadeInTime, int stayTime, int fadeOutTime) {
        sendTitle((List<Player>) Bukkit.getOnlinePlayers(), title, subTitle, fadeInTime, stayTime, fadeOutTime);
    }

    public static void sendTitle(List<Player> players, String title, int fadeInTime, int stayTime, int fadeOutTime) {
        sendTitle(players, title, "", fadeInTime, stayTime, fadeOutTime);
    }

    @SuppressWarnings("unchecked")
    public static void sendTitleAll(String title, int fadeInTime, int stayTime, int fadeOutTime) {
        sendTitle((List<Player>) Bukkit.getOnlinePlayers(), title, fadeInTime, stayTime, fadeOutTime);
    }

}