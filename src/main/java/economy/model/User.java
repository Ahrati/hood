package economy.model;

import java.util.UUID;

public class User {
    public UUID uuid;
    public String username;
    public int money;

    public User(UUID uuid, String username, int money) {
        this.uuid = uuid;
        this.username = username;
        this.money = money;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }
}
