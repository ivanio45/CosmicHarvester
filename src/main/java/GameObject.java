import java.awt.*;

class GameObject {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Color color = Color.WHITE;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics g, int worldX, int worldY) {
        g.setColor(color);
        g.fillRect(x - worldX, y - worldY, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}