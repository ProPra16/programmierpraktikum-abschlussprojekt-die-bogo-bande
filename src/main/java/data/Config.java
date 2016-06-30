package data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * Created by root on 29.06.16.
 */
public class Config {
    public static float loadFloatFromConfig(String path, String h){
        Path configPath = Paths.get(path);
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if(!optionalObjectSoundvolume.isPresent())return -10;
            String objectSoundvolume = optionalObjectSoundvolume.get();
            String[] valueVolume = objectSoundvolume.split("'");
            return Float.valueOf(valueVolume[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean loadBoolFromConfig(String path, String h){
        Path configPath = Paths.get(path);
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if(!optionalObjectSoundvolume.isPresent())return false;
            String objectSoundvolume = optionalObjectSoundvolume.get();
            String[] valueVolume = objectSoundvolume.split("'");
            return Boolean.valueOf(valueVolume[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void saveConfig(String path,String h,float f){
        Path configPath = Paths.get(path);
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if(optionalObjectSoundvolume.isPresent()) {
                int indexSoundVol = config.indexOf(optionalObjectSoundvolume.get());
                config.remove(indexSoundVol);
                config.add(indexSoundVol, h+"='" + f + "';");
            }
            else config.add(h+"='" + f + "';");
            Files.write(configPath,config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(String path,String h,boolean b){
        Path configPath = Paths.get(path);
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if(optionalObjectSoundvolume.isPresent()) {
                int indexSoundVol = config.indexOf(optionalObjectSoundvolume.get());
                config.remove(indexSoundVol);
                config.add(indexSoundVol, h+"='" + b + "';");
            }
            else config.add(h+"='" + b + "';");
            Files.write(configPath,config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
