package gamestart;

import panels.GamePanel;
import panels.MenuPanel;
import utils.Difficulty;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class CosmicHarvester extends JFrame {

    private final GamePanel gamePanel;
    private final MenuPanel menuPanel;
    private final int WINDOW_WIDTH = 1200;
    private final int WINDOW_HEIGHT = 800;
    private boolean isFullScreen = false;
    private GraphicsDevice graphicsDevice;

    public CosmicHarvester() {
        setTitle("Cosmic Harvester");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new CardLayout());

        gamePanel = new GamePanel();
        menuPanel = new MenuPanel(WINDOW_WIDTH, WINDOW_HEIGHT, gamePanel, this);
        add(menuPanel, "menu");
        add(gamePanel, "game");

        menuPanel.getPlayButton().addActionListener(e -> {
            showGamePanel();
        });

        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        // Установить начальный оконный режим
        setWindowedMode();

        // Обработчик изменения размера окна
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gamePanel.updatePanelSize(getWidth(), getHeight());
            }
        });

        setVisible(true);
    }

    public void setWindowedMode() {
        if (isFullScreen) {
            graphicsDevice.setFullScreenWindow(null);
            isFullScreen = false;
        }
        setResizable(false);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        gamePanel.updatePanelSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public void setFullScreenMode() {
        if (!isFullScreen) {
            dispose(); // Закрыть текущее окно для изменения режима
            setUndecorated(true); // Убрать рамку окна
            setResizable(false);
            graphicsDevice.setFullScreenWindow(this);
            isFullScreen = true;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            gamePanel.updatePanelSize(screenSize.width, screenSize.height);
            setVisible(true);
        }
    }

    public boolean isFullScreen() {
        return isFullScreen;
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