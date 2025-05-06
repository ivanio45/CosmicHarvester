package gameobjects;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Enemy extends GameObject implements EnemyInterface {
    protected int speed;
    protected BufferedImage image;
    public int health;
    protected EnemyType type;
    public int maxHealth;

    private static final Map<EnemyType, String> IMAGE_PATHS = new HashMap<>();
    static {
        IMAGE_PATHS.put(EnemyType.NORMAL, "/enemy_0.png");
        IMAGE_PATHS.put(EnemyType.FAST, "/enemy_5.png");
        IMAGE_PATHS.put(EnemyType.TANK, "/enemy_6.png");
    }

    public Enemy(int x, int y, int width, int height, EnemyType type) {
        super(x, y, width, height);
        this.type = type;
        this.color = Color.RED;
        loadEnemyData(type);
        this.maxHealth = health;
        loadImage();
    }

    protected void loadEnemyData(EnemyType type) {
        switch (type) {
            case EnemyType.FAST:
                this.speed = 3;
                this.health = (int)(width / 2);
                break;
            case EnemyType.TANK:
                this.speed = 1;
                this.health = (int) (width / 0.5);
                break;
            default:
                this.speed = 2;
                this.health = width;
                break;
        }
    }

    protected void loadImage() {
        String imagePath = IMAGE_PATHS.get(type);
        try {
            InputStream imageStream = getClass().getResourceAsStream(imagePath);
            image = ImageIO.read(imageStream);
            imageStream.close();
            if (image == null) {
                System.err.println("Не удалось загрузить изображение: " + imagePath);
                color = Color.MAGENTA;
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения: " + imagePath);
            e.printStackTrace();
            color = Color.MAGENTA;
        } catch (IllegalArgumentException e) {
            System.err.println("Неверный путь к изображению: " + imagePath);
            e.printStackTrace();
            color = Color.MAGENTA;
        }
    }


    public void update(int playerX, int playerY) {
        if (x < playerX) x += speed;
        if (x > playerX) x -= speed;
        if (y < playerY) y += speed;
        if (y > playerY) y -= speed;
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (image != null) {
            g.drawImage(image, x - worldX, y - worldY, width, height, null);
        } else {
            g.setColor(color);
            g.fillRect(x - worldX, y - worldY, width, height);
        }
        g.setColor(Color.RED);
        g.fillRect(x - worldX, y - worldY, width, 5);
        int currentHealthWidth = width * health / maxHealth;
        g.setColor(Color.GREEN);
        g.fillRect(x - worldX, y - worldY, currentHealthWidth, 5);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health <= 0;
    }

}