package transactionsign;

import economy.handler.OrganisationHandler;
import economy.model.User;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransactionSignListener implements Listener {
    private Plugin plugin;
    private OrganisationHandler organisationHandler;
    private NamespacedKey keyDescription;
    private NamespacedKey keyAmount;
    private NamespacedKey keyReceiver;
    private NamespacedKey keyMode;
    private final NamespacedKey keyIsTransactionSign;
    private final Pattern transactionPattern = Pattern.compile("<([a-zA-Z])>([a-zA-Z0-9_]+)");

    public TransactionSignListener(Plugin plugin, OrganisationHandler organisationHandler) {
        this.plugin = plugin;
        this.organisationHandler = organisationHandler;
        keyIsTransactionSign = new NamespacedKey(plugin, "is_transaction_sign");
        keyDescription = new NamespacedKey(plugin, "transaction_description");
        keyAmount = new NamespacedKey(plugin, "transaction_amount");
        keyReceiver = new NamespacedKey(plugin, "transaction_receiver");
        keyMode = new NamespacedKey(plugin, "transaction_mode");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        Player player = event.getPlayer();
        player.sendMessage("sign edited, checking if TS");

        if (event.getLine(0).equals("[$]")){
            player.sendMessage("making TS");
            Sign sign = (Sign) event.getBlock().getState();

            // CHECK MODE AND RECEIVER
            String secondLine = event.getLine(1);
            Matcher matcher = transactionPattern.matcher(secondLine);
            if (!matcher.matches()) {
                player.sendMessage("[§dTransaction Sign§r] §cInvalid format on line 2. Please use <mode>playerName.");
                return;
            }

            String mode = matcher.group(1);
            String receiver = matcher.group(2);
            player.sendMessage(mode);
            player.sendMessage(receiver);

            if (!(mode.equals("o") || mode.equals("p"))){
                player.sendMessage("[§dTransaction Sign§r] §cMode must be 'o' or 'p'!");
                return;
            }

            if (mode.equals("p")) {
                if (!receiver.equals(player.getDisplayName())) {
                    player.sendMessage("[§dTransaction Sign§r] §cYou must be the receiver!");
                    return;
                }
            }

            if (mode.equals("o")) {
                try {
                    if (!organisationHandler.isOrganisation(receiver)){
                        player.sendMessage("[§dTransaction Sign§r] §cThat organisation doesn't exist");
                        return;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                List<User> orgMembers;
                try {
                    orgMembers = organisationHandler.getAllMembers(receiver);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (orgMembers == null) {
                    player.sendMessage("[§dTransaction Sign§r] §cYou are not a member of the organisation.");
                    return;
                }

                boolean playerIsMember = false;
                for (User member : orgMembers) {
                    if (member.getUsername().equals(player.getName())) {
                        playerIsMember = true;
                        break;
                    }
                }

                if (!playerIsMember) {
                    player.sendMessage("[§dTransaction Sign§r] §cYou are not a member of the organization.");
                    return;
                }
            }

            //CHECK AMOUNT
            int amount = 0;
            try {
                amount = Integer.parseInt(event.getLine(2));
                if (amount <= 0 || event.getLine(2).isEmpty()) {
                    player.sendMessage("[§dTransaction Sign§r] §cWrong amount!");
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("[§dTransaction Sign§r] §cInvalid value on line 3. Please enter a number greater than 0.");
            }

            //EVERYTHING ALRIGHT
            PersistentDataContainer container = sign.getPersistentDataContainer();

            //mode
            container.set(keyMode, PersistentDataType.STRING, mode);

            //receiver
            container.set(keyReceiver, PersistentDataType.STRING, receiver);

            //amount
            container.set(keyAmount, PersistentDataType.INTEGER, amount);

            //description
            String description = event.getLine(3);
            container.set(keyDescription, PersistentDataType.STRING, description);

            //wax
            sign.setWaxed(true);
            player.sendMessage("waxed " + sign.isWaxed());

            player.sendMessage("[§dTransaction Sign§r] §aTransaction sign created!");
            container.set(keyIsTransactionSign, PersistentDataType.BYTE, (byte) 1);

            sign.update();
        }


    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getState() instanceof Sign){
            Sign sign = (Sign) event.getClickedBlock().getState();
            PersistentDataContainer container = sign.getPersistentDataContainer();

            if (!container.has(keyIsTransactionSign, PersistentDataType.BYTE)) return;

            Player player = (Player) event.getPlayer();

            String sender = player.getName();
            String receiver = container.get(keyReceiver,PersistentDataType.STRING);
            String mode = container.get(keyMode,PersistentDataType.STRING);
            int amount = container.get(keyAmount,PersistentDataType.INTEGER);
            String description = container.get(keyDescription,PersistentDataType.STRING);

            player.sendMessage("TS interact!" + sender + receiver + mode + amount + description);
        }
    }
}
