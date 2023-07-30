package economy.repository;

import db.database;
import economy.model.TransactionLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TransactionLogRepository {
    private final database db;
    public TransactionLogRepository(database db) {
        this.db = db;
    }

    public List<TransactionLog> getTransactions(String name, String type) throws SQLException {
        if(Objects.equals(type, "p")) {

            PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM transactionlog WHERE mode = ? OR mode = ? OR mode = ?");
            statement.setString(1, "p2o");
            statement.setString(2, "o2p");
            statement.setString(3, "p2p");

            ResultSet resultSet = statement.executeQuery();
            List<TransactionLog> transactions = new ArrayList<>();
            while(resultSet.next()) {
                TransactionLog transaction = new TransactionLog(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("mode"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
                        resultSet.getInt("amount"),
                        resultSet.getString("transaction_description"),
                        resultSet.getTimestamp("date")
                );

                Date now = new Date();
                if(TimeUnit.MILLISECONDS.toDays(transaction.getDatetime().getTime() - now.getTime()) > 10) {
                    deleteTransaction(transaction.gettId());
                } else {
                    transactions.add(transaction);
                }

                transactions.removeIf(t -> !(Objects.equals(t.getFrom(), name)
                        && (Objects.equals(t.getMode(), "p2o") || Objects.equals(t.getMode(), "p2p")))
                        && !(Objects.equals(t.getTo(), name)
                        && (Objects.equals(t.getMode(), "o2p") || Objects.equals(t.getMode(), "p2p"))));


            }
            statement.close();
            return transactions;
        } else if(Objects.equals(type, "o")) {

            PreparedStatement statement = db.getConnection().prepareStatement("SELECT * FROM transactionlog WHERE mode = ? OR mode = ? OR mode = ?");
            statement.setString(1, "p2o");
            statement.setString(2, "o2p");
            statement.setString(3, "o2o");

            ResultSet resultSet = statement.executeQuery();
            List<TransactionLog> transactions = new ArrayList<>();
            if(resultSet.next()) {
                TransactionLog transaction = new TransactionLog(
                        resultSet.getInt("transaction_id"),
                        resultSet.getString("mode"),
                        resultSet.getString("sender"),
                        resultSet.getString("receiver"),
                        resultSet.getInt("amount"),
                        resultSet.getString("transaction_description"),
                        resultSet.getTimestamp("date")
                );

                Date now = new Date();
                if(TimeUnit.MILLISECONDS.toDays(transaction.getDatetime().getTime() - now.getTime()) > 10) {
                    deleteTransaction(transaction.gettId());
                } else {
                    transactions.add(transaction);
                }

                transactions.removeIf(t -> !(Objects.equals(t.getFrom(), name)
                        && (Objects.equals(t.getMode(), "o2p") || Objects.equals(t.getMode(), "o2o")))
                        && !(Objects.equals(t.getTo(), name)
                        && (Objects.equals(t.getMode(), "p2o") || Objects.equals(t.getMode(), "o2o"))));
            }

            statement.close();
            return transactions;
        }
        return null;
    }

    public void createTransaction(String from, String to, int amount, String description, String mode) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO transactionlog (mode, sender, receiver, amount, transaction_description, date) VALUES (?, ?, ?, ?, ?, ?)");
        statement.setString(1, mode);
        statement.setString(2, from);
        statement.setString(3, to);
        statement.setInt(4, amount);
        statement.setString(5, description);

        Date date = new Date();
        Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        statement.setTimestamp(6, timestamp);

        statement.executeUpdate();
        statement.close();
    }
    public void deleteTransaction(int id) throws SQLException{
        PreparedStatement statement = db.getConnection().prepareStatement("DELETE FROM transactionlog WHERE transaction_id = ?");
        statement.setString(1, String.valueOf(id));
        statement.executeUpdate();
        statement.close();
    }
}
