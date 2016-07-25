package data;

import org.apache.commons.io.IOUtils;
import sound.Volume;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Config {
    private static String path = "data/config.cfg";
    private static Path configPath = Paths.get(path);

    public static float loadFloatFromConfig(String h) {
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if (!optionalObjectSoundvolume.isPresent()) return -10;
            String objectSoundvolume = optionalObjectSoundvolume.get();
            String[] valueVolume = objectSoundvolume.split("'");
            return Float.valueOf(valueVolume[1]);
        } catch (Exception e) {
            return Volume.getMinVol();
        }
    }

    public static boolean loadBoolFromConfig(String h) {
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if (!optionalObjectSoundvolume.isPresent()) return false;
            String objectSoundvolume = optionalObjectSoundvolume.get();
            String[] valueVolume = objectSoundvolume.split("'");
            return Boolean.valueOf(valueVolume[1]);
        } catch (Exception e) {
            return false;
        }
    }

    public static void saveConfig(String h, float f) {
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if (optionalObjectSoundvolume.isPresent()) {
                int indexSoundVol = config.indexOf(optionalObjectSoundvolume.get());
                config.remove(indexSoundVol);
                config.add(indexSoundVol, h + "='" + f + "';");
            } else config.add(h + "='" + f + "';");
            Files.write(configPath, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(String h, boolean b) {
        try {
            List<String> config = Files.readAllLines(configPath);
            Optional<String> optionalObjectSoundvolume = config.stream().filter(s -> s.contains(h)).map(s -> s).findFirst();
            if (optionalObjectSoundvolume.isPresent()) {
                int indexSoundVol = config.indexOf(optionalObjectSoundvolume.get());
                config.remove(indexSoundVol);
                config.add(indexSoundVol, h + "='" + b + "';");
            } else config.add(h + "='" + b + "';");
            Files.write(configPath, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
