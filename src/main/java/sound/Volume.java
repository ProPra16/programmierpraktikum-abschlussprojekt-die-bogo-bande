package sound;

import javax.sound.sampled.FloatControl;

public class Volume {
    private static float volume = 0;
    private static float minVol = new Sound("build/resources/main/sound/test.wav").getVolumeControl().getMinimum();

    public static void setVolume(float i){
        volume = i;
    }

    public static float getVolume(){
        return volume;
    }

    public static double initVolume(){
            Sound sound = new Sound("build/resources/main/sound/test.wav");
            FloatControl soundControl = sound.getVolumeControl();
            sound.setVolume(soundControl.getMinimum());
            return (-soundControl.getMinimum() / (-soundControl.getMinimum() + soundControl.getMaximum()) * 100);
    }

    public static float getMinVol(){
        return minVol;
    }
}
