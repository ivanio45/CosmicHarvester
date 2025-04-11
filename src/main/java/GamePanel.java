import java.awt.*;
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

import java.awt.Font; // Import Font

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {

    private Player player;
    private World world;
    private List<Bullet> bullets;
    private List<HomingMissile> rockets = new ArrayList<>();
    private int rocketCost = 150; // Стоимость ракеты
    private int rocketsCount = 0;
    boolean isRocketKeyPressed = false;
    private Timer gameTimer;
    private boolean running;
    private final int PANEL_WIDTH = 800;
    private final int PANEL_HEIGHT = 600;
    private int score = 0;
    private boolean gameOver = false;
    private Random random = new Random();
    private boolean paused = false;
    private Difficulty difficulty;
    private int worldX = 0;
    private int worldY = 0;
    private Shop shop;
    private ShopMarker shopMarker;
    private boolean inShop = false;
    private boolean bossSpawnedThisSeries = false;
    private boolean bossDefeated = false;
    private boolean shopSpawned = false;


    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        this.difficulty = Difficulty.EASY;
        initGame();
    }

    private void initGame() {
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2, 32, 32, this);
        world = new World(difficulty);
        bullets = new ArrayList<>();
        shop = new Shop();
        world.generateInitialWorldObjects(player.getX(), player.getY(), PANEL_WIDTH, PANEL_HEIGHT);
        gameTimer = new Timer(16, this);
        running = true;
        gameTimer.start();
        bossSpawnedThisSeries = false; // Initialize the flag
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            String gameOverText = "Game Over! Score: " + score;
            String locationsText = "Locations: " + player.getLocationsCrossed();
            String restartText = "Press R to Restart";

            int gameOverTextWidth = g.getFontMetrics().stringWidth(gameOverText);
            int locationsTextWidth = g.getFontMetrics().stringWidth(locationsText);
            int restartTextWidth = g.getFontMetrics().stringWidth(restartText);

            g.drawString(gameOverText, PANEL_WIDTH / 2 - gameOverTextWidth / 2, PANEL_HEIGHT / 2 - 40);
            g.drawString(locationsText, PANEL_WIDTH / 2 - locationsTextWidth / 2, PANEL_HEIGHT / 2); // Draw locations
            g.drawString(restartText, PANEL_WIDTH / 2 - restartTextWidth / 2, PANEL_HEIGHT / 2 + 40);

            return;
        }

        if (inShop) {
            shop.draw(g);

            // Draw player stats in shop
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.PLAIN, 16));
            g.drawString("Health: " + player.getHealth(), 10, 20);
            g.drawString("Damage: " + player.getDamage(), 10, 40);
            g.drawString("Score: " + getScore(), 10, 60);
            g.drawString("Level: " + player.getLevel(), 10, 80);
            g.drawString("Speed: " + player.getSpeed(), 10, 100);
            g.drawString("1 - Upgrade damage (+10): 50 score", 150, 20);
            g.drawString("2 - Upgrade health (+20): 30 score", 150, 40);
            g.drawString("3 - Upgrade speed (+2, Level +1): 100 score", 150, 60);
            g.drawString("4 - Buy Rocket (+1) : 150 score", 150, 80);
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

        // Draw the boss
        Boss boss = world.getBoss();
        if (boss != null) {
            boss.draw(g, worldX, worldY);
        }

        if (shopMarker != null) {
            shopMarker.draw(g, worldX, worldY);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Health: " + player.getHealth(), 10, 40);
        g.drawString("Location: " + player.getLocationsCrossed(), 10, 60);

        if (world.getBoss() != null) {
            g.drawString("Boss Health: " + world.getBoss().getHealth(), 10, 80);
        }

        if (paused) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String pauseText = "PAUSED";
            int textWidth = g.getFontMetrics().stringWidth(pauseText);
            g.drawString(pauseText, PANEL_WIDTH / 2 - textWidth / 2, PANEL_HEIGHT / 2);
        }
    }

    public int getScore() {
        return score;
    }

    public void update() {
        if (!gameOver && !paused && !inShop) {
            player.update(PANEL_WIDTH, PANEL_HEIGHT);
            world.updateWorldObjects(worldX, worldY, PANEL_WIDTH, PANEL_HEIGHT);
            updateBullets();
            updateEnemies();
            updateBoss();
            for (int i = 0; i < rockets.size(); i++) {
                HomingMissile rocket = rockets.get(i);
                rocket.update(); // Обновляем положение ракеты
            }
            checkCollisions();
            checkBossCollisions();



            if (player.getHealth() <= 0) {
                gameOver = true;
                running = false;
                gameTimer.stop();
            }

            // Spawn Boss Logic
            if (player.getLocationsCrossed() > 0 && player.getLocationsCrossed() % 4 == 0 && world.getBoss() == null && !bossSpawnedThisSeries) {
                int spawnX = player.getX() + worldX + 200; // Смещаем точку спавна по X
                int spawnY = player.getY() + worldY + 200; // Смещаем точку спавна по Y
                world.spawnBoss(spawnX, spawnY, PANEL_WIDTH, PANEL_HEIGHT, player.getLocationsCrossed());
                bossSpawnedThisSeries = true;
                bossDefeated = false; // Reset bossDefeated when boss spawns
            }

            // RESET BOSS SPAWNED FLAG AFTER BOSS IS DEFEATED AND NEW LOCATION CROSSED
            if (bossDefeated && player.getLocationsCrossed() % 4 != 0) {
                bossSpawnedThisSeries = false;
                bossDefeated = false; // Reset the defeated flag
            }


            if (player.getLocationsCrossed() % 3 == 0 && player.getLocationsCrossed() != 0 && !inShop) {
                if (shopMarker == null) {
                    shopMarker = new ShopMarker(random.nextInt(700), random.nextInt(500), 50, 50);
                    shopSpawned = true;
                }

                Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
                Rectangle shopMarkerRect = new Rectangle(shopMarker.getX() - worldX, shopMarker.getY() - worldY, shopMarker.getWidth(), shopMarker.getHeight());

                if (playerRect.intersects(shopMarkerRect)) {
                    inShop = true;
                    shopSpawned = false;
                    gameTimer.stop();
                }
            } else {
                shopMarker = null;
                shopSpawned = false;
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

    private void checkCollisions() {
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        for (int i = 0; i < world.getAsteroids().size(); i++) {
            Asteroid asteroid = world.getAsteroids().get(i);
            Rectangle asteroidRect = new Rectangle(asteroid.getX() - worldX, asteroid.getY() - worldY, asteroid.getWidth(), asteroid.getHeight());

            if (playerRect.intersects(asteroidRect)) {
                if (asteroid.getType() == Asteroid.AsteroidType.RARE) {
                    score += 20;
                } else if (asteroid.getType() == Asteroid.AsteroidType.NORMAL) {
                    score += 10;
                } else {
                    score -= 5;
                }
                world.getAsteroids().remove(i);
                i--;
            }
        }

        // Collision with Enemies
        for (int i = 0; i < world.getEnemies().size(); i++) {
            Enemy enemy = world.getEnemies().get(i);
            Rectangle enemyRect = new Rectangle(enemy.getX() - worldX, enemy.getY() - worldY, enemy.getWidth(), enemy.getHeight());

            if (playerRect.intersects(enemyRect)) {
                int damage = enemy.getWidth() / 5; // Урон зависит от размера врага
                player.setHealth(player.getHealth() - damage);
            }

            for (int j = 0; j < bullets.size(); j++) {
                Bullet bullet = bullets.get(j);
                Rectangle bulletRect = new Rectangle(bullet.getX() - worldX, bullet.getY() - worldY, bullet.getWidth(), bullet.getHeight());

                if (bulletRect.intersects(enemyRect)) {
                    enemy.takeDamage(bullet.getDamage());
                    bullets.remove(j);
                    j--;

                    if (!enemy.isAlive()) {
                        world.getEnemies().remove(i);
                        i--;
                        score += 50; // Начисляем очки
                    }
                }
            }
        }
        for (int i = 0; i < rockets.size(); i++) {
            HomingMissile rocket = rockets.get(i);
            Rectangle rocketRect = new Rectangle(rocket.getX() - worldX, rocket.getY() - worldY, rocket.getWidth(), rocket.getHeight());

            for (int j = 0; j < world.getEnemies().size(); j++) {
                Enemy enemy = world.getEnemies().get(j);
                Rectangle enemyRect = new Rectangle(enemy.getX() - worldX, enemy.getY() - worldY, enemy.getWidth(), enemy.getHeight());

                if (rocketRect.intersects(enemyRect)) {
                    // Ракета столкнулась с врагом!
                    enemy.takeDamage(rocket.getDamage());

                    if (!enemy.isAlive()) {
                        world.getEnemies().remove(j);
                        j--;
                        score += 50;
                    }
                    rockets.remove(i); // Удаляем ракету из списка
                    i--;
                    break;
                }
            }
        }
        Boss boss = world.getBoss();
        if (boss != null) {
            for (int i = 0; i < rockets.size(); i++) {
                HomingMissile rocket = rockets.get(i);
                Rectangle rocketRect = new Rectangle(rocket.getX() - worldX, rocket.getY() - worldY, rocket.getWidth(), rocket.getHeight());
                Rectangle bossRect = new Rectangle(boss.getX(), boss.getY(), boss.getWidth(), boss.getHeight());
                if (rocketRect.intersects(bossRect)) {
                    // Ракета столкнулась с боссом!
                    boss.takeDamage(rocket.getDamage());
                    rockets.remove(i);
                    i--;
                    break;
                }
            }
        }
    }

    private void checkBossCollisions() {
        Boss boss = world.getBoss();
        if (boss != null) {
            Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getWidth(), player.getHeight());
            Rectangle bossRect = new Rectangle(boss.getX() - worldX, boss.getY() - worldY, boss.getWidth(), boss.getHeight());

            if (playerRect.intersects(bossRect)) {
                player.setHealth(player.getHealth() - boss.getDamage());
                if (player.getHealth() <= 0) {
                    gameOver = true;
                    running = false;
                    gameTimer.stop();
                }
            }

            for (int i = 0; i < bullets.size(); i++) {
                Bullet bullet = bullets.get(i);
                if (!bullet.isActive()) {
                    bullets.remove(i);
                    i--;
                    continue;
                }
                Rectangle bulletRect = new Rectangle(bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
                if (bulletRect.intersects(new Rectangle(boss.getX() - worldX, boss.getY() - worldY, boss.getWidth(), boss.getHeight()))) {
                    boss.takeDamage(player.getDamage());  // or bullet damage
                    bullet.setActive(false);

                    if (!boss.isAlive()) {
                        world.setBoss(null);
                        score += 100;
                        bossDefeated = true;  // SET THE FLAG WHEN BOSS IS DEFEATED
                    }
                }
            }
        }
    }

    public void startGame() {
        gameTimer.start();
        inShop = false;
        shopMarker = null;
    }

    public void generateObjectsAroundPlayer() {
        world.generateObjectsAroundPlayer(player.getX(), player.getY(), PANEL_WIDTH, PANEL_HEIGHT);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (inShop) {
            if (key == KeyEvent.VK_1) {
                if (score >= 50) {
                    score -= 50;
                    player.setDamage(player.getDamage() + 10);
                }
            } else if (key == KeyEvent.VK_2) {
                if (score >= 30) {
                    score -= 30;
                    player.setHealth(player.getHealth() + 20);
                }
            } else if (key == KeyEvent.VK_3) { // NEW: buy speed
                if (score >= 100) {
                    score -= 100;
                    player.setBaseSpeed(player.getBaseSpeed() + 2); // increase the ship speed by 2
                    player.setSpeed(player.getBaseSpeed());
                    player.setLevel(player.getLevel() + 1);
                }
            } else if (key == KeyEvent.VK_ESCAPE) {
                startGame();
            }

            repaint();
        }
        if(key == KeyEvent.VK_4) {
            if(score >= rocketCost) {
                score -= rocketCost;
                rocketsCount++;
            }
        }
        if (key == KeyEvent.VK_T && rocketsCount > 0 && !isRocketKeyPressed) {
            // Находим ближайшего врага
            GameObject closestEnemy = findClosestEnemy(player.getX(), player.getY());

            if (closestEnemy != null) {
                HomingMissile missile = new HomingMissile(player.getX(), player.getY(), 16, 16, closestEnemy);
                rockets.add(missile);
                rocketsCount--;
            }
            isRocketKeyPressed = true; // Устанавливаем флаг, что клавиша нажата
        }
        else if (gameOver) {
            if(key == KeyEvent.VK_R) {
                restartGame();
            }
        }
        else if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
            paused = !paused;
            if (paused) {
                gameTimer.stop();
            } else {
                gameTimer.start();
            }
            repaint();
        }
        else {
            player.keyPressed(e);
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

    private void restartGame() {
        gameOver = false;
        running = true;
        score = 0;
        player.setLocationsCrossed(0);
        player.setHealth(100);
        world = new World(difficulty);
        bullets = new ArrayList<>();
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2, 32, 32, this);
        world.generateInitialWorldObjects(player.getX(), player.getY(), PANEL_WIDTH, PANEL_HEIGHT);
        gameTimer.start();
        bossSpawnedThisSeries = false; // Reset the flag on restart
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        restartGame();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_T) {
            isRocketKeyPressed = false; // Сбрасываем флаг, когда клавиша отпущена
        }
        player.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            update();
        }
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver && !inShop && !paused) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            bullets.add(new Bullet(
                    player.getX() + player.getWidth() / 2,
                    player.getY() + player.getHeight() / 2,
                    5, 5,
                    mouseX, mouseY
            ));
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub

    }
}