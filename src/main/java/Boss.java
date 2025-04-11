import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class Boss extends GameObject {
    private int speed;
    private BufferedImage image;
    private int health;
    private int maxHealth;
    private int damage = 50; // Урон босса
    private int experienceReward = 500;// Награда за убийство босса
    private int delay = 50;

    public Boss(int x, int y, int width, int height, int health, int speed) {
        super(x, y, width, height);
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.color = Color.BLUE;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File("resources/boss1.png"));
            if (image == null) {
                System.err.println("Не удалось загрузить изображение босса");
                color = Color.MAGENTA;
            }
        } catch (IOException e) {
            System.err.println("Ошибка загрузки изображения босса");
            e.printStackTrace();
            color = Color.MAGENTA; // Цвет по умолчанию
        } catch (IllegalArgumentException e) {
            System.err.println("Неверный путь к изображению босса");
            e.printStackTrace();
            color = Color.MAGENTA; // Цвет по умолчанию
        }
    }

    public void update(int playerX, int playerY) {
        if (delay > 0) {
            delay--;
            return;
        }

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
        g.fillRect(x - worldX, y - worldY - 10, width, 10);
        g.setColor(Color.GREEN);
        g.fillRect(x - worldX, y - worldY - 10, (int) (width * ((double) health / maxHealth)), 10);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public int getDamage() {
        return damage;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
}