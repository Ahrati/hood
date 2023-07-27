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

public class orgCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    private final OrganisationHandler organisationHandler;

    public orgCommand(MoneyHandler moneyHandler, OrganisationHandler organisationHandler) {
        this.moneyHandler = moneyHandler;
        this.organisationHandler = organisationHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            // /org
            StringBuilder sb = new StringBuilder();
            sb.append("[Organisations]\n");
            sb.append("You are a in " + "0" + "/" + "5" + " organisations\n");
            try {
                for(Organisation org : organisationHandler.getOrganisationsByMember((Player) sender)) {
                    sb.append(org.getName()).append("  - §8").append(organisationHandler.getOnlineMembers(org.getName()).size()).append("/").append(organisationHandler.getAllMembers(org.getName()).size()).append(" members online\n§f");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sender.sendMessage("Couldn't get organisation info");
                return true;
            }
            sender.sendMessage(sb.toString());
            return true;
        }
        switch(args[0]) {
            case "help" -> {
                // /org help
                String str = """
                        [Organisations]
                        /org - general info
                        /org <org-name> - info about particular org
                        /org create <org-name> <description> - create org
                        /org invite <name> <org-name> - invite people to org
                        /org join <org-name> - join an org
                        /org leave <org-name> - leave org
                        /org kick <name> <org-name> - kick person from org""";
                sender.sendMessage(str);
                return true;
            }
            case "create" -> {
                // /org create <name> [description]
                String desc;
                if(args.length < 2) {
                    return false;
                }
                if(args.length == 3) {
                    desc = args [2];
                } else {
                    desc = "";
                }
                try {
                    organisationHandler.createOrganisation(args[1], desc, (Player) sender);
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Error creating organisation");
                }
                return true;
            }
            case "join" -> {
                // /org join <name>
                if(args.length > 2) {
                    return false;
                }
                try {
                    if(organisationHandler.joinOrganisation(args[1], (Player) sender, "member")) {
                        //send to all online members of org that player has joined
                        List<Player> members = organisationHandler.getOnlineMembers(args[1]);
                        for(Player member : members) {
                            member.sendMessage(sender.getName() + " has joined " + args[1] + ".");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case "leave" -> {
                // /org leave <org>
                if(args.length > 2) {
                    return false;
                }
                try {
                    if(!organisationHandler.leaveOrganisation(args[1], sender.getName())) {
                        sender.sendMessage("You are not part of that organisation.");
                    } else {
                        //send to all online members of that org that player left
                        List<Player> members = organisationHandler.getOnlineMembers(args[1]);
                        for(Player member : members) {
                            member.sendMessage(sender.getName() + " has left " + args[1] + ".");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Couldn't leave organisation.");
                }

                return true;
            }
            case "invite" -> {

                // /org invite <name> <org>
                if(args.length != 3) {
                    return false;
                }
                try {
                    if(organisationHandler.isOwner((Player) sender, args[2])) {
                        sender.sendMessage("You are not the owner of this organisation");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    organisationHandler.inviteMember((Player) sender, args[1], args[2]);

                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Couldn't invite player to organisation.");
                }
                return true;
            }
            case "kick" -> {

                // /org kick <name> <org>
                if(args.length != 3) {
                    return false;
                }
                try {
                    if(organisationHandler.isOwner((Player) sender, args[2])) {
                        sender.sendMessage("You are not the owner of this organisation");
                        return true;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    if(!organisationHandler.leaveOrganisation(args[2], args[1])) {
                        sender.sendMessage("User doesn't exist or isn't part of that organisation.");
                    } else {
                        //send to all online members of that org that player got kicked
                        List<Player> members = organisationHandler.getOnlineMembers(args[1]);
                        for(Player member : members) {
                            member.sendMessage(args[1] + " has been kicked from " + args[2] + ".");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Couldn't invite player to organisation.");
                }
                return true;
            }
            default -> {
                // /org <name> [-u [name <new-name>] [desc <new-description>]]
                if(args.length > 1) {
                    return false;
                }
                try {
                    Organisation org = organisationHandler.checkOrganisation(args[0], (Player) sender);
                    if(org != null) {
                        sender.sendMessage(org.getName() + "\n" + org.getDescription() + "\nmembers:");
                        List<Player> onlineMembers = organisationHandler.getOnlineMembers(org.getName());
                        StringBuilder sb = new StringBuilder();
                        for(User member : org.getMembers()) {
                            for(Player player : onlineMembers) {
                                if(player.getName() == member.getUsername()) {
                                    sb.append("§6");
                                    break;
                                } else {
                                    sb.append("§7");
                                }
                            }
                            sb.append(member.getUsername()).append("\n");
                        }
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
            List<Organisation> ORGANISATIONS;
            try {
                ORGANISATIONS = organisationHandler.getOrganisationsByMember((Player) sender);
            } catch (SQLException e) {
                e.printStackTrace();
                ORGANISATIONS = null;
            }

            final List<String> arguments = new ArrayList<>();
            for (String string : SUBCOMMANDS) if (string.toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(string);
            if(ORGANISATIONS != null) {
                for(Organisation org : ORGANISATIONS) if(org.getName().toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(org.getName());
            }
            return arguments;
        } else if(args.length == 3) {
            final List<String> arguments = new ArrayList<>();
            if(Objects.equals(args[0], "invite") || Objects.equals(args[0], "kick")) {
                List<Organisation> ORGANISATIONS;
                try {
                    ORGANISATIONS = organisationHandler.getOrganisationsByMember((Player) sender);
                } catch (SQLException e) {
                    e.printStackTrace();
                    ORGANISATIONS = null;
                }
                if(ORGANISATIONS != null) {
                    for(Organisation org : ORGANISATIONS) {
                        try {
                            if(org.getName().toLowerCase().startsWith(args[0].toLowerCase()) && organisationHandler.isOwner((Player) sender, org.getName())) arguments.add(org.getName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return arguments;
        } else if(args.length == 2) {
            final List<String> arguments = new ArrayList<>();
            if(Objects.equals(args[0], "leave")) {
                List<Organisation> ORGANISATIONS;
                try {
                    ORGANISATIONS = organisationHandler.getOrganisationsByMember((Player) sender);
                } catch (SQLException e) {
                    e.printStackTrace();
                    ORGANISATIONS = null;
                }
                if(ORGANISATIONS != null) {
                    for(Organisation org : ORGANISATIONS) if(org.getName().toLowerCase().startsWith(args[0].toLowerCase())) arguments.add(org.getName());
                }
            }
            return arguments;
        } else {
            return null;
        }
    }
}
