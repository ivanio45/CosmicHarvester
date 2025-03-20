import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Random;

public class CosmicHarvester extends JFrame {

    private GamePanel gamePanel;
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;

    public CosmicHarvester() {
        setTitle("Cosmic Harvester");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CosmicHarvester::new);
    }
}