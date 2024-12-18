package economy.model;

import java.sql.Timestamp;

public class TransactionLog {
    public int tId;
    public String mode;
    public String from;
    public String to;
    public int amount;
    public String description;
    public Timestamp datetime;

    public TransactionLog(int tId, String mode, String from, String to, int amount, String description, Timestamp datetime) {
        this.tId = tId;
        this.mode = mode;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.description = description;
        this.datetime = datetime;
    }

    public Timestamp getDatetime() {
        return datetime;
    }

    public void setDatetime(Timestamp datetime) {
        this.datetime = datetime;
    }

    public int gettId() {
        return tId;
    }

    public void settId(int tId) {
        this.tId = tId;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
