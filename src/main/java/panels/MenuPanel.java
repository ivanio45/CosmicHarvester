package panels;

import utils.Difficulty;
import utils.SoundManager;
import panels.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class MenuPanel extends JPanel {

    private ImageIcon backgroundImage;
    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;
    private final int PANEL_WIDTH;
    private final int PANEL_HEIGHT;
    private Difficulty selectedDifficulty = Difficulty.EASY; // По умолчанию средняя сложность
    SoundManager soundManager = SoundManager.getInstance();

    private JPanel settingsPanel;
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;
    private boolean showingSettings = false;

    private JCheckBox soundToggle;
    private JSlider musicVolumeSlider;
    private JSlider effectsVolumeSlider;

    public GamePanel gamePanel;

    public MenuPanel(int width, int height, GamePanel gamePanel) {
        this.PANEL_WIDTH = width;
        this.PANEL_HEIGHT = height;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null);

        createSettingsPanel();
        try {
            String imagePath = "/background.gif";
            URL imageUrl = getClass().getResource(imagePath);
            if (imageUrl == null) {
                backgroundImage = null;
            } else {
                backgroundImage = new ImageIcon(imageUrl);
            }

        } catch (Exception e) {
            System.err.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
            backgroundImage = null;
        }

        playButton = createButton("Играть", PANEL_WIDTH/2 - 200, 250);
        settingsButton = createButton("Настройки", PANEL_WIDTH/2 - 200, 350);
        exitButton = createButton("Выйти", PANEL_WIDTH/2 - 200, 450);

        playButton.addActionListener(e -> {
            soundManager.playSound("button");
            gamePanel.resumeGame();
            System.out.println("Играть нажата");
        });

        settingsButton.addActionListener(e -> {
            soundManager.playSound("button");
            if (settingsPanel != null) {
                toggleSettings();
            }
            System.out.println("Настройки нажаты");
        });

        exitButton.addActionListener(e -> {
            soundManager.playSound("button");
            System.exit(0);
        });

        add(settingsPanel);
        add(playButton);
        add(settingsButton);
        add(exitButton);

        settingsPanel.setVisible(false);
        soundManager.playBackgroundMusic();

        this.gamePanel = gamePanel;
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setBounds(PANEL_WIDTH/4, 100, PANEL_WIDTH/2, 400);
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setOpaque(false);

        // Панель сложности
        JPanel difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new BoxLayout(difficultyPanel, BoxLayout.Y_AXIS));
        difficultyPanel.setOpaque(false);
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        JLabel difficultyLabel = createLabelWithBackground("Сложность:");
        difficultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        difficultyPanel.add(difficultyLabel);

        easyButton = createRadioButtonWithBackground("Легкий");
        mediumButton = createRadioButtonWithBackground("Средний");
        hardButton = createRadioButtonWithBackground("Тяжелый");

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        switch(selectedDifficulty) {
            case Difficulty.EASY: easyButton.setSelected(true); break;
            case Difficulty.MEDIUM: mediumButton.setSelected(true); break;
            case Difficulty.HARD: hardButton.setSelected(true); break;
        }

        // Центрируем кнопки сложности
        easyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mediumButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hardButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        difficultyPanel.add(easyButton);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        difficultyPanel.add(mediumButton);
        difficultyPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        difficultyPanel.add(hardButton);

        settingsPanel.add(difficultyPanel);

        // Панель звука
        JPanel soundPanel = new JPanel();
        soundPanel.setLayout(new BoxLayout(soundPanel, BoxLayout.Y_AXIS));
        soundPanel.setOpaque(false);
        soundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel soundLabel = createLabelWithBackground("Настройки звука:");
        soundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundPanel.add(soundLabel);

        // Включение/выключение звуков
        soundToggle = new JCheckBox("Включить звуки", soundManager.isSoundEnabled());
        styleCheckBox(soundToggle);
        soundToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        soundToggle.addItemListener(e -> {
            soundManager.toggleSound(soundToggle.isSelected());
        });
        soundPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        soundPanel.add(soundToggle);

        // Громкость музыки
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

        // Громкость эффектов
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

        settingsPanel.add(soundPanel);
        add(settingsPanel);
        settingsPanel.setVisible(false);
    }

    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setForeground(Color.WHITE);
        checkBox.setFont(new Font("Arial", Font.BOLD, 16));
        checkBox.setOpaque(false);
        checkBox.setContentAreaFilled(false);
        checkBox.setBorderPainted(false);
        checkBox.setFocusPainted(false);
    }

    private void styleSlider(JSlider slider) {
        slider.setOpaque(false);
        slider.setForeground(Color.WHITE);
        slider.setBackground(new Color(70, 70, 70, 180));
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(10);
    }

    private JLabel createLabelWithBackground(String text) {
        JLabel label = new JLabel(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(50, 50, 50, 200)); // Темно-серый с прозрачностью
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 22));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    private JRadioButton createRadioButtonWithBackground(String text) {
        JRadioButton button = new JRadioButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(70, 70, 70, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        return button;
    }


    private void toggleSettings() {
        showingSettings = !showingSettings;

        if (showingSettings) {
            settingsButton.setText("Назад");
            playButton.setVisible(false);
            exitButton.setVisible(false);
            settingsPanel.setVisible(true);
            settingsButton.setBounds(PANEL_WIDTH/2 - 200, 600, 400, 50);
        } else {
            settingsButton.setText("Настройки");
            playButton.setVisible(true);
            exitButton.setVisible(true);
            settingsPanel.setVisible(false);
            settingsButton.setBounds(PANEL_WIDTH/2 - 200, 350, 400, 50);

            if(easyButton.isSelected()) selectedDifficulty = Difficulty.EASY;
            else if(mediumButton.isSelected()) selectedDifficulty = Difficulty.MEDIUM;
            else if(hardButton.isSelected()) selectedDifficulty = Difficulty.HARD;
        }

        repaint();
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setForeground(Color.black);
        button.setBackground(Color.MAGENTA);
        button.setBounds(x, y, 400, 50);
        button.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, 20));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            backgroundImage.paintIcon(this, g, 0, 0);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        }
    }

    public JButton getPlayButton() {
        return this.playButton;
    }
}