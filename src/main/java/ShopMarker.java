import java.awt.*;

class ShopMarker extends GameObject {
    private String label = "Shop";

    public ShopMarker(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = Color.BLUE;
    }

    public void draw(Graphics g, int worldX, int worldY) {
        super.draw(g, worldX, worldY);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        int textWidth = g.getFontMetrics().stringWidth(label);
        g.drawString(label, x - worldX + width / 2 - textWidth / 2, y - worldY + height / 2 + 5);
    }
}