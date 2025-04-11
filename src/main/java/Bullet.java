import java.awt.*;


public class Bullet extends GameObject {
    private int speed = 10;
    private int damage;
    private double targetX;
    private double targetY;
    private double startX;
    private double startY;
    private double distance;
    private boolean isActive = true;


    public Bullet(int x, int y, int width, int height, double targetX, double targetY) {
        super(x, y, width, height);
        this.damage = 20;
        this.targetX = targetX;
        this.targetY = targetY;
        this.startX = x;
        this.startY = y;
        this.color = Color.YELLOW;

        // Calculate direction and distance
        double dx = targetX - x;
        double dy = targetY - y;
        distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;
        }
        this.xDirection = dx;
        this.yDirection = dy;
    }

    private double xDirection;
    private double yDirection;


    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void update() {
        if (!isActive) return;

        x += xDirection * speed;
        y += yDirection * speed;

        double currentDistance = Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));
        if (currentDistance > 800) { // max distance for the bullet
            isActive = false;
        }
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        g.setColor(color);
        g.fillRect(x, y, width, height); // Remove worldX, worldY
    }
}