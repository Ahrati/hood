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
import java.util.List;

public class orgCommand implements TabExecutor {
    private final MoneyHandler moneyHandler;
    private final OrganisationHandler organisationHandler;

    public orgCommand(MoneyHandler moneyHandler, OrganisationHandler organisationHandler) {
        this.moneyHandler = moneyHandler;
        this.organisationHandler = organisationHandler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 3) {
            return false;
        }
        switch(args[0]) {
            case "create" -> {
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
                if(args.length > 1) {
                    return false;
                }
                try {
                    organisationHandler.joinOrganisation(args[1], (Player) sender);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return true;
            }
            case "leave" -> {
                if(args.length > 1) {
                    return false;
                }
                organisationHandler.leaveOrganisation(args[1]);
                return true;
            }
            case "invite" -> {
                if(args.length != 3) {
                    return false;
                }
                try {
                    organisationHandler.inviteMember((Player) sender, args[1], args[2]);
                } catch (SQLException e) {
                    e.printStackTrace();
                    sender.sendMessage("Couldn't invite player to organisation.");
                }
                return true;
            }
            default -> {
                if(args.length > 1) {
                    return false;
                }
                Organisation org = null;
                try {
                    org = organisationHandler.checkOrganisation(args[0], (Player) sender);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if(org != null) {
                    sender.sendMessage(org.getName() + "\n" + org.getDescription() + "\nmembers:");
                    for(User member : org.getMembers()) {
                        sender.sendMessage(member.getUsername());
                    }
                }

                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }
}
