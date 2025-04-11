import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Player extends GameObject {
    private int speed = 5;
    private int baseSpeed = 5; // Сохраняем базовую скорость
    private boolean up, down, left, right;
    private int health = 100;
    private int damage = 20;
    private int lowHealthThreshold = 50; // Порог здоровья, при котором скорость снижается
    private double speedReductionPercentage = 0.5;
    private int locationsCrossed = 0;
    private int experience = 0;
    private int experienceToLevelUp = 100;
    private int level = 1; // Добавляем уровень
    private BufferedImage shipImage;
    private GamePanel gamePanel;

    public Player(int x, int y, int width, int height, GamePanel gamePanel) {
        super(x, y, width, height);
        this.gamePanel = gamePanel;
        this.color = Color.GREEN;
        try {
            shipImage = ImageIO.read(new File("resources/SpaceShip.png"));
        } catch (IOException e) {
            System.err.println("Не удалось загрузить изображение корабля: " + e.getMessage());
            shipImage = null;
        }
    }

    public void update(int panelWidth, int panelHeight) {
        int currentSpeed = speed; // Store speed before modification

        if (health < lowHealthThreshold) {
            speed = (int) (baseSpeed * speedReductionPercentage); // Reduce speed if health is low
        } else {
            speed = baseSpeed; // Reset to base speed if health is above the threshold
        }

        // Movement logic with the calculated speed
        if (up) {
            y -= speed;
        }
        if (down) {
            y += speed;
        }
        if (left) {
            x -= speed;
        }
        if (right) {
            x += speed;
        }

        if (x < 0) {
            x = panelWidth;
            locationsCrossed++;
            gamePanel.generateObjectsAroundPlayer();
        }
        if (x > panelWidth) {
            x = 0;
            locationsCrossed++;
            gamePanel.generateObjectsAroundPlayer();
        }
        if (y < 0) {
            y = panelHeight;
            locationsCrossed++;
        }
        if (y > panelHeight) {
            y = 0;
            locationsCrossed++;
        }

        if(currentSpeed != speed){
            gamePanel.repaint(); // update values in shop
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            up = true;
        }
        if (key == KeyEvent.VK_S) {
            down = true;
        }
        if (key == KeyEvent.VK_A) {
            left = true;
        }
        if (key == KeyEvent.VK_D) {
            right = true;
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) {
            up = false;
        }
        if (key == KeyEvent.VK_S) {
            down = false;
        }
        if (key == KeyEvent.VK_A) {
            left = false;
        }
        if (key == KeyEvent.VK_D) {
            right = false;
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getBaseSpeed() {
        return baseSpeed;
    }

    public void setBaseSpeed(int baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public int getLocationsCrossed() {
        return locationsCrossed;
    }

    public void setLocationsCrossed(int locationsCrossed) {
        this.locationsCrossed = locationsCrossed;
    }

    public int getExperience() { // NEW: Getter for experience
        return experience;
    }

    public void setExperience(int experience) { // NEW: Setter for experience
        this.experience = experience;
    }

    public void addExperience(int amount) {
        this.experience += amount;
        // You can add level up logic here if you want
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (shipImage != null) {
            g.drawImage(shipImage, x, y, width, height, null);
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }
}