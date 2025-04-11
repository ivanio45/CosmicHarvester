import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;;

class World {
    private List<Asteroid> asteroids;
    private List<Enemy> enemies;
    private Boss boss; // Добавляем босса (ADDED)
    private Random random = new Random();
    private Difficulty difficulty;
    private int visibleAreaPadding = 100;

    public boolean isOutOfBounds(GameObject object, int worldX, int worldY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        int generationRadius = 100;
        return object.getX() < worldX - generationRadius - PANEL_WIDTH / 2 ||
                object.getX() > worldX + PANEL_WIDTH + generationRadius + PANEL_WIDTH / 2 ||
                object.getY() < worldY - generationRadius - PANEL_HEIGHT / 2 ||
                object.getY() > worldY + PANEL_HEIGHT + generationRadius + PANEL_HEIGHT / 2;
    }

    public World(Difficulty difficulty) {
        this.difficulty = difficulty;
        asteroids = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    public List<Asteroid> getAsteroids() {
        return asteroids;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    // Method to get the Boss (ADDED)
    public Boss getBoss() {
        return boss;
    }

    public void generateInitialWorldObjects(int playerX, int playerY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        generateObjectsAroundPlayer(playerX, playerY, PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void generateObjectsAroundPlayer(int playerX, int playerY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        generateObjects(
                playerX - PANEL_WIDTH / 2 - visibleAreaPadding,
                playerY - PANEL_HEIGHT / 2 - visibleAreaPadding,
                playerX + PANEL_WIDTH / 2 + visibleAreaPadding,
                playerY + PANEL_HEIGHT / 2 + visibleAreaPadding
        );
    }

    public void generateObjects(int minX, int minY, int maxX, int maxY) {
        int asteroidCount = 10;

        for (int i = 0; i < asteroidCount; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;
            Asteroid asteroid = new Asteroid(x, y, 32, 32);
            double rand = random.nextDouble();
            if (rand < 0.2) {
                asteroid.setType(Asteroid.AsteroidType.DANGEROUS);
                asteroid.setColor(Color.ORANGE);
            } else if (rand < 0.5) {
                asteroid.setType(Asteroid.AsteroidType.RARE);
                asteroid.setColor(Color.CYAN);
            }
            asteroids.add(asteroid);
        }

        int enemyCount = difficulty.getEnemyCount();
        for (int i = 0; i < enemyCount; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;
            int baseSize = 20 + random.nextInt(30);
            int size = (int) (baseSize * difficulty.getDifficultyMultiplier());

            // Выбираем случайный тип врага
            EnemyType type = EnemyType.values()[random.nextInt(EnemyType.values().length)];
            enemies.add(new Enemy(x, y, size, size, type));
        }
    }

    //method to spawn the boss (ADDED)
    public void spawnBoss(int x, int y, int panelWidth, int panelHeight, int locationsCrossed) {
        int health = 800;
        int speed = 2;

        // Adjust boss stats based on locationsCrossed
        if (locationsCrossed >= 8) {
            health = 1200 + (locationsCrossed - 8) * 100;
            speed = 3; // Increase speed
        } else if (locationsCrossed >= 4) {
            health = 1000; // Base health for first boss
            speed = 2;
        }

        boss = new Boss(x, y, 86, 86, health, speed); // Modified
    }

    public void updateWorldObjects(int worldX, int worldY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        asteroids.removeIf(asteroid -> isOutOfBounds(asteroid, worldX, worldY, PANEL_WIDTH, PANEL_HEIGHT));
        enemies.removeIf(enemy -> isOutOfBounds(enemy, worldX, worldY, PANEL_WIDTH, PANEL_HEIGHT));
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }
}