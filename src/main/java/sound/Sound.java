package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Sound {

    private String soundFile;

    private Clip soundClip;

    private FloatControl volume;

    public Sound(String soundFile) {
        try {
            this.soundFile = "./" + soundFile;
            File file = new File(this.soundFile);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
            soundClip = AudioSystem.getClip();
            soundClip.open(audioIn);
            volume = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        soundClip.start();
    }

    public void stop() {
        soundClip.stop();
    }

    public void mute() { setVolume(volume.getMinimum()); }

    public void setVolume(float volume) {
        this.volume.setValue(volume);
    }

    public float getVolume() {
        return volume.getValue();
    }

    public FloatControl getVolumeControl(){
        return volume;
    }
}
