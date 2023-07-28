package economy.handler;

import economy.repository.OrganisationRepository;
import economy.repository.PlayerRepository;
import economy.repository.TransactionLogRepository;

public class TransactionHistoryHandler {
    private final TransactionLogRepository trepo;

    public TransactionHistoryHandler(TransactionLogRepository trepo) {
        this.trepo = trepo;
    }
}
