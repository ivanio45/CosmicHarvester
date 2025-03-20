import java.awt.*;

class Enemy extends GameObject {
    private int speed = 2;

    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = Color.RED;
    }

    public void update(int playerX, int playerY) {
        if (x < playerX) x += speed;
        if (x > playerX) x -= speed;
        if (y < playerY) y += speed;
        if (y > playerY) y -= speed;
    }
}