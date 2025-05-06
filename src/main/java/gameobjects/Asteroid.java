package gameobjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Asteroid extends GameObject {

    private static final Map<AsteroidType, String> IMAGE_PATHS = new HashMap<>();
    static {
        IMAGE_PATHS.put(AsteroidType.NORMAL, "/asteroid_normal.png");
        IMAGE_PATHS.put(AsteroidType.RARE, "/asteroid_rare.png");
        IMAGE_PATHS.put(AsteroidType.DANGEROUS, "/asteroid_dangerous.png");
        IMAGE_PATHS.put(AsteroidType.HEALTH_PACK, "/health_a.png");
        IMAGE_PATHS.put(AsteroidType.SHIELD, "/shield_a.png");
        IMAGE_PATHS.put(AsteroidType.BLACK_HOLE, "/black_hole.png");
    }

    public enum AsteroidType {
        NORMAL,
        RARE,
        DANGEROUS,
        HEALTH_PACK,
        SHIELD,
        BLACK_HOLE
    }

    private AsteroidType type = AsteroidType.NORMAL;
    private BufferedImage image;

    public Asteroid(int x, int y, int width, int height,AsteroidType type) {
        super(x, y, width, height);
        this.type = type;
        loadImage();
    }


    protected void loadImage() {
        String imagePath = IMAGE_PATHS.get(type);
        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            image = ImageIO.read(imageStream);
            imageStream.close();
            if (image == null) {
                System.err.println("Не удалось загрузить изображение: " + imagePath);
                color = getColor();
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения: " + imagePath);
            e.printStackTrace();
            color = getColor();
        } catch (IllegalArgumentException e) {
            System.err.println("Неверный путь к изображению: " + imagePath);
            e.printStackTrace();
            color = getColor();
        }
    }

    public AsteroidType getType() {
        return type;
    }


    public BufferedImage getImage() {
        return image;
    }

    public Color getColor() {
        switch (type) {
            case NORMAL:
                return Color.GRAY;
            case RARE:
                return Color.CYAN;
            case DANGEROUS:
                return Color.ORANGE;
            case HEALTH_PACK:
                return Color.GREEN;
            case SHIELD:
                return Color.PINK;
            default:
                return Color.GRAY;
        }
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY){
        if (image != null) {
            g.drawImage(image, x - worldX, y - worldY, width, height, null);
        } else {
            g.setColor(color);
            g.fillRect(x - worldX, y - worldY, width, height);
        }
    }
}