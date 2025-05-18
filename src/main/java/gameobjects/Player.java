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
    private int shieldHealth = 50;

    private double angle = 0;
    private double rotationSpeed = 0.15;

    private int healthUpgrade = 20;
    private int damageUpgrade = 10;
    private int speedUpgrade = 1;

    private BufferedImage gunImage;
    private double gunAngle = 0;
    private Point mousePosition = new Point(0, 0);

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
        try{
            InputStream gunStream = getClass().getResourceAsStream("/gun2.png");
            BufferedImage originalGun = ImageIO.read(gunStream);
            gunStream.close();

            int newWidth = originalGun.getWidth() / 6;
            int newHeight = originalGun.getHeight() / 6;
            gunImage = new BufferedImage(newWidth, newHeight, originalGun.getType());

            Graphics2D g = gunImage.createGraphics();
            g.drawImage(originalGun, 0, 0, newWidth, newHeight, null);
            g.dispose();
        } catch (IOException e) {
            System.err.println("Не удалось загрузить изображение пушки: " + e.getMessage());
            gunImage = null;
        }
    }

    public void update(int panelWidth, int panelHeight, double scaleX, double scaleY) {
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

        // Преобразуем реальные размеры экрана в масштабированные единицы
        int scaledWidth = (int) (panelWidth / scaleX);
        int scaledHeight = (int) (panelHeight / scaleY);

        // Проверка границ с учетом размеров корабля
        if (x < -width) {
            x = scaledWidth; // Появляется справа
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }
        if (x > scaledWidth) {
            x = -width; // Появляется слева
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }
        if (y < -height) {
            y = scaledHeight; // Появляется снизу
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }
        if (y > scaledHeight) {
            y = -height; // Появляется сверху
            if (gamePanel.getWorld().getEnemies().isEmpty()) {
                locationsCrossed++;
                gamePanel.generateObjectsAroundPlayer();
                hasShield = false;
            }
        }

        if (currentSpeed != speed) {
            gamePanel.repaint();
        }
    }

    public void upgradeHealth() {
        if(healthPoints + 1 == 3){
            healthPoints = 0;
            health += healthUpgrade;
            maxHealth += healthUpgrade;
        }
        else{
            healthPoints++;
        }
    }

    public void upgradeSpeed() {
        if(speedPoints + 1 == 5){
            speedPoints = 0;
            speed += speedUpgrade;
            baseSpeed += speedUpgrade;
        }
        else{
            speedPoints++;
        }
    }

    public void upgradeDamage() {
        if(damagePoints + 1 == 7){
            damagePoints = 0;
            damage += damageUpgrade;
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
        Graphics2D g2d = (Graphics2D) g;
        if (shipImage != null) {
            AffineTransform transform = new AffineTransform();
            transform.translate(x, y);
            transform.rotate(angle, width / 2.0, height / 2.0);

            g2d.drawImage(shipImage, transform, null);
        } else {
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }

        if (gunImage != null) {
            AffineTransform gunTransform = new AffineTransform();

            double gunPivotX = width / 2.0;
            double gunPivotY = height / 2.0;

            gunTransform.translate(x + gunPivotX, y + gunPivotY);
            gunTransform.rotate(gunAngle);

            gunTransform.translate((double) -gunImage.getWidth() /2, (double) -gunImage.getHeight() /2);

            g2d.drawImage(gunImage, gunTransform, null);
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

    public void updateGunDirection(Point mousePos) {
        this.mousePosition = mousePos;
        double dx = mousePos.x - (x + width/2);
        double dy = mousePos.y - (y + height/2);
        gunAngle = Math.atan2(dy, dx);
    }

}