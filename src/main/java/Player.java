import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

class Player extends GameObject {
    private int speed = 5;
    private boolean up, down, left, right;
    private int health = 100;
    private GamePanel gamePanel;
    private int locationsCrossed = 0;
    private int damage = 20;
    private BufferedImage shipImage;

    public Player(int x, int y, int width, int height, GamePanel gamePanel) {
        super(x, y, width, height);
        this.gamePanel = gamePanel;
        this.color = Color.GREEN;

        try {
            shipImage = ImageIO.read(new File("C:\\Users\\antip\\IdeaProjects\\CosmicHarvester\\CosmicHarvester\\src\\main\\resources\\SpaceShip.png"));  // Замените на актуальный путь!
        } catch (IOException e) {
            System.err.println("Не удалось загрузить изображение корабля: " + e.getMessage());
            shipImage = null; // Важно!
        }
    }

    public void update(int panelWidth, int panelHeight) {

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
            gamePanel.generateObjectsAroundPlayer();
        }
        if (y > panelHeight) {
            y = 0;
            locationsCrossed++;
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

    public int getLocationsCrossed() {
        return locationsCrossed;
    }

    public void setLocationsCrossed(int locationsCrossed) {
        this.locationsCrossed = locationsCrossed;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public void draw(Graphics g, int worldX, int worldY) {
        if (shipImage != null) {
            g.drawImage(shipImage, x - worldX, y - worldY, width, height, null);
        } else {
            g.setColor(color);
            g.fillRect(x - worldX, y - worldY, width, height);
        }
    }
}