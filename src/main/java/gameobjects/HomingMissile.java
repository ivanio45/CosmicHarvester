package gameobjects;

import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.geom.AffineTransform;
import java.io.InputStream;

public class HomingMissile extends Bullet {

    private final GameObject target;
    private BufferedImage image;
    private double angle_i;
    private final int damage = 50;
    private final int changeSpeed = 7;
    private final int targetWidth = 40;
    private final int targetHeight = 60;


    public HomingMissile(int x, int y, GameObject target) {
        super(x, y, target.getX(), target.getY());
        this.target = target;
        this.width = 16;
        this.height = 16;
        this.color = Color.ORANGE;
        loadImage();
    }



    public void update() {
        if (target != null) {
            double dx = target.getX() - x;
            double dy = target.getY() - y;
            double angle = Math.atan2(dy, dx);

            x += (int) (changeSpeed * Math.cos(angle));
            y += (int) (changeSpeed * Math.sin(angle));
            angle_i = Math.atan2(dy, dx);
        } else {
            x += changeSpeed;
        }
    }

    public int getDamage() {
        return damage;
    }

    public GameObject getTarget() {
        return target;
    }

    private void loadImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/rocket.png");
            BufferedImage originalImage = ImageIO.read(imageStream);
            imageStream.close();
            if (originalImage != null) {
                image = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
                g.dispose();

                width = targetWidth;
                height = targetHeight;
            } else {
                System.err.println("Не удалось загрузить изображение самонаводящейся ракеты");
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения самонаводящейся ракеты");
            image = null;
        }
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (image != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.rotate(angle_i,  width / 2.0, height / 2.0);
            g2d.drawImage(image, transform, null);
            g2d.dispose();
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }


}