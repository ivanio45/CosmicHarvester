package panels;

import gameobjects.*;
import gamestart.CosmicHarvester;
import utils.Difficulty;
import utils.SoundManager;
import utils.World;

import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.*;
import javax.swing.OverlayLayout;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

import java.awt.Font;

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {

    private Player player;
    private World world;
    private List<Bullet> bullets;
    private List<HomingMissile> rockets = new ArrayList<>();
    private int rocketsCount = 0;
    private Timer gameTimer;
    private boolean running;
    private final int PANEL_WIDTH = 1200;
    private final int PANEL_HEIGHT = 800;
    private int score = 0;
    private boolean gameOver = false;
    private final Random random = new Random();
    private boolean paused = false;
    private PausePanel pausePanel;
    private Difficulty difficulty;
    private int worldX = 0;
    private int worldY = 0;
    private Shop shop;
    private ShopMarker shopMarker;
    private boolean inShop = false;
    private boolean shopSpawned = false;
    private boolean bossSpawned = false;
    private BufferedImage shieldImage;
    private BufferedImage[] backgrounds;
    private int currentBackgroundIndex = 0;
    SoundManager soundManager = SoundManager.getInstance();


    // --- Конструктор ---
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        this.difficulty = Difficulty.EASY;

        setLayout(new OverlayLayout(this));
        initGame();

        pausePanel = new PausePanel(this);
        add(pausePanel);
        pausePanel.setVisible(false);
    }

    // --- Методы инициализации ---
    private void initGame() {
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2, 85, 85, this);
        world = new World(difficulty);
        bullets = new ArrayList<>();
        shop = new Shop(player);
        world.generateObjects(PANEL_WIDTH, PANEL_HEIGHT);
        gameTimer = new Timer(30, this);
        running = false;
        gameTimer.stop();
        paused = true;
        backgrounds = new BufferedImage[7];
        for (int i = 0; i < 7; i++) {
            try {
                InputStream imageStream = getClass().getResourceAsStream("/background" + (i + 1) + ".png");
                backgrounds[i] = ImageIO.read(imageStream);
                imageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            InputStream imageStream = getClass().getResourceAsStream("/shield.png");
            shieldImage = ImageIO.read(imageStream);
            imageStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // --- Методы обновления ---
    private void updateEnemies() {
        for (Enemy enemy : world.getEnemies()) {
            enemy.update(player.getX(), player.getY());
        }
    }

    private void updateBoss() {
        Boss boss = world.getBoss();
        if (boss != null) {
            boss.update(player.getX() + worldX, player.getY() + worldY);
        }
    }

    public void update() {
        if (!gameOver && !paused && !inShop) {
            player.update(PANEL_WIDTH, PANEL_HEIGHT);
            updateBullets();
            updateEnemies();
            updateBoss();
            for (HomingMissile rocket : rockets) {
                rocket.update();
            }
            checkCollisions();

            if (player.getHealth() <= 0) {
                soundManager.playSound("game_over");
                gameOver = true;
                running = false;
                gameTimer.stop();
            }

            if (player.getLocationsCrossed() > 1 && player.getLocationsCrossed() % 3 == 1) {
                if (!bossSpawned && world.getBoss() == null) {
                    world.spawnBoss(PANEL_WIDTH / 2, PANEL_HEIGHT + PANEL_HEIGHT / 2, player.getLocationsCrossed());
                    bossSpawned = true;
                }
            } else {
                bossSpawned = false;
            }


            if (player.getLocationsCrossed() % 3 == 0 && player.getLocationsCrossed() != 0) {
                if (shopMarker == null && !shopSpawned) {
                    shopMarker = new ShopMarker(random.nextInt(700), random.nextInt(500), 180, 150);
                    shopSpawned = true;
                }
                if (shopMarker != null) {
                    Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
                    Rectangle shopMarkerRect = new Rectangle(shopMarker.getX() - worldX, shopMarker.getY() - worldY,
                            shopMarker.getWidth(), shopMarker.getHeight());

                    if (playerRect.intersects(shopMarkerRect)) {
                        inShop = true;
                    }
                }
            } else {
                if (shopSpawned) {
                    shopMarker = null;
                    shopSpawned = false;
                }
            }
        }
    }

    public void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (!bullet.isActive()) {
                bullets.remove(i);
                i--;
            }
        }
    }

    public void generateObjectsAroundPlayer() {
        world.generateObjects(PANEL_WIDTH, PANEL_HEIGHT);
        for (int i = 0; i < world.getEnemies().size(); i++) {
            world.getEnemies().get(i).health += player.getLocationsCrossed() * 15;
            world.getEnemies().get(i).maxHealth += player.getLocationsCrossed() * 15;

        }
        changeBackground();
    }

    private void changeBackground() {
        currentBackgroundIndex = (player.getLocationsCrossed()) % 7;
    }


    // --- Методы проверки столкновений ---
    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(player.getX() + player.getWidth() / 4, player.getY() + player.getHeight() / 4, player.getWidth() / 2, player.getHeight() / 2);

        for (int i = 0; i < world.getAsteroids().size(); i++) {
            Asteroid asteroid = world.getAsteroids().get(i);
            Rectangle asteroidRect = new Rectangle(asteroid.getX() + asteroid.getWidth() / 4, asteroid.getY() + asteroid.getHeight() / 4, asteroid.getWidth() / 2, asteroid.getHeight() / 2);

            if (playerRect.intersects(asteroidRect)) {
                soundManager.playSound("pickup");

                if (asteroid.getType() == Asteroid.AsteroidType.RARE) {
                    score += 20;
                } else if (asteroid.getType() == Asteroid.AsteroidType.NORMAL) {
                    score += 10;
                } else if (asteroid.getType() == Asteroid.AsteroidType.DANGEROUS) {
                    if (player.hasShield()) {
                        player.setShieldHealth(player.getShieldHealth() - 10);
                        if (player.getShieldHealth() <= 0) {
                            player.setHasShield(false);
                        }
                    } else {
                        score -= 10;
                        player.setHealth(player.getHealth() - 10);
                    }
                } else if (asteroid.getType() == Asteroid.AsteroidType.HEALTH_PACK) {
                    if (player.getHealth() < player.getMaxHealth() / 2) {
                        player.setSpeed(player.getBaseSpeed());
                    }
                    player.setHealth(player.getMaxHealth());
                } else if (asteroid.getType() == Asteroid.AsteroidType.SHIELD) {
                    player.setHasShield(true);
                    player.setShieldHealth(30);
                } else if (asteroid.getType() == Asteroid.AsteroidType.BLACK_HOLE) {
                    player.setHealth(-1);
                }
                world.getAsteroids().remove(i);
                i--;
            }
        }
        for (int i = 0; i < world.getEnemies().size(); i++) {
            Enemy enemy = world.getEnemies().get(i);
            Rectangle enemyRect = new Rectangle(enemy.getX() - worldX, enemy.getY() - worldY, enemy.getWidth(), enemy.getHeight());

            if (playerRect.intersects(enemyRect)) {
                int damage = enemy.getWidth() / 5;
                if (player.hasShield()) {
                    player.setShieldHealth(player.getShieldHealth() - damage);
                    if (player.getShieldHealth() <= 0) {
                        player.setHasShield(false);
                    }
                } else {
                    player.setHealth(player.getHealth() - damage);
                }
            }

            for (int j = 0; j < bullets.size(); j++) {
                Bullet bullet = bullets.get(j);
                Rectangle bulletRect = new Rectangle(bullet.getX() + bullet.getWidth() / 3, bullet.getY() + bullet.getHeight() / 3, bullet.getWidth() / 4, bullet.getHeight() / 4);

                if (bulletRect.intersects(enemyRect)) {
                    enemy.takeDamage(player.getDamage());
                    bullets.remove(j);
                    j--;
                    if (enemy.isAlive()) {
                        soundManager.playSound("explosion");

                        if (enemy instanceof Boss) {
                            score += enemy.maxHealth / 8;
                            world.setBoss(null);
                        } else {
                            score += 50;
                        }
                        world.getEnemies().remove(i);
                        i--;
                    }
                }
            }
        }
        for (int i = 0; i < rockets.size(); i++) {
            HomingMissile rocket = rockets.get(i);
            if (rocket.getTarget() == null) {
                rockets.remove(i);
                i--;
            }

            Rectangle rocketRect = new Rectangle(rocket.getX() - worldX, rocket.getY() - worldY, rocket.getWidth(), rocket.getHeight());

            for (int j = 0; j < world.getEnemies().size(); j++) {
                Enemy enemy = world.getEnemies().get(j);
                Rectangle enemyRect = new Rectangle(enemy.getX() - worldX, enemy.getY() - worldY, enemy.getWidth(), enemy.getHeight());

                if (rocketRect.intersects(enemyRect)) {
                    enemy.takeDamage(rocket.getDamage());

                    if (enemy.isAlive()) {
                        world.getEnemies().remove(j);
                        score += 50;
                    }
                    rockets.remove(i);
                    i--;
                    break;
                }
            }
        }
    }


    // --- Методы отрисовки ---
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgrounds[currentBackgroundIndex], 0, 0, getWidth(), getHeight(), this);


        draw(g);
    }

    public void draw(Graphics g) {
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String gameOverText = "Game Over! Score: " + score;
            String locationsText = "Locations: " + player.getLocationsCrossed();
            String restartText = "Press R to Restart";
            String exitToMenuText = "Press M to Exit to Menu";

            int gameOverTextWidth = g.getFontMetrics().stringWidth(gameOverText);
            int locationsTextWidth = g.getFontMetrics().stringWidth(locationsText);
            int restartTextWidth = g.getFontMetrics().stringWidth(restartText);
            int exitToMenuTextWidth = g.getFontMetrics().stringWidth(exitToMenuText);

            g.drawString(gameOverText, PANEL_WIDTH / 2 - gameOverTextWidth / 2, PANEL_HEIGHT / 2 - 100);
            g.drawString(locationsText, PANEL_WIDTH / 2 - locationsTextWidth / 2, PANEL_HEIGHT / 2 - 60);
            g.drawString(restartText, PANEL_WIDTH / 2 - restartTextWidth / 2, PANEL_HEIGHT / 2 - 20);
            g.drawString(exitToMenuText, PANEL_WIDTH / 2 - exitToMenuTextWidth / 2, PANEL_HEIGHT / 2 + 20);

            return;
        }

        player.draw(g, 0, 0);

        for (Asteroid asteroid : world.getAsteroids()) {
            asteroid.draw(g, worldX, worldY);
        }
        for (Enemy enemy : world.getEnemies()) {
            enemy.draw(g, worldX, worldY);
        }
        for (Bullet bullet : bullets) {
            bullet.draw(g, worldX, worldY);
        }
        for (HomingMissile rocket : rockets) {
            rocket.draw(g, worldX, worldY);
        }


        if (shopMarker != null) {
            shopMarker.draw(g, worldX, worldY);
        }

        if (paused) {
            g.setColor(new Color(255, 255, 255, 150)); // полупрозрачный белый цвет
            g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Health: " + player.getHealth() + '/' + player.getMaxHealth(), 10, 60);
        g.drawString("Speed: " + player.getSpeed() + '/' + player.getBaseSpeed(), 10, 80);
        g.drawString("Damage: " + player.getDamage(), 10, 100);
        g.drawString("Rockets: " + rocketsCount, 10, 120);

        if (player.hasShield()) {
            g.drawImage(shieldImage, player.getX() - 15, player.getY() - 15, 110, 110, this);
            g.drawString("Shield: " + player.getShieldHealth(), 10, 160);
        }


        g.drawString("Location: " + player.getLocationsCrossed(), 10, 140);
        if (inShop) {
            shop.draw(g);
        }

        if (player.hasShield()) {
            g.drawImage(shieldImage, player.getX() - 15, player.getY() - 15, 110, 110, this);
            g.drawString("Shield: " + player.getShieldHealth(), 10, 160);
        }

        Boss boss = world.getBoss();
        if (boss != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("BOSS HP: ", PANEL_WIDTH / 4, 40);
            g.setColor(Color.RED);
            g.fillRect(PANEL_WIDTH / 4 + 120, 20, 500, 20);
            int currentHealthWidth = 500 * boss.health / boss.maxHealth;
            g.setColor(Color.GREEN);
            g.fillRect(PANEL_WIDTH / 4 + 120, 20, currentHealthWidth, 20);
        }
    }

    // --- Обработчики событий ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (inShop) {
            soundManager.playSound("buy");
            if (key == KeyEvent.VK_1) {
                if (score >= 50) {
                    score -= 50;
                    player.upgradeDamage();
                    repaint();
                }
            } else if (key == KeyEvent.VK_2) {
                if (score >= 30) {
                    score -= 30;
                    player.upgradeHealth();
                    repaint();
                }
            } else if (key == KeyEvent.VK_3) {
                if (score >= 100) {
                    score -= 100;
                    player.upgradeSpeed();
                    repaint();
                }
            } else if (key == KeyEvent.VK_4) {
                if (score >= 150) {
                    score -= 150;
                    rocketsCount++;
                }
            } else if (key == KeyEvent.VK_ESCAPE) {
                startGame();
            }
            repaint();
            return;
        }
        if (key == KeyEvent.VK_T && rocketsCount > 0) {
            GameObject closestEnemy = findClosestEnemy(player.getX(), player.getY());
            if (closestEnemy != null) {
                HomingMissile missile = new HomingMissile(player.getX(), player.getY(), closestEnemy);
                rockets.add(missile);
                rocketsCount--;
                soundManager.playSound("shoot");
            }
        } else if (gameOver) {
            if (key == KeyEvent.VK_R) {
                restartGame();
            } else if (key == KeyEvent.VK_M) {
                exitToMenu();
            }
        } else if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
            paused = !paused;
            if (paused) {
                gameTimer.stop();
                pausePanel.setVisible(true);
                pausePanel.showMainMenu();
            } else {
                resumeGame();
            }
            repaint();
        } else {
            player.keyPressed(e);
        }
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        restartGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            if (!inShop) {
                update();
            }
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver && !inShop && !paused) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            bullets.add(new Bullet(
                    player.getX() - player.getWidth() / 2 + 25,
                    player.getY() - player.getHeight() / 2 + 35,
                    mouseX - 35, mouseY - 35
            ));
            soundManager.playSound("shoot");
        }
    }

    private GameObject findClosestEnemy(int x, int y) {
        double closestDistance = Double.MAX_VALUE;
        GameObject closestEnemy = null;

        for (Enemy enemy : world.getEnemies()) {
            double distance = Math.sqrt(Math.pow(enemy.getX() - x, 2) + Math.pow(enemy.getY() - y, 2));
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEnemy = enemy;
            }
        }
        return closestEnemy;
    }


    // --- Методы управления состоянием игры ---

    public void startGame() {
        gameTimer.start();
        inShop = false;
        shopMarker = null;
    }

    private void restartGame() {
        gameOver = false;
        running = true;
        score = 0;
        player.setLocationsCrossed(0);
        player.setHealth(100);
        world = new World(difficulty);
        bullets = new ArrayList<>();
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2, 85, 85, this);
        shop = new Shop(player);
        world.generateObjects(PANEL_WIDTH, PANEL_HEIGHT);
        gameTimer.start();
    }

    public void resumeGame() {
        paused = false;
        gameTimer.start();
        pausePanel.setVisible(false);
        requestFocusInWindow();
    }

    public void exitToMenu() {
        gameTimer.stop();
        paused = false;
        pausePanel.setVisible(false);
        CosmicHarvester frame = (CosmicHarvester) SwingUtilities.getWindowAncestor(this);
        CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
        cardLayout.show(frame.getContentPane(), "menu");
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
