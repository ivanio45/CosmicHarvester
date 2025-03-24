import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener {

    private Player player;
    private World world;
    private List<Bullet> bullets;
    private Timer gameTimer;
    private boolean running;
    private final int PANEL_WIDTH = 800;
    private final int PANEL_HEIGHT = 600;
    private int score = 0;
    private boolean gameOver = false;
    private Random random = new Random();
    private boolean paused = false;

    private int worldX = 0;
    private int worldY = 0;

    private int currentLocation = 0;

    private Shop shop;
    private ShopMarker shopMarker;
    private boolean inShop = false;
    private boolean shopSpawned = false;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        initGame();
    }

    private void initGame() {
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2, 32, 32, this);
        world = new World();
        bullets = new ArrayList<>();
        shop = new Shop(this);
        world.generateInitialWorldObjects(player.getX(), player.getY(), PANEL_WIDTH, PANEL_HEIGHT);
        gameTimer = new Timer(16, this);
        running = true;
        gameTimer.start();
    }

    private void shoot() {
        bullets.add(new Bullet(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, 5, 5));
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
            g.drawString(locationsText, PANEL_WIDTH / 2 - locationsTextWidth / 2, PANEL_HEIGHT / 2);
            g.drawString(restartText, PANEL_WIDTH / 2 - restartTextWidth / 2, PANEL_HEIGHT / 2 + 40);

            return;
        }

        if (inShop) {
            shop.draw(g);
            return;
        }

        player.draw(g, 0, 0);

        for (Asteroid asteroid : world.getAsteroids()) {
            asteroid.draw(g, worldX, worldY);
        }
        for (Enemy enemy : world.getEnemies()) {
            enemy.draw(g, worldX, worldY);
        }
        for (Resource resource : world.getResources()) {
            resource.draw(g, worldX, worldY);
        }
        for (Bullet bullet : bullets) {
            bullet.draw(g, worldX, worldY);
        }

        if (shopMarker != null) {
            shopMarker.draw(g, worldX, worldY);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Health: " + player.getHealth(), 10, 40);
        g.drawString("Location: " + player.getLocationsCrossed(), 10, 60);

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

    public void update() {
        if (!gameOver && !paused && !inShop) {
            player.update(PANEL_WIDTH, PANEL_HEIGHT);
            world.updateWorldObjects(worldX, worldY, PANEL_WIDTH, PANEL_HEIGHT);
            updateBullets();
            updateEnemies();
            updateResources();
            checkCollisions();

            if (player.getHealth() <= 0) {
                gameOver = true;
                running = false;
                gameTimer.stop();
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


    private void updateBullets() {
        for (int i = 0; i < bullets.size(); i++) {
            Bullet bullet = bullets.get(i);
            bullet.update();
            if (bullet.getX() < worldX - 100 || bullet.getX() > worldX + PANEL_WIDTH + 100 || bullet.getY() < worldY || bullet.getY() > worldY + PANEL_HEIGHT) { //Check if bullets are out of screen
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

    private void updateResources() {
        for (Resource resource : world.getResources()) {
            resource.update();
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
                } else {
                    score += 10;
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
                player.setHealth(player.getHealth() - damage);
            }

            for (int j = 0; j < bullets.size(); j++) {
                Bullet bullet = bullets.get(j);
                Rectangle bulletRect = new Rectangle(bullet.getX() - worldX, bullet.getY() - worldY, bullet.getWidth(), bullet.getHeight());
                if (bulletRect.intersects(enemyRect)) {
                    world.getEnemies().remove(i);
                    bullets.remove(j);
                    i--;
                    j--;
                    score += 50;
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
            } else if (key == KeyEvent.VK_ESCAPE) {
                startGame();
            }
        }
        else if (gameOver) {
            if (key == KeyEvent.VK_R) {
                restartGame();
            }
        } else if (key == KeyEvent.VK_P || key == KeyEvent.VK_ESCAPE) {
            paused = !paused;
            if (paused) {
                gameTimer.stop();
            } else {
                gameTimer.start();
            }
            repaint();
        } else {
            player.keyPressed(e);
        }
    }

    private void restartGame() {
        gameOver = false;
        running = true;
        score = 0;
        player.setLocationsCrossed(0);
        player.setHealth(100);
        world = new World();
        bullets = new ArrayList<>();
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2, 32, 32, this);
        world.generateInitialWorldObjects(player.getX(), player.getY(), PANEL_WIDTH, PANEL_HEIGHT);
        gameTimer.start();

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
            update();
        }
        repaint();
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver && !inShop && !paused) {
            shoot();
        }
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