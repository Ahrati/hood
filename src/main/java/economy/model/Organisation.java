package economy.model;

import java.util.List;

public class Organisation {
    public String name;
    public String description;
    public List<User> members;
    public int money;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public Organisation(String name, String description, List<User> members, int money) {
        this.name = name;
        this.description = description;
        this.members = members;
        this.money = money;
    }
}
