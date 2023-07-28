package economy.repository;

import db.database;
import economy.model.Organisation;
import economy.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class OrganisationRepository {

    private final database db;
    private List<Organisation> cache;
    // private Map<User, String> roleCache;
    public OrganisationRepository(database db) {
        this.db = db;
        this.cache = new ArrayList<>();
        // this.roleCache = new HashMap<>();
    }
    /*
    public Organisation cached(String name) {
        for(Organisation organisation : cache) {
            if(Objects.equals(organisation.getName(), name)) {
                return organisation;
            }
        }
        return null;
    }

    public String cachedRole(User user) {
        return roleCache.getOrDefault(user, null);
    }
    */
    public Organisation fetchOrganisation(String name) throws SQLException {
/*
        Organisation cached = cached(name);
        if(cached != null) {
            return cached;
        }
*/
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM organisation WHERE name = ?");
        statement.setString(1, name);

        ResultSet resultSet = statement.executeQuery();
        Organisation organisation;
        if (resultSet.next()) {
            String description = resultSet.getString("description");
            int money = resultSet.getInt("money");
            int id = resultSet.getInt("id");

            List<User> members = fetchOrganisationMembersbyId(String.valueOf(id));

            organisation = new Organisation(id, name, description, members, money);
            statement.close();
            return organisation;
        }

        statement.close();
        return null;
    }

    public List<User> fetchOrganisationMembers(String organisationName) throws SQLException {
        if(fetchOrganisation(organisationName) == null) {
            return null;
        }
/*
        Organisation cached = cached(organisationName);
        if(cached != null) {
            return cached.getMembers();
        }
*/
        List<User> members = new ArrayList<>();
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT user.* FROM user " +
                "JOIN memberlist ON user.player_uuid = memberlist.uuid " +
                "JOIN organisation ON organisation.id = memberlist.organisationid " +
                "WHERE organisation.id = ?");
        statement.setString(1, String.valueOf(fetchOrganisation(organisationName).getId()));

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("player_uuid"));
            String username = resultSet.getString("username");
            int money = resultSet.getInt("money");
            members.add(new User(uuid, username, money));
        }

        statement.close();
        return members;
    }

    public List<User> fetchOrganisationMembersbyId(String organisationId) throws SQLException {
        List<User> members = new ArrayList<>();
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT user.* FROM user " +
                "JOIN memberlist ON user.player_uuid = memberlist.uuid " +
                "JOIN organisation ON organisation.id = memberlist.organisationid " +
                "WHERE organisation.id = ?");
        statement.setString(1, organisationId);

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            UUID uuid = UUID.fromString(resultSet.getString("player_uuid"));
            String username = resultSet.getString("username");
            int money = resultSet.getInt("money");
            members.add(new User(uuid, username, money));
        }

        statement.close();
        return members;
    }

    public String fetchRole(String organisationName, User user) throws SQLException{
/*
        String cached = cachedRole(user);
        if(cached != null) {
            return cached;
        }
*/
        PreparedStatement checkStatement = db.getConnection().prepareStatement("SELECT * FROM memberlist WHERE uuid = ? AND organisationid = ?");
        checkStatement.setString(1, user.getUuid().toString());
        checkStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
        ResultSet resultSet = checkStatement.executeQuery();
        String result;
        if (resultSet.next()) {
            result = resultSet.getString("role");
        } else {
            result = null;
        }

        checkStatement.close();
        return result;
    }

    public void createOrganisation(Organisation organisation) throws SQLException {

        //cache.add(organisation);

        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO organisation (name, description, money) VALUES (?, ?, ?)");
        statement.setString(1, organisation.getName());
        statement.setString(2, organisation.getDescription());
        statement.setInt(3, organisation.getMoney());
        statement.executeUpdate();
        statement.close();
    }
    public void updateOrganisation(Organisation organisation) throws SQLException {
/*
        Organisation cached = cached(organisation.getName());
        if(cached != null) {
            cache.set(cache.indexOf(cached), organisation);
        }
*/
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE organisation SET name = ? description = ?, money = ? WHERE id = ?");
        statement.setString(1, organisation.getName());
        statement.setString(2, organisation.getDescription());
        statement.setInt(3, organisation.getMoney());
        statement.setString(4, String.valueOf(organisation.getId()));
        statement.executeUpdate();
        statement.close();
    }
    public void deleteOrganisation(Organisation organisation) throws SQLException {
/*
        Organisation cached = cached(organisation.getName());
        if(cached != null) {
            cache.remove(organisation);
        }
*/
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM organisation WHERE id = ?");
        statement.setString(1, String.valueOf(organisation.getId()));
        statement.executeUpdate();
        statement.close();
    }

    public List<Organisation> getAllOrganisations() throws SQLException {
        List<Organisation> organisations = new ArrayList<>();
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM organisation");

        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            int money = resultSet.getInt("money");
            int id = resultSet.getInt("id");
            List<User> members = fetchOrganisationMembers(name);

            organisations.add(new Organisation(id, name, description, members, money));
        }

        statement.close();
        return organisations;
    }

    public void insertMemberList(String organisationName, User member, String role) throws SQLException {
/*
        Organisation cached = cached(organisationName);
        if(cached != null) {
            if(!cached.getMembers().contains(member)) {
                cached.getMembers().add(member);
            }
        }
*/
        PreparedStatement checkStatement = db.getConnection().prepareStatement("SELECT * FROM memberlist WHERE uuid = ? AND organisationid = ?");
        checkStatement.setString(1, member.getUuid().toString());
        checkStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
        ResultSet resultSet = checkStatement.executeQuery();

        if (!resultSet.next()) {
            PreparedStatement insertStatement = db.getConnection().prepareStatement("INSERT INTO memberlist (uuid, organisationid, role) VALUES (?, ?, ?)");
            insertStatement.setString(1, member.getUuid().toString());
            insertStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
            insertStatement.setString(3, role);
            insertStatement.executeUpdate();
            insertStatement.close();
        }

        checkStatement.close();
    }

    public boolean exitMemberList(String organisationName, User member) throws SQLException {
/*
        Organisation cached = cached(organisationName);
        if(cached != null) {
            if(cached.getMembers().contains(member)) {
                cached.getMembers().remove(member);
            }
        }
*/
        PreparedStatement checkStatement = db.getConnection().prepareStatement("SELECT * FROM memberlist WHERE uuid = ? AND organisationid = ?");
        checkStatement.setString(1, member.getUuid().toString());
        checkStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
        ResultSet resultSet = checkStatement.executeQuery();
        boolean exited = false;
        if (resultSet.next()) {
            PreparedStatement insertStatement = db.getConnection().prepareStatement("DELETE FROM memberlist WHERE uuid = ? AND organisationid = ?");
            insertStatement.setString(1, member.getUuid().toString());
            insertStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
            insertStatement.executeUpdate();
            insertStatement.close();
            exited = true;
        }

        checkStatement.close();
        return exited;
    }
}
