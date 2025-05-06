package gameobjects;

import java.awt.*;

public class Shop {
    private final Player player;

    public Shop(Player player) {
        this.player = player;
    }

    public void draw(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(100, 100, 600, 400);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("gameobjects.Shop", 370, 150);

        g.setFont(new Font("Arial", Font.PLAIN, 16));


        g.drawString("1 - Upgrade damage (+10): 50 score| " + player.getDamagePoints() + "/7", 200, 200);
        g.drawString("2 - Upgrade health (+20): 30 score| " + player.getHealthPoints() + "/3", 200, 250);
        g.drawString("3 - Upgrade speed (+1): 100 score| " + player.getSpeedPoints() + "/5", 200, 300);
        g.drawString("4 - Buy Rocket (+1) : 150 score", 200, 350);
        g.drawString("Press 1, 2, 3, 4 to buy, Press ESC to exit", 200, 400);
    }
}