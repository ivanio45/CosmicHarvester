import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

class Enemy extends GameObject {
    private int speed;
    private BufferedImage image;
    private int health;
    private EnemyType type;

    // Карта, связывающая типы врагов с путями к изображениям
    private static final Map<EnemyType, String> IMAGE_PATHS = new HashMap<>();

    static {
        IMAGE_PATHS.put(EnemyType.NORMAL, "resources/enemy_1.png");
        IMAGE_PATHS.put(EnemyType.FAST, "resources/enemy_2.png");
        IMAGE_PATHS.put(EnemyType.TANK, "resources/enemy_3.png");
    }

    public Enemy(int x, int y, int width, int height, EnemyType type) {
        super(x, y, width, height);
        this.type = type;
        this.color = Color.RED;
        loadEnemyData(type);
        loadImage();
    }

    // Метод для загрузки данных врага (скорость, здоровье)
    private void loadEnemyData(EnemyType type) {
        switch (type) {
            case EnemyType.FAST:
                this.speed = 4;
                this.health = (int) (width / 1.0);
                break;
            case EnemyType.TANK:
                this.speed = 1;
                this.health = (int) (width / 0.5);
                break;
            default: // NORMAL
                this.speed = 2;
                this.health = (int) (width / 1.5);
                break;
        }
    }

    private void loadImage() {
        String imagePath = IMAGE_PATHS.get(type);
        try {
            image = ImageIO.read(new File(imagePath));
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

    public EnemyType getType() {
        return type;
    }

    public void setType(EnemyType type) {
        this.type = type;
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
        g.fillRect(x - worldX, y - worldY - 10, width, 5);
        g.setColor(Color.GREEN);
        g.fillRect(x - worldX, y - worldY - 10, (int) (width * ((double) health / (width / 2))), 5);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }
}