package fasttravel;

public class FastTravelPoint {
    private String name;
    private int x,y,z,radius;

    public FastTravelPoint(String name, int x, int y, int z, int radius){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}
