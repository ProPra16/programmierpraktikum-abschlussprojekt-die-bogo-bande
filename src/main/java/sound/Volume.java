package sound;

import data.Config;

public class Volume {
    private static float volume = Config.loadFloatFromConfig("SOUNDVOLUME");
    private static float minVol = new Sound("sound/test.wav").getVolumeControl().getMinimum();

    public static void setVolume(float i) {
        Config.saveConfig("SOUNDVOLUME", i);
        volume = i;
    }

    public static float getVolume() {
        return volume;
    }

    public static float getMinVol() {
        return minVol;
    }

}
