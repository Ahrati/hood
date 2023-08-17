package stats;

import java.util.Date;

public class StatsRecord {
    private String name;
    private int time;
    private int mobsKilled;
    private int  blocksDestroyed;
    private Date date;

    public StatsRecord(String name, int time, int mobsKilled, int blocksDestroyed, int travel) {
        this.name = name;
        this.time = time;
        this.mobsKilled = mobsKilled;
        this.blocksDestroyed = blocksDestroyed;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void setMobsKilled(int mobsKilled) {
        this.mobsKilled = mobsKilled;
    }

    public int getBlocksDestroyed() {
        return blocksDestroyed;
    }

    public void setBlocksDestroyed(int blocksDestroyed) {
        this.blocksDestroyed = blocksDestroyed;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
