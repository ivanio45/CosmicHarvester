package gameobjects;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Boss extends Enemy implements EnemyInterface {

    private final int bossDamage = 50;

    public Boss(int x, int y, int width, int height, int health, EnemyType type ) {
        super(x, y, width, height, type);
        this.health = health;
        this.maxHealth = health;
        loadImage();
    }

    @Override
    protected void loadImage() {
        try {
            InputStream imageStream = getClass().getResourceAsStream("/BOSS.png");
            image = ImageIO.read(imageStream);
            imageStream.close();
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

    public int getDamage() {
        return bossDamage;
    }
}