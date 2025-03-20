import java.awt.*;

class Shop {

    private GamePanel gamePanel;

    public Shop(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(100, 100, 600, 400);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Shop", 370, 150);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("1. Upgrade Damage (50 score)", 200, 200);
        g.drawString("2. Upgrade Health (30 score)", 200, 250);
        g.drawString("Press 1 or 2 to buy, Press ESC to exit", 200, 300);
    }
}