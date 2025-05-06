package utils;

import gameobjects.Asteroid;
import gameobjects.Boss;
import gameobjects.Enemy;
import gameobjects.EnemyType;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
;

public class World {
    private final List<Asteroid> asteroids;
    private final List<Enemy> enemies;
    private Boss boss;
    private final Random random = new Random();
    private Difficulty difficulty;

    public World(Difficulty difficulty) {
        this.difficulty = difficulty;
        asteroids = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    public void generateObjects(int PANEL_WIDTH, int PANEL_HEIGHT) {
        int asteroidCount = random.nextInt(5,10);

        for (int i = 0; i < asteroidCount; i++) {
            int x = random.nextInt(PANEL_WIDTH);
            int y = random.nextInt(PANEL_HEIGHT);
            double rand = random.nextDouble();

            Asteroid.AsteroidType type = Asteroid.AsteroidType.NORMAL;

            if (rand < 0.02){
                type = Asteroid.AsteroidType.BLACK_HOLE;
            }
            else if (rand < 0.05) {
                type = Asteroid.AsteroidType.HEALTH_PACK;
            } else if (rand < 0.09) {
                type = Asteroid.AsteroidType.SHIELD;
            } else if (rand < 0.3) {
                type = Asteroid.AsteroidType.DANGEROUS;
            } else if (rand < 0.4) {
                type = Asteroid.AsteroidType.RARE;
            }

            if (type == Asteroid.AsteroidType.BLACK_HOLE){
                Asteroid asteroid = new Asteroid(x, y, 150, 150, type);
                asteroids.add(asteroid);
            }
            else {
                Asteroid asteroid = new Asteroid(x, y, 40, 40, type);
                asteroids.add(asteroid);
            }
        }

        int enemyCount = difficulty.getEnemyCount();
        for (int i = 0; i < enemyCount; i++) {
            spawnEnemy(PANEL_WIDTH, PANEL_HEIGHT);
        }
    }

    public void spawnEnemy(int PANEL_WIDTH, int PANEL_HEIGHT) {
        int side = random.nextInt(4);
        int x, y;

        switch (side) {
            case 0:
                x = -100;
                y = random.nextInt(PANEL_HEIGHT);
                break;
            case 1:
                x = random.nextInt(PANEL_WIDTH);
                y = -100;
                break;
            case 2:
                x = PANEL_WIDTH + 100;
                y = random.nextInt(PANEL_HEIGHT);
                break;
            default:
                x = random.nextInt(PANEL_WIDTH);
                y = PANEL_HEIGHT + 100;
        }

        int baseSize = 60 + random.nextInt(20);
        int size = (int) (baseSize * difficulty.getDifficultyMultiplier());
        EnemyType type = EnemyType.values()[random.nextInt(EnemyType.values().length)];
        Enemy enemy = new Enemy(x, y, size, size, type);
        enemies.add(enemy);
    }

    public void spawnBoss(int x, int y, int locationsCrossed) {
        int health = 800;
        if (locationsCrossed >= 8) {
            health = 1200 + (locationsCrossed - 8) * 100;
        } else if (locationsCrossed >= 4) {
            health = 1000;
        }

        int size = (int) (100 * difficulty.getDifficultyMultiplier());
        boss = new Boss(x, y, size, size, health, EnemyType.values()[random.nextInt(EnemyType.values().length)]);
        enemies.add(boss);
    }

    public List<Asteroid> getAsteroids() {
        return asteroids;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public Boss getBoss() {
        return boss;
    }

    public void setBoss(Boss boss) {
        this.boss = boss;
    }
}