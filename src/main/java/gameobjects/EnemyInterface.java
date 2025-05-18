package gameobjects;

import java.awt.*;

public interface EnemyInterface {
    void update(int playerX, int playerY);
    void draw(Graphics g, int worldX, int worldY);
    void takeDamage(int damage);
    boolean isAlive();
    int getX();
    int getY();
}