package panels;

import utils.Difficulty;
import utils.SoundManager;
import gamestart.CosmicHarvester;

import javax.swing.*;
import java.awt.*;
import java.net.URL;


public class MenuPanel extends JPanel {

    private ImageIcon backgroundImage;
    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;
    private JButton backButton;
    private final int DESIGN_WIDTH = 1200;
    private final int DESIGN_HEIGHT = 800;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
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
    private JCheckBox fullScreenToggle;

    public GamePanel gamePanel;
    private CosmicHarvester frame;

    public MenuPanel(int width, int height, GamePanel gamePanel, CosmicHarvester frame) {
        this.frame = frame;
        updatePanelSize(width, height);
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

        playButton = createButton("Играть", DESIGN_WIDTH/2 - 200, 250);
        settingsButton = createButton("Настройки", DESIGN_WIDTH/2 - 200, 350);
        exitButton = createButton("Выйти", DESIGN_WIDTH/2 - 200, 450);

        playButton.addActionListener(e -> {
            soundManager.playSound("button");
            gamePanel.resumeGame();
        });

        settingsButton.addActionListener(e -> {
            soundManager.playSound("button");
            if (settingsPanel != null) {
                toggleSettings();
            }
        });

        exitButton.addActionListener(e -> {
            soundManager.playSound("button");
            System.exit(0);
        });

        add(playButton);
        add(settingsButton);
        add(exitButton);
        add(settingsPanel);

        settingsPanel.setVisible(false);
        if (soundManager.isSoundEnabled()) {
            soundManager.playBackgroundMusic();
        }

        this.gamePanel = gamePanel;
    }

    public void updatePanelSize(int width, int height) {
        this.scaleX = (double) width / DESIGN_WIDTH;
        this.scaleY = (double) height / DESIGN_HEIGHT;
        setPreferredSize(new Dimension(width, height));
        updateComponents();
        if (gamePanel != null) {
            gamePanel.updatePanelSize(width, height);
        }
    }

    private void updateComponents() {
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        if (playButton != null) {
            int buttonX = (int)(currentWidth / 2 - 200 * scaleX);
            playButton.setBounds(buttonX, (int)(250 * scaleY), (int)(400 * scaleX), (int)(50 * scaleY));
            settingsButton.setBounds(buttonX, (int)(350 * scaleY), (int)(400 * scaleX), (int)(50 * scaleY));
            exitButton.setBounds(buttonX, (int)(450 * scaleY), (int)(400 * scaleX), (int)(50 * scaleY));
            int settingsX = (int)((DESIGN_WIDTH / 4) * scaleX);
            settingsPanel.setBounds(settingsX, (int)(100 * scaleY), (int)(DESIGN_WIDTH / 2 * scaleX), (int)((currentHeight - 200) * scaleY));
            backButton.setBounds(buttonX, (int)((currentHeight - 100) * scaleY), (int)(400 * scaleX), (int)(50 * scaleY));
        }
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBounds((int)(DESIGN_WIDTH/4 * scaleX), (int)(100 * scaleY), (int)(DESIGN_WIDTH/2 * scaleX), (int)(500 * scaleY));
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


        // Панель режима экрана
        JPanel screenModePanel = new JPanel();
        screenModePanel.setLayout(new BoxLayout(screenModePanel, BoxLayout.Y_AXIS));
        screenModePanel.setOpaque(false);
        screenModePanel.setBorder(BorderFactory.createEmptyBorder((int)(10 * scaleY), (int)(10 * scaleX), (int)(10 * scaleY), (int)(10 * scaleX)));

        JLabel screenModeLabel = createLabelWithBackground("Режим экрана:");
        screenModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        screenModePanel.add(screenModeLabel);

        fullScreenToggle = new JCheckBox("Полноэкранный режим", frame.isFullScreen());
        styleCheckBox(fullScreenToggle);
        fullScreenToggle.setAlignmentX(Component.CENTER_ALIGNMENT);
        fullScreenToggle.addItemListener(e -> {
            if (fullScreenToggle.isSelected()) {
                frame.setFullScreenMode();
            } else {
                frame.setWindowedMode();
            }
            updatePanelSize(frame.getWidth(), frame.getHeight()); // Обновляем размеры после переключения
        });
        screenModePanel.add(Box.createRigidArea(new Dimension(0, (int)(10 * scaleY))));
        screenModePanel.add(fullScreenToggle);

        settingsPanel.add(screenModePanel);
        add(settingsPanel);
        settingsPanel.setVisible(false);

        backButton = new JButton("Назад");
        backButton.setForeground(Color.BLACK);
        backButton.setBackground(Color.MAGENTA);
        backButton.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, (int)(20 * scaleX)));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> {
            soundManager.playSound("button");
            toggleSettings();
        });
        settingsPanel.add(Box.createRigidArea(new Dimension(0, (int)(20 * scaleY))));
        settingsPanel.add(backButton);
        updateComponents();
    }

    private void styleLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, (int)(16 * scaleX)));
    }

    private void styleCheckBox(JCheckBox checkBox) {
        checkBox.setForeground(Color.WHITE);
        checkBox.setFont(new Font("Arial", Font.BOLD, (int)(16 * scaleX)));
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
                g2d.setColor(new Color(50, 50, 50, 200));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), (int)(15 * scaleX), (int)(15 * scaleY));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, (int)(22 * scaleX)));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder((int)(5 * scaleY), (int)(10 * scaleX), (int)(5 * scaleY), (int)(10 * scaleX)));
        return label;
    }

    private JRadioButton createRadioButtonWithBackground(String text) {
        JRadioButton button = new JRadioButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(70, 70, 70, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), (int)(10 * scaleX), (int)(10 * scaleY));
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, (int)(18 * scaleX)));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder((int)(5 * scaleY), (int)(15 * scaleX), (int)(5 * scaleY), (int)(5 * scaleX)));
        return button;
    }


    private void toggleSettings() {
        showingSettings = !showingSettings;

        if (showingSettings) {
            playButton.setVisible(false);
            exitButton.setVisible(false);
            settingsButton.setVisible(false);
            settingsPanel.setVisible(true);
            backButton.setVisible(true);
        } else {
            playButton.setVisible(true);
            exitButton.setVisible(true);
            settingsButton.setVisible(true);
            settingsPanel.setVisible(false);
            backButton.setVisible(false);

            if (easyButton.isSelected()) selectedDifficulty = Difficulty.EASY;
            else if (mediumButton.isSelected()) selectedDifficulty = Difficulty.MEDIUM;
            else if (hardButton.isSelected()) selectedDifficulty = Difficulty.HARD;
        }

        updateComponents();
        repaint();
    }

    public Difficulty getSelectedDifficulty() {
        return selectedDifficulty;
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setForeground(Color.black);
        button.setBackground(Color.MAGENTA);
        button.setBounds((int)((DESIGN_WIDTH / 2 - 200) * scaleX), (int)(y * scaleY), (int)(400 * scaleX), (int)(50 * scaleY));
        button.setFont(new Font("Franklin Gothic Heavy", Font.BOLD, (int)(20 * scaleX)));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.scale(scaleX, scaleY);
        if (backgroundImage != null) {
            Image image = backgroundImage.getImage();
            g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, DESIGN_WIDTH, DESIGN_HEIGHT);
        }
        g2d.scale(1 / scaleX, 1 / scaleY);
    }

    public JButton getPlayButton() {
        return this.playButton;
    }
}