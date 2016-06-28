package sound;

import javax.sound.sampled.FloatControl;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Volume {
    private static float volume = loadFromConfig("build/resources/main/config.cfg");
    private static float minVol = new Sound("build/resources/main/sound/test.wav").getVolumeControl().getMinimum();

    public static void setVolume(float i){
        volume = i;
        saveToConfig("build/resources/main/config.cfg");
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

    private static float loadFromConfig(String path){
        Path configPath = Paths.get(path);
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains("SOUNDVOLUME")).map(s -> s).findFirst();
            if(!optionalObjectSoundvolume.isPresent())return -10;
            String objectSoundvolume = optionalObjectSoundvolume.get();
            String[] valueVolume = objectSoundvolume.split("'");
            return Float.valueOf(valueVolume[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void saveToConfig(String path){
        Path configPath = Paths.get(path);
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains("SOUNDVOLUME")).map(s -> s).findFirst();
            if(optionalObjectSoundvolume.isPresent()) {
                int indexSoundVol = config.indexOf(optionalObjectSoundvolume.get());
                config.remove(indexSoundVol);
                config.add(indexSoundVol, "SOUNDVOLUME='" + volume + "';");
            }
            else config.add("SOUNDVOLUME='" + volume + "';");
            Files.write(configPath,config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
