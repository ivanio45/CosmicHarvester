package utils;

import java.io.Serializable;

public class SoundSettings implements Serializable {
    private final float musicVolume;
    private final float effectsVolume;
    private final boolean soundEnabled;

    public SoundSettings(float musicVolume, float effectsVolume, boolean soundEnabled) {
        this.musicVolume = musicVolume;
        this.effectsVolume = effectsVolume;
        this.soundEnabled = soundEnabled;
    }

    public float getMusicVolume() { return musicVolume; }
    public float getEffectsVolume() { return effectsVolume; }
    public boolean isSoundEnabled() { return soundEnabled; }
}