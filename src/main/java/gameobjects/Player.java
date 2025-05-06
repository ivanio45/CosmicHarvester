package gameobjects;

import panels.GamePanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class Player extends GameObject {
    private int speed = 5;
    private int baseSpeed = 5;
    private boolean up, down, left, right;
    private int health = 100;
    private int maxHealth = 100;
    private int damage = 20;
    private int locationsCrossed = 0;
    private BufferedImage shipImage;
    private final GamePanel gamePanel;
    private int healthPoints = 0;
    private int speedPoints = 0;
    private int damagePoints = 0;
    private boolean hasShield = false;
    private int shieldHealth = 30;

    private double angle = 0;
    private double rotationSpeed = 0.15;

    public Player(int x, int y, int width, int height, GamePanel gamePanel) {
        super(x, y, width, height);
        this.gamePanel = gamePanel;
        this.color = Color.GREEN;
        try {
            InputStream imageStream = getClass().getResourceAsStream("/medfrighter.png");
            shipImage = ImageIO.read(imageStream);
            imageStream.close();
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(-270), shipImage.getWidth() / 2.0, shipImage.getHeight() / 2.0);

            BufferedImage rotatedImage = new BufferedImage(shipImage.getHeight(), shipImage.getWidth(), shipImage.getType());
            Graphics2D g = rotatedImage.createGraphics();
            g.transform(transform);
            g.drawImage(shipImage, 0, 0, null);
            g.dispose();

            shipImage = rotatedImage;
        } catch (IOException e) {
            System.err.println("Не удалось загрузить изображение корабля: " + e.getMessage());
            shipImage = null;
        }
    }

    public void update(int panelWidth, int panelHeight) {
        int currentSpeed = speed;

        int lowHealthThreshold = maxHealth / 2;
        if (health < lowHealthThreshold) {
            double speedReductionPercentage = 0.5;
            speed = (int) (baseSpeed * speedReductionPercentage);
        }
        if (up) {
            x += speed * Math.cos(angle);
            y += speed * Math.sin(angle);
        }
        if (down) {
            x -= speed * Math.cos(angle);
            y -= speed * Math.sin(angle);
        }

        if (left) {
            angle -= rotationSpeed;
        }
        if (right) {
            angle += rotationSpeed;
        }

        if (x < 0) {
            x = panelWidth;
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;

            }
        }
        if (x > panelWidth) {
            x = 0;
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }
        if (y < 0) {
            y = panelHeight;
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }
        if (y > panelHeight) {
            y = 0;
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }

        if(currentSpeed != speed){
            gamePanel.repaint();
        }
    }

    public void upgradeHealth() {
        if(healthPoints + 1 == 3){
            healthPoints = 0;
            health += 20;
            maxHealth += 20;
        }
        else{
            healthPoints++;
        }
    }

    public void upgradeSpeed() {
        if(speedPoints + 1 == 5){
            speedPoints = 0;
            speed += 1;
            baseSpeed += 1;
        }
        else{
            speedPoints++;
        }
    }

    public void upgradeDamage() {
        if(damagePoints + 1 == 7){
            damagePoints = 0;
            damage += 10;
        }
        else{
            damagePoints++;
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

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeedPoints() {
        return speedPoints;
    }

    public int getDamagePoints() {
        return damagePoints;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public int getMaxHealth(){
        return maxHealth;
    }

    public int getBaseSpeed(){
        return baseSpeed;
    }

    public int getLocationsCrossed() {
        return locationsCrossed;
    }

    public void setLocationsCrossed(int locationsCrossed) {
        this.locationsCrossed = locationsCrossed;
    }


    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (shipImage != null) {
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.rotate(angle, width / 2.0, height / 2.0);

            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(shipImage, transform, null);
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }


    public boolean hasShield() {
        return hasShield;
    }

    public void setHasShield(boolean hasShield) {
        this.hasShield = hasShield;
    }

    public int getShieldHealth() {
        return shieldHealth;
    }

    public void setShieldHealth(int shieldHealth) {
        this.shieldHealth = shieldHealth;
    }

    public int getDamage() {
        return damage;
    }

}