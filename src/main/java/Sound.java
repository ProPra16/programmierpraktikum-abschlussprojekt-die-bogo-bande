import javafx.concurrent.Task;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;

/**
 * Created by root on 27.06.16.
 */
public class Sound {

    public static float Volume;

    public static void getVol(Controller controller) {
        FloatControl x = sound("build/resources/main/sound/test.wav");
        x.setValue(x.getMinimum());
        Volume = 0;
        controller.volSlider.setValue((-x.getMinimum() / (-x.getMinimum() + x.getMaximum()) * 100));

    }

    public static FloatControl sound(String soundFile) {
        File f = new File("./" + soundFile);
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            return volume;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
