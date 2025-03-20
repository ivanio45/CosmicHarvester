import java.awt.*;
import java.util.Random;

class Resource extends GameObject {
    private int dx;
    private int dy;
    private int speed = 1;

    public Resource(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = Color.YELLOW;
        Random random = new Random();
        dx = random.nextInt(2 * speed + 1) - speed;
        dy = random.nextInt(2 * speed + 1) - speed;
    }

    public void update() {
        x += dx;
        y += dy;

        if (x <= 0 || x + width >= 800) {
            dx = -dx;
        }

        if (y <= 0 || y + height >= 600) {
            dy = -dy;
        }
    }
}