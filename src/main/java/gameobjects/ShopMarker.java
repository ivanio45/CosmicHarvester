package gameobjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class ShopMarker extends GameObject {
    private String label = "SHOP";
    private BufferedImage image;

    public ShopMarker(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.color = Color.BLUE;
        loadImage();
    }

    private void loadImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/shop.png");
            image = ImageIO.read(imageStream);
            imageStream.close();
        } catch (IOException e) {
            System.err.println("Не удалось загрузить изображение магазина");
            image = null;
        }
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (image != null) {
            g.drawImage(image, x - worldX, y - worldY, width, height, null);
        } else {
            super.draw(g, worldX, worldY);
            g.setColor(color);
            g.fillRect(x - worldX, y - worldY, width, height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            int textWidth = g.getFontMetrics().stringWidth(label);
            g.drawString(label, x - worldX + width / 2 - textWidth / 2, y - worldY + height / 2 + 5);
        }


    }
}