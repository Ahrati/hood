package economy.repository;

import db.database;
import economy.model.Organisation;
import economy.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrganisationRepository {

    private final database db;

    public OrganisationRepository(database db) {
        this.db = db;
    }
    public Organisation fetchOrganisation(String name) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM organisation WHERE name = ?");
        statement.setString(1, name);

        ResultSet resultSet = statement.executeQuery();
        Organisation organisation;
        if (resultSet.next()) {
            String description = resultSet.getString("description");
            int money = resultSet.getInt("money");
            int id = resultSet.getInt("id");
            List<User> members = fetchOrganisationMembers(name);

            organisation = new Organisation(id, name, description, members, money);
            statement.close();
            return organisation;
        }

        statement.close();
        return null;
    }

    public List<User> fetchOrganisationMembers(String organisationName) throws SQLException {
        List<User> members = new ArrayList<>();
        PreparedStatement statement = db.getConnection().prepareStatement("SELECT user.* FROM user " +
                "JOIN memberlist ON user.player_uuid = memberlist.uuid " +
                "JOIN organisation ON organisation.id = memberlist.organisationid " +
                "WHERE organisation.name = ?");
        statement.setString(1, organisationName);

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

    public void createOrganisation(Organisation organisation) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO organisation (name, description, money) VALUES (?, ?, ?)");
        statement.setString(1, organisation.getName());
        statement.setString(2, organisation.getDescription());
        statement.setInt(3, organisation.getMoney());
        statement.executeUpdate();
        statement.close();
    }
    public void updateOrganisation(Organisation organisation) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("UPDATE organisation SET description = ?, money = ? WHERE name = ?");
        statement.setString(1, organisation.getDescription());
        statement.setInt(2, organisation.getMoney());
        statement.setString(3, organisation.getName());
        statement.executeUpdate();
        statement.close();
    }
    public void deleteOrganisation(Organisation organisation) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM organisation WHERE name = ?");
        statement.setString(1, organisation.getName());
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

    public void insertMemberList(String organisationName, User member) throws SQLException {
        // Check if the user is already associated with the organisation
        PreparedStatement checkStatement = db.getConnection().prepareStatement("SELECT * FROM memberlist WHERE uuid = ? AND organisationid = ?");
        checkStatement.setString(1, member.getUuid().toString());
        checkStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
        ResultSet resultSet = checkStatement.executeQuery();

        if (!resultSet.next()) {
            // If not, create a new association
            PreparedStatement insertStatement = db.getConnection().prepareStatement("INSERT INTO memberlist (uuid, organisationid) VALUES (?, ?)");
            insertStatement.setString(1, member.getUuid().toString());
            insertStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
            insertStatement.executeUpdate();
            insertStatement.close();
        }

        checkStatement.close();
    }

    public void exitMemberList(String organisationName, User member) throws SQLException {
        // Check if the user is already associated with the organisation
        PreparedStatement checkStatement = db.getConnection().prepareStatement("SELECT * FROM memberlist WHERE uuid = ? AND organisationid = ?");
        checkStatement.setString(1, member.getUuid().toString());
        checkStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
        ResultSet resultSet = checkStatement.executeQuery();

        if (resultSet.next()) {
            // If yes, delete association
            PreparedStatement insertStatement = db.getConnection().prepareStatement("INSERT INTO memberlist (uuid, organisationid) VALUES (?, ?)");
            insertStatement.setString(1, member.getUuid().toString());
            insertStatement.setString(2, String.valueOf(fetchOrganisation(organisationName).getId()));
            insertStatement.executeUpdate();
            insertStatement.close();
        }

        checkStatement.close();
    }
}
