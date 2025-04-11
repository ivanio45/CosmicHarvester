public enum Difficulty {
    EASY(3, 1.0f),
    MEDIUM(5, 1.5f),
    HARD(7, 2.0f);

    private final int enemyCount;
    private final float difficultyMultiplier;

    Difficulty(int enemyCount, float difficultyMultiplier) {
        this.enemyCount = enemyCount;
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public int getEnemyCount() {
        return enemyCount;
    }

    public float getDifficultyMultiplier() {
        return difficultyMultiplier;
    }
}