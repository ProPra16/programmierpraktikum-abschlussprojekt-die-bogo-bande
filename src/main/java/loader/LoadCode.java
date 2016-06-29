package loader;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class LoadCode {
    public static void loaddata(TextArea texter,String s) {
        final Path path = Paths.get(s);
        try {
            String out ="";
            for (String x:Files.readAllLines(path)) {
                out = out + x + "\n";
            }
            texter.setText(out);
            System.out.println("Erfolgreich");
        } catch (IOException e) {
            System.out.println("Fehler beim Laden");

        }

    }

    public static String chooseFile(Stage stage){
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Choose file");
        dialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.txt"));
        dialog.setInitialDirectory(new File("build/resources/main/saves"));
        String path = dialog.showOpenDialog(stage).getPath();
        if(path!=null){File file = new File(path);
        return file.getPath();}
        //System.out.println(file.getAbsolutePath()!=null?file.getAbsolutePath():"Error");
        return null;
    }
}