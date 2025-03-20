import java.awt.*;

class Asteroid extends GameObject {

    public enum AsteroidType {
        NORMAL,
        RARE,
        DANGEROUS
    }

    private AsteroidType type = AsteroidType.NORMAL;

    public Asteroid(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = Color.GRAY;
    }

    public AsteroidType getType() {
        return type;
    }

    public void setType(AsteroidType type) {
        this.type = type;
    }
}