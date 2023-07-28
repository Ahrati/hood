package economy.repository;

import db.database;
import economy.model.TransactionLog;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TransactionLogRepository {
    private final database db;
    public TransactionLogRepository(database db) {
        this.db = db;
    }

    public List<TransactionLog> getTransactions() {
        return null;
    }

    public void createTransaction(String from, String to, int amount, String description, String mode) throws SQLException {
        PreparedStatement statement = db.getConnection().prepareStatement("INSERT INTO transactionlog (mode, from, to, amount, transaction-description) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, mode);
        statement.setString(2, from);
        statement.setString(3, to);
        statement.setInt(4, amount);
        statement.setString(5, description);
        statement.executeUpdate();
        statement.close();
    }
}
