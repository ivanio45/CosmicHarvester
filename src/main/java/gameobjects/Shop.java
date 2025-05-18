package gameobjects;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.awt.*;
import java.io.InputStream;

public class Shop {
    private final Player player;
    private Image backgroundImage;
    private final int windowWidth;
    private final int windowHeight;

    public Shop(Player player, int windowWidth, int windowHeight) {
        this.player = player;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        try {
            InputStream imageStream = getClass().getResourceAsStream("/medfrighter.png");
            if (imageStream != null) {
                backgroundImage = ImageIO.read(imageStream);
            } else {
                System.err.println("Не удалось найти фоновое изображение магазина: shop_background");
                backgroundImage = null;
            }
        } catch (IOException e) {
            System.err.println("Не удалось загрузить фоновое изображение магазина: " + e.getMessage());
            backgroundImage = null;
        }
    }

    private void drawStringWithOutline(Graphics g, String text, int x, int y, Color textColor, Color outlineColor, int outlineThickness) {
        g.setColor(outlineColor);
        g.drawString(text, x - outlineThickness, y); // Слева
        g.drawString(text, x + outlineThickness, y); // Справа
        g.drawString(text, x, y - outlineThickness); // Сверху
        g.drawString(text, x, y + outlineThickness); // Снизу
        g.setColor(textColor);
        g.drawString(text, x, y);
    }

    public void draw(Graphics g) {
        int shopWidth = 800;
        int shopHeight = 600;
        int shopX = (windowWidth - shopWidth) / 2;
        int shopY = (windowHeight - shopHeight) / 2;
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, shopX, shopY, shopWidth, shopHeight, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillRect(shopX, shopY, shopWidth, shopHeight);
        }


        String shopText = "Shop";
        g.setFont(new Font("Impact", Font.BOLD, 36));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(shopText);
        int textX = (windowWidth - textWidth) / 2;
        int textY = shopY + 50;


        drawStringWithOutline(g, shopText, textX, textY, Color.MAGENTA, Color.BLACK, 4);


        g.setFont(new Font("Impact", Font.PLAIN, 22));
        int textStartX = shopX + 210;
        int textStartY = shopY + 150;
        int lineHeight = 35;

        drawStringWithOutline(g, "1 - Upgrade damage (+10): 50 score| " + player.getDamagePoints() + "/7",
                textStartX, textStartY, Color.MAGENTA, Color.BLACK, 1);
        drawStringWithOutline(g, "2 - Upgrade health (+20): 30 score| " + player.getHealthPoints() + "/3",
                textStartX, textStartY + lineHeight, Color.MAGENTA, Color.BLACK, 1);
        drawStringWithOutline(g, "3 - Upgrade speed (+1): 100 score| " + player.getSpeedPoints() + "/5",
                textStartX, textStartY + 2 * lineHeight, Color.MAGENTA, Color.BLACK, 1);
        drawStringWithOutline(g, "4 - Buy Rocket (+1) : 150 score",
                textStartX, textStartY + 3 * lineHeight, Color.MAGENTA, Color.BLACK, 1);
        drawStringWithOutline(g, "Press 1, 2, 3, 4 to buy, Press ESC to exit",
                textStartX, textStartY + 4 * lineHeight, Color.MAGENTA, Color.BLACK, 1);
    }
}