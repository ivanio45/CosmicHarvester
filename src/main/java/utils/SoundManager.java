package utils;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class SoundManager {
    private static final String SETTINGS_FILE = "sound_settings.dat";
    private static SoundManager instance;
    private HashMap<String, Clip> soundEffects;
    private Clip backgroundMusic;
    private float musicVolume;
    private float effectsVolume;
    private boolean soundEnabled;


    private SoundManager() {
        soundEffects = new HashMap<>();
        loadSettings();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                SoundSettings settings = (SoundSettings) ois.readObject();
                this.musicVolume = settings.getMusicVolume();
                this.effectsVolume = settings.getEffectsVolume();
                this.soundEnabled = settings.isSoundEnabled();
            } catch (Exception e) {
                setDefaultSettings();
                System.err.println("Error loading sound settings: " + e.getMessage());
            }
        } else {
            setDefaultSettings();
        }
    }

    private void setDefaultSettings() {
        this.musicVolume = 0.7f;
        this.effectsVolume = 0.7f;
        this.soundEnabled = true;
    }

    public void saveSettings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SETTINGS_FILE))) {
            oos.writeObject(new SoundSettings(musicVolume, effectsVolume, soundEnabled));
        } catch (Exception e) {
            System.err.println("Error saving sound settings: " + e.getMessage());
        }
    }

    private void loadSounds() {
        try {
            loadSound("shoot", "/shot_sound.wav");
            loadSound("explosion", "/kill_sound.wav");
            loadSound("pickup", "/take_sound.wav");
            loadSound("button", "/button_sound.wav");
            loadSound("buy", "/buy_sound.wav");
            loadSound("game_over", "/game_over.wav");



            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource("/background(1).wav")));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);

            setSoundEffectsVolume(effectsVolume);
            toggleSound(soundEnabled);
            setBackgroundVolume(musicVolume);

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }

    private void loadSound(String name, String path) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource(path)));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(effectsVolume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
            soundEffects.put(name, clip);
        } catch (Exception e) {
            System.err.println("Error loading sound " + name + ": " + e.getMessage());
        }
    }

    public void playSound(String name) {
        if (!soundEnabled) return;

        Clip clip = soundEffects.get(name);
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void playBackgroundMusic() {
        if (soundEnabled && backgroundMusic != null && !backgroundMusic.isRunning()) {
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void setBackgroundVolume(float volume) {
        this.musicVolume = volume;
        if (backgroundMusic != null) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
        saveSettings();
    }

    public void setSoundEffectsVolume(float volume) {
        this.effectsVolume = volume;
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        }
        saveSettings();
    }

    public void toggleSound(boolean enable) {
        this.soundEnabled = enable;
        if (backgroundMusic != null) {
            if (enable) {
                playBackgroundMusic();
            } else {
                stopBackgroundMusic();
            }
        }
        saveSettings();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public float getBackgroundVolume() {
        return musicVolume;
    }

    public float getEffectsVolume() {
        return effectsVolume;
    }
}