package panels;

import gameobjects.*;
import gamestart.CosmicHarvester;
import utils.Difficulty;
import utils.SoundManager;
import utils.World;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
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
    private int panelWidth = 1200;
    private int panelHeight = 800;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
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
        setPreferredSize(new Dimension(panelWidth, panelHeight));
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

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!gameOver && !paused && !inShop) {
                    int mouseX = (int)(e.getX() / scaleX);
                    int mouseY = (int)(e.getY() / scaleY);
                    player.updateGunDirection(new Point(mouseX, mouseY));
                    repaint();
                }
            }
        });
    }

    // --- Методы инициализации ---
    private void initGame() {
        player = new Player(panelWidth / 2, panelHeight / 2, 85, 85, this);
        world = new World(difficulty);
        bullets = new ArrayList<>();
        shop = new Shop(player,getWidth(),getHeight());
        world.generateObjects(panelWidth, panelHeight);
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
    public void updatePanelSize(int width, int height) {
        this.panelWidth = width;
        this.panelHeight = height;
        setPreferredSize(new Dimension(width, height));
        scaleX = (double) width / 1200;
        scaleY = (double) height / 800;
        if (player != null) {
            int newWidth = (int)(85 * scaleX);
            int newHeight = (int)(85 * scaleY);
            int newX = (int)(player.getX() * scaleX / (player.getWidth() / 85.0));
            int newY = (int)(player.getY() * scaleY / (player.getHeight() / 85.0));
            player = new Player(newX, newY, newWidth, newHeight, this);
        }
        if (shop != null) {
            shop = new Shop(player, (int)(panelWidth / scaleX), (int)(panelHeight / scaleY));
        }
        revalidate();
        repaint();
    }



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
            player.update(panelWidth, panelHeight, scaleX, scaleY);
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
                    world.spawnBoss(panelWidth / 2, panelHeight + panelHeight / 2, player.getLocationsCrossed());
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
        world.generateObjects(panelWidth, panelHeight);
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
                        player.setShieldHealth(player.getShieldHealth() - 20);
                        if (player.getShieldHealth() <= 0) {
                            player.setHasShield(false);
                        }
                    } else {
                        score -= 20;
                        player.setHealth(player.getHealth() - 20);
                    }
                } else if (asteroid.getType() == Asteroid.AsteroidType.HEALTH_PACK) {
                    if (player.getHealth() < player.getMaxHealth() / 2) {
                        player.setSpeed(player.getBaseSpeed());
                    }
                    player.setHealth(player.getMaxHealth());
                } else if (asteroid.getType() == Asteroid.AsteroidType.SHIELD) {
                    player.setHasShield(true);
                    player.setShieldHealth(50);
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scaleX, scaleY);
        g2d.drawImage(backgrounds[currentBackgroundIndex], 0, 0, (int)(panelWidth / scaleX), (int)(panelHeight / scaleY), this);
        draw(g2d);
        g2d.scale(1 / scaleX, 1 / scaleY); // Сброс масштаба
    }

    public void draw(Graphics g) {
        if (gameOver) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, (int)(30 * scaleX)));
            String gameOverText = "Game Over! Score: " + score;
            String locationsText = "Locations: " + player.getLocationsCrossed();
            String restartText = "Press R to Restart";
            String exitToMenuText = "Press M to Exit to Menu";

            int gameOverTextWidth = g.getFontMetrics().stringWidth(gameOverText);
            int locationsTextWidth = g.getFontMetrics().stringWidth(locationsText);
            int restartTextWidth = g.getFontMetrics().stringWidth(restartText);
            int exitToMenuTextWidth = g.getFontMetrics().stringWidth(exitToMenuText);

            g.drawString(gameOverText, (int)((panelWidth / scaleX) / 2 - gameOverTextWidth / 2), (int)((panelHeight / scaleY) / 2 - 100));
            g.drawString(locationsText, (int)((panelWidth / scaleX) / 2 - locationsTextWidth / 2), (int)((panelHeight / scaleY) / 2 - 60));
            g.drawString(restartText, (int)((panelWidth / scaleX) / 2 - restartTextWidth / 2), (int)((panelHeight / scaleY) / 2 - 20));
            g.drawString(exitToMenuText, (int)((panelWidth / scaleX) / 2 - exitToMenuTextWidth / 2), (int)((panelHeight / scaleY) / 2 + 20));

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
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(0, 0, (int)(panelWidth / scaleX), (int)(panelHeight / scaleY));
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, (int)(20 * scaleX)));
        g.drawString("Score: " + score, (int)(10 * scaleX), (int)(20 * scaleY));
        g.drawString("Health: " + player.getHealth() + '/' + player.getMaxHealth(), (int)(10 * scaleX), (int)(60 * scaleY));
        g.drawString("Speed: " + player.getSpeed() + '/' + player.getBaseSpeed(), (int)(10 * scaleX), (int)(80 * scaleY));
        g.drawString("Damage: " + player.getDamage(), (int)(10 * scaleX), (int)(100 * scaleY));
        g.drawString("Rockets: " + rocketsCount, (int)(10 * scaleX), (int)(120 * scaleY));

        if (player.hasShield()) {
            g.drawImage(shieldImage, (int)(player.getX() - 15 * scaleX), (int)(player.getY() - 15 * scaleY), (int)(110 * scaleX), (int)(110 * scaleY), this);
            g.drawString("Shield: " + player.getShieldHealth(), (int)(10 * scaleX), (int)(160 * scaleY));
        }

        g.drawString("Location: " + player.getLocationsCrossed(), (int)(10 * scaleX), (int)(140 * scaleY));
        if (inShop) {
            shop.draw(g);
        }

        Boss boss = world.getBoss();
        if (boss != null) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, (int)(20 * scaleX)));
            g.drawString("BOSS HP: ", (int)(panelWidth / scaleX / 4), (int)(40 * scaleY));
            g.setColor(Color.RED);
            g.fillRect((int)(panelWidth / scaleX / 4 + 120), (int)(20 * scaleY), (int)(500 * scaleX), (int)(20 * scaleY));
            int currentHealthWidth = (int)(500 * scaleX * boss.health / boss.maxHealth);
            g.setColor(Color.GREEN);
            g.fillRect((int)(panelWidth / scaleX / 4 + 120), (int)(20 * scaleY), currentHealthWidth, (int)(20 * scaleY));
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
            int mouseX = (int)(e.getX() / scaleX);
            int mouseY = (int)(e.getY() / scaleY);
            bullets.add(new Bullet(
                    player.getX() - player.getWidth() / 2 + (int)(25 * scaleX),
                    player.getY() - player.getHeight() / 2 + (int)(35 * scaleY),
                    mouseX - (int)(35 * scaleX), mouseY - (int)(35 * scaleY)
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
        player = new Player((int)(panelWidth / scaleX / 2), (int)(panelHeight / scaleY / 2), (int)(85 * scaleX), (int)(85 * scaleY), this);
        shop = new Shop(player, (int)(panelWidth / scaleX), (int)(panelHeight / scaleY));
        world.generateObjects((int)(panelWidth / scaleX), (int)(panelHeight / scaleY));
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
