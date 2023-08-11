package stats;

public class StatsRecord {
    private String name;
    private int time;
    private int mobsKilled;
    private int  blocksPlaced;
    private int  blocksDestroyed;
    private int date;

    public StatsRecord(String name, int time, int mobsKilled, int blocksPlaced, int blocksDestroyed, int date) {
        this.name = name;
        this.time = time;
        this.mobsKilled = mobsKilled;
        this.blocksPlaced = blocksPlaced;
        this.blocksDestroyed = blocksDestroyed;
        this.date = date;
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

    public int getBlocksPlaced() {
        return blocksPlaced;
    }

    public void setBlocksPlaced(int blocksPlaced) {
        this.blocksPlaced = blocksPlaced;
    }

    public int getBlocksDestroyed() {
        return blocksDestroyed;
    }

    public void setBlocksDestroyed(int blocksDestroyed) {
        this.blocksDestroyed = blocksDestroyed;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
}
