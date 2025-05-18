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

    public void generateObjects(int panelWidth, int panelHeight) {
        int asteroidCount = random.nextInt(5,10);

        for (int i = 0; i < asteroidCount; i++) {
            int x = random.nextInt(panelWidth);
            int y = random.nextInt(panelHeight);
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

            double scaleX = (double) panelWidth / 1200;
            double scaleY = (double) panelHeight / 800;

            if (type == Asteroid.AsteroidType.BLACK_HOLE) {
                Asteroid asteroid = new Asteroid(x, y, (int)(150 * scaleX), (int)(150 * scaleY), type);
                asteroids.add(asteroid);
            } else {
                Asteroid asteroid = new Asteroid(x, y, (int)(40 * scaleX), (int)(40 * scaleY), type);
                asteroids.add(asteroid);
            }
        }

        int enemyCount = difficulty.getEnemyCount();
        for (int i = 0; i < enemyCount; i++) {
            spawnEnemy(panelWidth, panelHeight);
        }
    }

    public void spawnEnemy(int panelWidth, int panelHeight) {
        int side = random.nextInt(4);
        int x, y;

        double scaleX = (double) panelWidth / 1200;
        double scaleY = (double) panelHeight / 800;

        switch (side) {
            case 0:
                x = (int)(-200 * scaleX);
                y = random.nextInt(panelHeight);
                break;
            case 1:
                x = random.nextInt(panelWidth);
                y = (int)(-200 * scaleY);
                break;
            case 2:
                x = (int)(panelWidth + 200 * scaleX);
                y = random.nextInt(panelHeight);
                break;
            default:
                x = random.nextInt(panelWidth);
                y = (int)(panelHeight + 200 * scaleY);
        }

        int baseSize = 60 + random.nextInt(20);
        int size = (int) (baseSize * difficulty.getDifficultyMultiplier() * scaleX);
        EnemyType type = EnemyType.values()[random.nextInt(EnemyType.values().length)];
        Enemy enemy = new Enemy(x, y, size, size, type);
        enemies.add(enemy);
    }

    public void spawnBoss(int x, int y, int locationsCrossed) {
        int health = 800 + (locationsCrossed/4) * 250;


        double scaleX = (double) x / 500;
        int size = (int) (100 * difficulty.getDifficultyMultiplier() * scaleX);
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