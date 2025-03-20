import java.awt.*;

class Bullet extends GameObject {
    private int speed = 10;

    public Bullet(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = Color.WHITE;
    }

    public void update() {
        x += speed;
    }
}