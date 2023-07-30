package economy.handler;

import economy.model.Organisation;
import economy.model.User;
import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class OrganisationHandler {
    private final PlayerRepository prepo;
    private final OrganisationRepository orepo;
    private final MoneyHandler moneyHandler;
    private Map<String, ArrayList<String>> invited;
    FileConfiguration config;

    public OrganisationHandler(PlayerRepository prepo, OrganisationRepository orepo, FileConfiguration config, MoneyHandler moneyHandler) {
        this.prepo = prepo;
        this.orepo = orepo;
        this.config = config;
        this.moneyHandler = moneyHandler;
        invited = new HashMap<>();
    }
    public int getMaxOrg() {
        return config.getInt("maxOrg");
    }
    public List<Organisation> getOrganisationsByMember(Player player) throws SQLException {
        User user = prepo.getPlayer(player);
        List<Organisation> orgs = orepo.getAllOrganisations();
        List<Organisation> result = new ArrayList<>();
        for(Organisation org : orgs) {
            for(User member : org.getMembers()) {
                if(Objects.equals(member.getUuid().toString(), user.getUuid().toString())) {
                    result.add(org);
                }
            }

        }
        return result;
    }
    public List<Organisation> getOrganisations() throws SQLException {
        return orepo.getAllOrganisations();
    }
    public void createOrganisation(String org, String desc, Player creator) throws SQLException {
        List<Organisation> orgs = orepo.getAllOrganisations();
        int counter = 0;
        for(Organisation organ : orgs) {
            if(Objects.equals(organ.getName(), org)) {
                creator.sendMessage("[§dOrganisations§r] §cOrganisation with that name already exists!");
                return;
            }
            List<User> mem = organ.getMembers();
            for(User user : mem) {
                if(Objects.equals(user.getUsername(), creator.getName())) {
                    if(isOwner(creator, organ.getName())) {
                        creator.sendMessage("[§dOrganisations§r] §cYou can only be the owner of one organisation at a time!");
                        return;
                    }
                    counter++;
                    break;
                }
            }
        }
        if(counter >= config.getInt("maxOrg")) {
            creator.sendMessage("[§dOrganisations§r] §cYou can only be a part of " + config.getInt("maxOrg") + " organisations.");
            return;
        }

        Organisation organisation = new Organisation(0, org, desc, null, 0);
        orepo.createOrganisation(organisation);
        orepo.insertMemberList(org, prepo.getPlayer(creator), "owner");
    }

    public void inviteMember(Player inviter, String username, String org) throws SQLException {
        if(!isOrganisation(org)) {
            return;
        }
        if(invited.containsKey(username)) {
            if(invited.get(username).contains(org)) {
                inviter.sendMessage("[§dOrganisations§r] §rThis user has alreay been invited");
                return;
            } else {
                invited.get(username).add(org);
            }
        } else {
            ArrayList<String> organisation = new ArrayList<>();
            organisation.add(org);
            invited.put(username, organisation);
        }
        inviter.sendMessage("[§dOrganisations§r] §aUser has been invited");
        Player invited = getServer().getPlayer(username);
        if(invited != null) {
            invited.sendMessage("[§dOrganisations§r] §aYou have been invited to §6" + org + "\n§rType '/org join " + org + "' to join the organisation");
        }
    }

    public boolean joinOrganisation(String org, Player joiner, String role) throws SQLException {
        if(!isOrganisation(org)) {
            return false;
        }
        if(orepo.fetchOrganisation(org).getMembers().contains(prepo.getPlayer(joiner))) {
            joiner.sendMessage("[§dOrganisations§r] §rYou are already in that organisation.");
            return false;
        }
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
            joiner.sendMessage("[§dOrganisations§r] §cYou can only be a part of " + config.getInt("maxOrg") + " organisations.");
            return false;
        }
        if(orepo.fetchOrganisation(org).getMembers().size() == config.getInt("maxOrgMembers")) {
            joiner.sendMessage("[§dOrganisations§r] §rThis organisation is full.");
            return false;
        }

        if(invited.get(joiner.getName()).contains(org)) {
            orepo.insertMemberList(org, prepo.getPlayer(joiner), role);
            invited.get(joiner.getName()).remove(org);
            joiner.sendMessage("[§dOrganisations§r] §aYou successfully joined the org");
            return true;
        } else {
            joiner.sendMessage("[§dOrganisations§r] §rYou haven't been invited to this organisation yet");
        }

        return false;
    }

    public boolean leaveOrganisation(String org, String leaver) throws SQLException {
        //attempt leave org
        if(!isOrganisation(org)) {
            return false;
        }
        User player = prepo.fetchPlayer(leaver);
        if(player == null) {
            return false;
        }
        if(orepo.exitMemberList(org, player)) {
            updateOrganisation(org);
            return true;
        }
        return false;

    }

    public boolean updateOrganisation(String org) throws SQLException {
        //if 0 members, delete org
        Organisation organisation = orepo.fetchOrganisation(org);
        if(organisation.getMembers().size() == 0) {
            orepo.deleteOrganisation(organisation);

            //MONEY HANDLER
            moneyHandler.transferMoney(org, "government", organisation.getMoney(), "disbanded", "o2o");

            return true;
        }
        return false;
    }

    public void updateOrganisationInfo(Organisation organisation) {

    }

    public Organisation checkOrganisation(String name, Player checker) throws SQLException{
        List<User> orgMembers = orepo.fetchOrganisationMembers(name);
        if(orgMembers == null) {
            checker.sendMessage("[§dOrganisations§r] §cOrganisation with that name doesn't exist");
            return null;
        }
        for(User member : orgMembers) {
            if(Objects.equals(member.getUsername(), checker.getName())) {
                return orepo.fetchOrganisation(name);
            }
        }
        return null;
    }

    public List<Player> getOnlineMembers(String org) throws SQLException {
        if(!isOrganisation(org)) {
            return null;
        }
        List<User> members = getAllMembers(org);
        List<Player> onlineMembers = new ArrayList<>();
        for(User member : members) {
            if(getServer().getPlayer(member.getUsername()) != null) {
                onlineMembers.add(getServer().getPlayer(member.getUsername()));
            }
        }

        return onlineMembers;
    }

    public List<User> getAllMembers(String org) throws SQLException {
        if(!isOrganisation(org)) {
            return null;
        }
        return orepo.fetchOrganisation(org).getMembers();
    }

    public User getOwner(String org) throws SQLException{
        for(User user : orepo.fetchOrganisationMembers(org)) {
            System.out.println(orepo.fetchRole(org, user) + user.getUsername());
            if(Objects.equals(orepo.fetchRole(org, user), "owner")) {
                return user;
            }
        }
        return null;
    }

    public boolean isOwner(Player player, String org) throws SQLException{
        if(!isOrganisation(org)) {
            return false;
        }
        User owner = getOwner(org);
        if(owner == null) {
            return false;
        } else {
            return player.getName().equals(owner.getUsername());
        }
    }

    public boolean isOrganisation(String org) throws SQLException{
        return orepo.fetchOrganisation(org) != null;
    }
}
