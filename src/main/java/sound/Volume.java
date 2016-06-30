package sound;

import data.Config;

import javax.sound.sampled.FloatControl;

public class Volume {
    private static float volume = Config.loadFloatFromConfig("build/resources/main/data/config.cfg","SOUNDVOLUME");
    private static float minVol = new Sound("build/resources/main/sound/test.wav").getVolumeControl().getMinimum();

    public static void setVolume(float i){
        volume = i;
        Config.saveConfig("build/resources/main/data/config.cfg","SOUNDVOLUME",volume);
    }

    public static float getVolume(){
        return volume;
    }

    public static double initVolume(){
            Sound sound = new Sound("build/resources/main/sound/test.wav");
            FloatControl soundControl = sound.getVolumeControl();
            sound.setVolume(soundControl.getMinimum());
            return (1-(-volume/-soundControl.getMinimum()))*100;
    }

    public static float getMinVol(){
        return minVol;
    }

}
