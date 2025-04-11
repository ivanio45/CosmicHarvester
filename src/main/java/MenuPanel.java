import javax.swing.*;
import java.awt.*;


public class MenuPanel extends JPanel {

    private ImageIcon backgroundImage;
    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;
    private final int PANEL_WIDTH;
    private final int PANEL_HEIGHT;
    private Difficulty selectedDifficulty = Difficulty.EASY; // По умолчанию средняя сложность

    private JPanel settingsPanel;
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;
    private boolean showingSettings = false;

    public MenuPanel(int width, int height) {
        this.PANEL_WIDTH = width;
        this.PANEL_HEIGHT = height;
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setLayout(null); // Используем null layout для абсолютного позиционирования

        createSettingsPanel();

        // 1. Загрузка фонового изображения
        try {
            backgroundImage = new ImageIcon("resources/background.gif"); // Замените на путь к вашему изображению
        } catch (Exception e) { // Обрабатываем любые исключения при загрузке
            System.err.println("Не удалось загрузить фоновое изображение: " + e.getMessage());
            backgroundImage = null;
        }

        // 2. Создание кнопок
        playButton = createButton("Играть", 100, 150); // x, y
        settingsButton = createButton("Настройки", 100, 250);
        exitButton = createButton("Выйти", 100, 350);

        // 3. Добавление обработчиков событий (ActionListener) для кнопок
        playButton.addActionListener(e -> {
            // Действие при нажатии кнопки "Играть"
            //  Будет вызвано из CosmicHarvester
            System.out.println("Играть нажата"); //Для теста
        });

        settingsButton.addActionListener(e -> {
            if (settingsPanel != null) {
                toggleSettings();
            }
            System.out.println("Настройки нажаты"); //Для теста
        });

        exitButton.addActionListener(e -> {
            // Действие при нажатии кнопки "Выйти"
            System.exit(0); // Завершение работы приложения
        });

        // 4. Добавление кнопок на панель
        add(settingsPanel);
        add(playButton);
        add(settingsButton);
        add(exitButton);

        settingsPanel.setVisible(false);
    }

    private void createSettingsPanel() {
        settingsPanel = new JPanel();
        settingsPanel.setBounds(400, 100, PANEL_WIDTH-450, 200); // Правая позиция
        settingsPanel.setLayout(new GridLayout(4, 1));
        settingsPanel.setOpaque(false); // Прозрачный фон

        JLabel difficultyLabel = new JLabel("Уровень сложности:");
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 16));

        easyButton = new JRadioButton("Легкий");
        mediumButton = new JRadioButton("Средний");
        hardButton = new JRadioButton("Тяжелый");

        // Стилизуем кнопки
        styleRadioButton(easyButton);
        styleRadioButton(mediumButton);
        styleRadioButton(hardButton);

        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);

        // Устанавливаем текущий выбор
        switch(selectedDifficulty) {
            case Difficulty.EASY: easyButton.setSelected(true); break;
            case Difficulty.MEDIUM: mediumButton.setSelected(true); break;
            case Difficulty.HARD: hardButton.setSelected(true); break;
        }

        settingsPanel.add(difficultyLabel);
        settingsPanel.add(easyButton);
        settingsPanel.add(mediumButton);
        settingsPanel.add(hardButton);

        add(settingsPanel);
        settingsPanel.setVisible(false);
    }

    private void styleRadioButton(JRadioButton button) {
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setOpaque(false);
        button.setFocusPainted(false);
    }


    private void toggleSettings() {
        showingSettings = !showingSettings;

        if (showingSettings) {
            settingsButton.setText("Назад");
            playButton.setVisible(false);
            exitButton.setVisible(false);
            settingsPanel.setVisible(true);
        } else {
            settingsButton.setText("Настройки");
            playButton.setVisible(true);
            exitButton.setVisible(true);
            settingsPanel.setVisible(false);

            // Сохраняем выбранную сложность
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
        button.setBounds(x, y, 200, 50); // Размеры и позиция кнопки (x, y, ширина, высота)
        button.setFont(new Font("Arial", Font.BOLD, 20));
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);  // Или любой другой цвет фона
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT); // Закрашиваем фон

        if (backgroundImage != null) {
            backgroundImage.paintIcon(this, g, 0, 0);  // Отрисовываем ImageIcon
        }
    }

    public JButton getPlayButton() { //Method for the main program
        return this.playButton;
    }
}