import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Color;

class World {

    private List<Asteroid> asteroids;
    private List<Enemy> enemies;
    private List<Resource> resources;
    private Random random = new Random();
    private int worldWidth = 2000;
    private int worldHeight = 2000;
    private int generationRadius = 500;

    public World() {
        asteroids = new ArrayList<>();
        enemies = new ArrayList<>();
        resources = new ArrayList<>();
    }

    public List<Asteroid> getAsteroids() {
        return asteroids;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void generateInitialWorldObjects(int playerX, int playerY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        generateObjectsAroundPlayer(playerX, playerY, PANEL_WIDTH, PANEL_HEIGHT);
    }

    public void generateObjectsAroundPlayer(int playerX, int playerY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        generateObjects(playerX - PANEL_WIDTH / 2 - generationRadius, playerY - PANEL_HEIGHT / 2 - generationRadius,
                playerX + PANEL_WIDTH / 2 + generationRadius, playerY + PANEL_HEIGHT / 2 + generationRadius);
    }

    public void generateObjects(int minX, int minY, int maxX, int maxY) {
        int asteroidCount = 10;
        int enemyCount = 3;

        for (int i = 0; i < asteroidCount; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;

            boolean alreadyExists = false;
            for (Asteroid asteroid : asteroids) {
                if (asteroid.getX() == x && asteroid.getY() == y) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                Asteroid asteroid = new Asteroid(x, y, 32, 32);
                if (random.nextDouble() < 0.5) {
                    asteroid.setType(Asteroid.AsteroidType.RARE);
                    asteroid.setColor(Color.CYAN);
                }
                asteroids.add(asteroid);
            }
        }

        for (int i = 0; i < enemyCount; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;
            int size = 20 + random.nextInt(30);
            boolean alreadyExists = false;
            for (Enemy enemy : enemies) {
                if (enemy.getX() == x && enemy.getY() == y) {
                    alreadyExists = true;
                    break;
                }
            }
            if (!alreadyExists) {
                enemies.add(new Enemy(x, y, size, size));
            }
        }

        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(maxX - minX) + minX;
            int y = random.nextInt(maxY - minY) + minY;
            resources.add(new Resource(x, y, 10, 10));
        }
    }

    public void updateWorldObjects(int worldX, int worldY, int PANEL_WIDTH, int PANEL_HEIGHT) {
        asteroids.removeIf(asteroid -> asteroid.getX() < worldX - generationRadius - PANEL_WIDTH / 2 ||
                asteroid.getX() > worldX + PANEL_WIDTH + generationRadius + PANEL_WIDTH / 2 ||
                asteroid.getY() < worldY - generationRadius - PANEL_HEIGHT / 2 ||
                asteroid.getY() > worldY + PANEL_HEIGHT + generationRadius + PANEL_HEIGHT / 2);

        enemies.removeIf(enemy -> enemy.getX() < worldX - generationRadius - PANEL_WIDTH / 2 ||
                enemy.getX() > worldX + PANEL_WIDTH + generationRadius + PANEL_WIDTH / 2 ||
                enemy.getY() < worldY - generationRadius - PANEL_HEIGHT / 2 ||
                enemy.getY() > worldY + PANEL_HEIGHT + generationRadius + PANEL_HEIGHT / 2);

        resources.removeIf(resource -> resource.getX() < worldX - generationRadius - PANEL_WIDTH / 2 ||
                resource.getX() > worldX + PANEL_WIDTH + generationRadius + PANEL_WIDTH / 2 ||
                resource.getY() < worldY - generationRadius - PANEL_HEIGHT / 2 ||
                resource.getY() > worldY + PANEL_HEIGHT + generationRadius + PANEL_HEIGHT / 2);
    }
}