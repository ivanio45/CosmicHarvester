package gameobjects;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.io.InputStream;


public class Bullet extends GameObject {
    private final double startX;
    private final double startY;
    private boolean isActive = true;
    private final double xDirection;
    private final double yDirection;
    private BufferedImage image;
    private double angle;


    public Bullet(int x, int y, double targetX, double targetY) {
        super(x, y, 5, 5);
        this.startX = x;
        this.startY = y;
        this.color = Color.YELLOW;
        loadImage();

        double dx = targetX - x;
        double dy = targetY - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0) {
            dx /= distance;
            dy /= distance;
        }
        this.xDirection = dx;
        this.yDirection = dy;
        angle = Math.atan2(dy, dx);
    }


    public void update() {
        if (!isActive) return;
        x += (int) (xDirection * 10);
        y += (int) (yDirection * 10);

        double currentDistance = Math.sqrt(Math.pow(x - startX, 2) + Math.pow(y - startY, 2));
        int activeDistance = 800;
        if (currentDistance > activeDistance) {
            isActive = false;
        }
    }

    private void loadImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/bullet.png");
            image = ImageIO.read(imageStream);
            imageStream.close();
            width = image.getWidth();
            height = image.getHeight();
        } catch (IOException e) {
            System.err.println("Не удалось загрузить изображение пули");
            image = null;
        }
    }

    public void setActive(boolean active) {
        isActive = active;
    }


    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();

            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.rotate(angle,  width / 2.0, height / 2.0);
            g2d.drawImage(image, transform, null);
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }



    public boolean isActive() {
        return isActive;
    }
}