package economy;

import economy.handler.MoneyHandler;
import economy.handler.OrganisationHandler;
import economy.model.Organisation;
import economy.model.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.bukkit.Bukkit.getServer;

public class orgCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    private final OrganisationHandler organisationHandler;

    public orgCommand(MoneyHandler moneyHandler, OrganisationHandler organisationHandler) {
        this.moneyHandler = moneyHandler;
        this.organisationHandler = organisationHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        if(args.length == 0) {

            StringBuilder sb = new StringBuilder();
            List<Organisation> orgs = new ArrayList<>();

            try {
                orgs = organisationHandler.getOrganisationsByMember((Player) sender);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            sb.append("[§dOrganisations§r] Info\n");

            if(orgs != null) {
                sb.append("> You are a in ").append(orgs.size()).append("/").append(organisationHandler.getMaxOrg()).append(" organisations\n");

                try {
                    for(Organisation org : orgs) {
                        sb.append("> §6").append(org.getName()).append("§r  - §8").append(organisationHandler.getOnlineMembers(org.getName()).size()).append("§r/§8").append(organisationHandler.getAllMembers(org.getName()).size()).append("§r members online\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§Organisations§r] §cCouldn't get organisation info!");
                    System.out.println("§cCould not fetch organisations general info!");
                    return true;
                }
            } else {
                sb.append("> You are not in any organisation currently\n");
            }

            sender.sendMessage(sb.toString());
            return true;
        }

        switch(args[0]) {
            case "help" -> {
                String str = """
                        [§dOrganisations§r] Help
                        /org §7- general info
                        /org <§7org-name§r> §7- info about particular org
                        /org create <§7org-name§r> <§7description§r> §7- create org
                        /org invite <§7name§r> <§7org-name§r> §7- invite people to org
                        /org join <§7org-name§r> §7- join an org
                        /org leave <§7org-name§r> §7- leave org
                        /org kick <§7name§r> <§7org-name§r> §7- kick person from org""";
                sender.sendMessage(str);
                return true;
            }

            case "create" -> {
                StringBuilder desc = new StringBuilder();
                if(args.length < 2) {
                    return false;
                }
                if(args.length >= 3) {
                    for(int i = 2; i < args.length; i++) {
                        desc.append(args[i]).append(" ");
                    }
                }
                try {
                    organisationHandler.createOrganisation(args[1], desc.toString(), (Player) sender);
                    sender.sendMessage("[§dOrganisations§r] §aOrganisation created!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§Organisations§r] §cError creating organisation");
                }
                return true;
            }

            case "join" -> {
                if(args.length > 2) {
                    return false;
                }
                try {
                    if(organisationHandler.joinOrganisation(args[1], (Player) sender, "member")) {
                        List<Player> members = organisationHandler.getOnlineMembers(args[1]);
                        for(Player member : members) {
                            member.sendMessage("[§dOrganisations§r] §b" + sender.getName() + " §rhas joined §6" + args[1] + ".");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return true;
            }

            case "leave" -> {
                if(args.length > 2) {
                    return false;
                }
                try {
                    if(organisationHandler.isOwner((Player) sender, args[1]) && organisationHandler.getAllMembers(args[1]).size() > 1) {
                        sender.sendMessage("[§dOrganisations§r] §cYou can't leave organisation while other members are still in it.");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if(!organisationHandler.leaveOrganisation(args[1], sender.getName())) {
                        sender.sendMessage("[§dOrganisations§r] §cYou are not part of that organisation.");
                    } else {
                        List<Player> members = organisationHandler.getOnlineMembers(args[1]);
                        for(Player member : members) {
                            member.sendMessage("[§dOrganisations§r] §b" + sender.getName() + "§r has left §6" + args[1] + ".");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§Organisations§r] §cCouldn't leave organisation.");
                }

                return true;
            }

            case "invite" -> {
                if(args.length != 3) {
                    return false;
                }
                try {
                    if(!organisationHandler.isOwner((Player) sender, args[2])) {
                        sender.sendMessage("[§dOrganisations§r] §cYou are not the owner of this organisation!");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    organisationHandler.inviteMember((Player) sender, args[1], args[2]);

                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§Organisations§r] §cCouldn't invite player to organisation.");
                }
                return true;
            }

            case "kick" -> {
                if(args.length != 3) {
                    return false;
                }
                try {
                    if(!organisationHandler.isOwner((Player) sender, args[2])) {
                        sender.sendMessage("[§dOrganisations§r] §cYou are not the owner of this organisation!");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(args[1].equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage("[§dOrganisations§r] §cCan't kick self!");
                }
                try {
                    if(!organisationHandler.leaveOrganisation(args[2], args[1])) {
                        sender.sendMessage("[§dOrganisations§r] §cUser doesn't exist or isn't part of that organisation.");
                    } else {
                        List<Player> members = organisationHandler.getOnlineMembers(args[2]);
                        for(Player member : members) {
                            member.sendMessage("[§dOrganisations§r] §b" + args[1] + " §rhas been kicked from §6" + args[2] + ".");
                        }
                        Player leaver = getServer().getPlayer(args[1]);
                        if(leaver != null) {
                            leaver.sendMessage("[§dOrganisations§r] §rYou've been kicked from " + args[2]);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("§c<§rerror§c>§r [§Organisations§r] §cCouldn't kick player from organisation.");
                }
                return true;
            }

            default -> {
                if(args.length > 1) {
                    return false;
                }
                try {

                    Organisation org = organisationHandler.checkOrganisation(args[0], (Player) sender);

                    if(org != null) {

                        StringBuilder sb = new StringBuilder();

                        sb.append("[§Organisations§r] Organisation info\n");
                        sb.append("Name: §6").append(org.getName()).append("\n");
                        sb.append("Description: §7").append(org.getDescription()).append("\n");
                        sb.append("Members: ").append("\n");

                        List<Player> onlineMembers = organisationHandler.getOnlineMembers(org.getName());
                        sb.append("§aONLINE §r- ");
                        for(Player player : onlineMembers) {
                            sb.append(player.getName()).append(", ");
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append("\n");
                        sb.append("§8OFFLINE §r- §8");
                        for(User member : org.getMembers()) {
                            boolean online = false;
                            for(Player player : onlineMembers) {
                                if(player.getName().equals(member.getUsername())) {
                                    online = true;
                                    break;
                                }
                            }
                            if(!online) {
                                sb.append(member.getUsername()).append("§r,§8 ");
                            }
                        }
                        sb.deleteCharAt(sb.length() - 1);
                        sb.deleteCharAt(sb.length() - 1);

                        sender.sendMessage(sb.toString());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                // STILL NEED TO DO "-u"

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 1) {
            String[] SUBCOMMANDS = {"create", "invite", "join", "leave", "kick"};
            final List<String> arguments = new ArrayList<>();
            for (String string : SUBCOMMANDS) if (string.toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(string);
            return arguments;
        } else {
            return null;
        }
    }
}
