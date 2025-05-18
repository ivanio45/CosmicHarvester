package panels;

import utils.SoundManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PausePanel extends JPanel {

    private GamePanel gamePanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private JCheckBox soundToggle;
    private JSlider musicVolumeSlider;
    private JSlider effectsVolumeSlider;

    private static final String MAIN_MENU_CARD = "MainMenu";
    private static final String SETTINGS_CARD = "Settings";

    private SoundManager soundManager = SoundManager.getInstance();

    public PausePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        setOpaque(false);
        setLayout(new GridBagLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        JPanel mainMenuPanel = createMainMenuPanel();
        JPanel settingsPanel = createSettingsPanel();
        cardPanel.add(mainMenuPanel, MAIN_MENU_CARD);
        cardPanel.add(settingsPanel, SETTINGS_CARD);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        add(cardPanel, gbc);
        cardLayout.show(cardPanel, MAIN_MENU_CARD);
        setVisible(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }

    private JPanel createMainMenuPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton resumeButton = new JButton("Продолжить игру");
        styleButton(resumeButton);
        resumeButton.addActionListener(e -> gamePanel.resumeGame());
        panel.add(resumeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton settingsButton = new JButton("Настройки");
        styleButton(settingsButton);
        settingsButton.addActionListener(e -> {
            showSettings();
        });
        panel.add(settingsButton);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Кнопка "Выход в главное меню"
        JButton exitButton = new JButton("Выход в главное меню");
        styleButton(exitButton);
        exitButton.addActionListener(e -> {
            gamePanel.exitToMenu();
        });
        panel.add(exitButton);
        return panel;
    }

    private JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel soundPanel = new JPanel();
        soundPanel.setLayout(new BoxLayout(soundPanel, BoxLayout.Y_AXIS));
        soundPanel.setOpaque(false);

        JLabel soundLabel = createLabelWithBackground("Настройки звука:");
        soundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundPanel.add(soundLabel);

        soundToggle = new JCheckBox("Включить звуки", soundManager.isSoundEnabled());
        styleCheckBox(soundToggle);
        soundToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundToggle.addItemListener(e -> soundManager.toggleSound(soundToggle.isSelected()));
        soundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        soundPanel.add(soundToggle);

        JPanel musicPanel = new JPanel();
        musicPanel.setLayout(new BoxLayout(musicPanel, BoxLayout.Y_AXIS));
        musicPanel.setOpaque(false);

        JLabel musicLabel = new JLabel("Громкость музыки:");
        styleLabel(musicLabel);
        musicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        musicPanel.add(musicLabel);

        musicVolumeSlider = new JSlider(0, 100, (int)(soundManager.getBackgroundVolume() * 100));
        styleSlider(musicVolumeSlider);
        musicVolumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        musicVolumeSlider.addChangeListener(e -> {
            float volume = musicVolumeSlider.getValue() / 100f;
            soundManager.setBackgroundVolume(volume);
        });
        musicPanel.add(musicVolumeSlider);

        soundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        soundPanel.add(musicPanel);

        JPanel effectsPanel = new JPanel();
        effectsPanel.setLayout(new BoxLayout(effectsPanel, BoxLayout.Y_AXIS));
        effectsPanel.setOpaque(false);

        JLabel effectsLabel = new JLabel("Громкость эффектов:");
        styleLabel(effectsLabel);
        effectsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectsPanel.add(effectsLabel);

        effectsVolumeSlider = new JSlider(0, 100, (int)(soundManager.getEffectsVolume() * 100));
        styleSlider(effectsVolumeSlider);
        effectsVolumeSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectsVolumeSlider.addChangeListener(e -> {
            float volume = effectsVolumeSlider.getValue() / 100f;
            soundManager.setSoundEffectsVolume(volume);
        });
        effectsPanel.add(effectsVolumeSlider);

        soundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        soundPanel.add(effectsPanel);

        panel.add(soundPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        JButton backButton = new JButton("Назад");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            showMainMenu();
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(backButton);

        return panel;
    }

    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 100));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.setOpaque(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMinimumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(250, 50));
        button.setMaximumSize(new Dimension(300, 60));
    }

    private void styleLabel(JLabel label) {
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(Color.WHITE);
        label.setOpaque(false);
    }

    private JLabel createLabelWithBackground(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 22));
        label.setForeground(Color.WHITE);
        label.setOpaque(false);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return label;
    }


    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setFont(new Font("Arial", Font.PLAIN, 18));
        checkBox.setForeground(Color.WHITE);
        checkBox.setOpaque(false);
        checkBox.setFocusPainted(false);
    }

    private void styleSlider(JSlider slider) {
        slider.setForeground(Color.WHITE);
        slider.setOpaque(false);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setSnapToTicks(true);
    }


    public void showMainMenu() {
        cardLayout.show(cardPanel, MAIN_MENU_CARD);
    }

    public void showSettings() {
        cardLayout.show(cardPanel, SETTINGS_CARD);
        if (soundToggle != null) {
            soundToggle.setSelected(soundManager.isSoundEnabled());
        }
        if (musicVolumeSlider != null) {
            musicVolumeSlider.setValue((int) (soundManager.getBackgroundVolume() * 100));
        }
        if (effectsVolumeSlider != null) {
            effectsVolumeSlider.setValue((int) (soundManager.getEffectsVolume() * 100));
        }
    }
}