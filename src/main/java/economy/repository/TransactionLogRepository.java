package economy.repository;

import db.database;

public class TransactionLogRepository {
    private final database db;
    public TransactionLogRepository(database db) {
        this.db = db;
    }
}
