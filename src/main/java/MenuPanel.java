import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {

    private ImageIcon backgroundImage;
    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;
    private final int PANEL_WIDTH;
    private final int PANEL_HEIGHT;

    public MenuPanel(int width, int height) {
        this.PANEL_WIDTH = width;
        this.PANEL_HEIGHT = height;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);

        try {
            backgroundImage = new ImageIcon("C:/Users/Spsch/IdeaProjects/CosmicHarvester/src/main/resources/background.gif"); // Замените на путь к вашему изображению
        } catch (Exception e) {
            System.err.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
            backgroundImage = null;
        }

        playButton = createButton("Играть", 100, 150);
        settingsButton = createButton("Настройки", 100, 250);
        exitButton = createButton("Выйти", 100, 350);

        playButton.addActionListener(e -> {
            System.out.println("Играть нажата");
        });

        settingsButton.addActionListener(e -> {
            System.out.println("Настройки нажаты");
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        add(playButton);
        add(settingsButton);
        add(exitButton);
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBounds(x, y, 200, 50);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        if (backgroundImage != null) {
            backgroundImage.paintIcon(this, g, 0, 0);
        }
    }

    public JButton getPlayButton() { //Method for the main program
        return this.playButton;
    }
}