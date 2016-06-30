package data;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class Saves {
    public static void loadData(TextArea textArea, String s) {
        final Path path = Paths.get(s);
        try {
            String out = "";
            for (String x : Files.readAllLines(path)) {
                out = out + x + "\n";
            }
            textArea.setText(out);
        } catch (IOException e) {
            System.out.println("Failed to load data");

        }

    }

    public static void saveData(TextArea texter, String file) {
        final Path path = Paths.get("build/resources/main/saves/" + file + ".txt");
        try {
            Files.write(path, texter.getParagraphs());
        } catch (IOException e) {
            System.out.println("Failed to save data");
        }
    }


    public static String chooseFile(Stage stage) {
        FileChooser dialog = new FileChooser();
        dialog.setTitle("Choose file");
        dialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.txt"));
        dialog.setInitialDirectory(new File("build/resources/main/saves"));
        try {
            String path = dialog.showOpenDialog(stage).getPath();
            File file = new File(path);
            return file.getPath();

        } catch (Exception e) {
            return null;
        }
    }
}