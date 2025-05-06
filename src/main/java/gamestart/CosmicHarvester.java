package gamestart;

import panels.GamePanel;
import panels.MenuPanel;
import utils.Difficulty;

import javax.swing.*;
import java.awt.*;

public class CosmicHarvester extends JFrame {

    private final GamePanel gamePanel;
    private final MenuPanel menuPanel;
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;

    public CosmicHarvester() {
        setTitle("Cosmic Harvester");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new CardLayout());

        gamePanel = new GamePanel();
        menuPanel = new MenuPanel(WINDOW_WIDTH, WINDOW_HEIGHT, gamePanel);
        add(menuPanel, "menu");
        add(gamePanel, "game");

        menuPanel.getPlayButton().addActionListener(e -> {
            showGamePanel();
        });

        setVisible(true);
    }

    private void showGamePanel() {
        Difficulty difficulty = menuPanel.getSelectedDifficulty();
        gamePanel.setDifficulty(difficulty);
        CardLayout cardLayout = (CardLayout) getContentPane().getLayout();
        cardLayout.show(getContentPane(), "game");
        gamePanel.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CosmicHarvester::new);
    }
}