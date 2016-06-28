package loader;

import javafx.scene.control.TextArea;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class SaveData {


    public static void saveToTextFile(TextArea texter,String s) {

        if (texter.getParagraphs() == null){
            System.out.println("Keine Eingabe");
        }
        final Path path;
        if(s.equals("code")) {
            path = Paths.get("build/resources/main/code.txt");
        }else {
            path = Paths.get("build/resources/main/test.txt");
        }

        try {
            Files.write(path,texter.getParagraphs());
            System.out.println("Erfolgreich");

        } catch (IOException e) {
            System.out.println("Fehler beim schreiben");
        }

    }
}
