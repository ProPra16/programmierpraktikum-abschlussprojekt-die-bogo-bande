package sound;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class Sound {


    private Clip soundClip;

    private FloatControl volume;

    public Sound(String soundFile) {

        soundFile = "./" + soundFile;
        File file = new File(soundFile);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(file.toURI().toURL());
            soundClip = AudioSystem.getClip();
            soundClip.open(audioIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
            volume = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);

    }

    public void start() {
        soundClip.start();
    }

    public void ende(){soundClip.close();}

    public void setVolume(float volume) {
            this.volume.setValue(volume);
    }

    FloatControl getVolumeControl() {
        return volume;
    }
}
