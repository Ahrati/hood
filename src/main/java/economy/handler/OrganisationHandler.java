package economy.handler;

import economy.model.Organisation;
import economy.model.User;
import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

public class OrganisationHandler {
    private final PlayerRepository prepo;
    private final OrganisationRepository orepo;

    private Map<String, ArrayList<String>> invited;
    FileConfiguration config;

    public OrganisationHandler(PlayerRepository prepo, OrganisationRepository orepo, FileConfiguration config) {
        this.prepo = prepo;
        this.orepo = orepo;
        this.config = config;
        invited = new HashMap<>();
    }

    public void createOrganisation(String org, String desc, Player creator) throws SQLException {
        List<Organisation> orgs = orepo.getAllOrganisations();
        int counter = 0;
        for(Organisation organ : orgs) {
            if(Objects.equals(organ.getName(), org)) {
                creator.sendMessage("Organisation with that name already exists!");
                return;
            }
            List<User> mem = organ.getMembers();
            for(User user : mem) {
                if(Objects.equals(user.getUsername(), creator.getName())) {
                    counter++;
                    break;
                }
            }
        }
        if(counter >= config.getInt("maxOrg")) {
            creator.sendMessage("You can only be a part of " + config.getInt("maxOrg") + " organisations.");
            return;
        }

        Organisation organisation = new Organisation(0, org, desc, null, 0);
        orepo.createOrganisation(organisation);
        orepo.insertMemberList(org, prepo.getPlayer(creator));
    }

    public void inviteMember(Player inviter, String username, String org) throws SQLException {
        if(invited.containsKey(username)) {
            if(invited.get(username).contains(org)) {
                inviter.sendMessage("This user has alreay been invited");
                return;
            } else {
                invited.get(username).add(org);
            }
        } else {
            ArrayList<String> organisation = new ArrayList<>();
            organisation.add(org);
            invited.put(username, organisation);
        }
        inviter.sendMessage("User has been invited");
    }

    public boolean joinOrganisation(String org, Player joiner) throws SQLException {
        List<Organisation> orgs = orepo.getAllOrganisations();
        int counter = 0;
        for(Organisation organ : orgs) {
            List<User> mem = organ.getMembers();
            for(User user : mem) {
                if(Objects.equals(user.getUsername(), joiner.getName())) {
                    counter++;
                    break;
                }
            }
        }
        if(counter >= config.getInt("maxOrg")) {
            joiner.sendMessage("You can only be a part of " + config.getInt("maxOrg") + " organisations.");
            return false;
        }
        if(orepo.fetchOrganisation(org).getMembers().size() == config.getInt("maxOrgMembers")) {
            joiner.sendMessage("This organisation is full.");
            return false;
        }

        if(invited.get(joiner.getName()).contains(org)) {
            orepo.insertMemberList(org, prepo.getPlayer(joiner));
            joiner.sendMessage("You successfully joined the org");
        } else {
            joiner.sendMessage("You haven't been invited to this organisation yet");
        }

        return false;
    }

    public boolean leaveOrganisation(String org) {
        //attempt leave org
        return false;
    }

    public boolean updateOrganisation() {
        //if 0 members, delete org
        return false;
    }

    public void updateOrganisationInfo(Organisation organisation) {

    }

    public Organisation checkOrganisation(String name, Player checker) throws SQLException{
        if(!orepo.fetchOrganisationMembers(name).contains(prepo.fetchPlayer(checker.getName()))) {
            checker.sendMessage("You aren't a member of that org");
            return null;
        }
        return orepo.fetchOrganisation(name);
    }
}
