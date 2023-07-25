package economy.model;

import java.util.List;

public class Organisation {
    public int id;

    public String name;
    public String description;
    public List<User> members;
    public int money;
    public Organisation(int id, String name, String description, List<User> members, int money) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.members = members;
        this.money = money;
    }
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
