import java.awt.*;

public class HomingMissile extends GameObject {
    private int speed = 7;
    private GameObject target; // Ближайший враг
    private int damage = 50;

    public HomingMissile(int x, int y, int width, int height, GameObject target) {
        super(x, y, width, height);
        this.target = target;
        this.color = Color.ORANGE;
    }

    public void update() {
        if (target != null) {

            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double angle = Math.atan2(dy, dx);

            x += speed * Math.cos(angle);
            y += speed * Math.sin(angle);
        } else {

            x += speed;
        }
    }

    public void draw(Graphics g, int worldX, int worldY) {
        g.setColor(color);
        g.fillRect(x - worldX, y - worldY, width, height);
    }

    public int getDamage() {
        return damage;
    }

    public boolean isOffScreen(int panelWidth, int panelHeight) {
        return x < 0 || x > panelWidth || y < 0 || y > panelHeight;
    }
}