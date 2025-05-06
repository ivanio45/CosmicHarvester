package utils;

import javax.sound.sampled.*;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class SoundManager {
    private static SoundManager instance;
    private HashMap<String, Clip> soundEffects;
    private Clip backgroundMusic;
    private float volume = 0.7f;
    private boolean soundEnabled = true;

    private SoundManager() {
        soundEffects = new HashMap<>();
        loadSounds();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
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

            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            setBackgroundVolume(volume);

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
        if (soundEnabled && !backgroundMusic.isRunning()) {
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    public void setBackgroundVolume(float volume) {
        this.volume = volume;
        if (backgroundMusic != null) {
            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);
        }
    }

    public void setSoundEffectsVolume(float volume) {
        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
                gainControl.setValue(dB);
            }
        }
    }

    public void toggleSound(boolean enable) {
        soundEnabled = enable;
        if (enable) {
            playBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public float getBackgroundVolume() {
        return volume;
    }

    public float getEffectsVolume() {
        return volume;
    }
}