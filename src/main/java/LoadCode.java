import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
public class LoadCode {




    public static TextArea loaddata(TextArea texter,String s) {

        final Path path = Paths.get(s + ".txt");
        try {
            texter.setText(String.valueOf(Files.readAllLines(path)));
            System.out.println("Erfolgreich");
        } catch (IOException e) {
            System.out.println("Fehler beim Laden");
            return null;
        }


        return texter;

    }
}