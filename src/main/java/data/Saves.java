package data;

import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Saves {

    public static void loadData(TextArea textArea, String s) {
        final Path path = Paths.get(s);
        try {
            String out = "";
            if (!Files.exists(path)) {
                Saves.saveDatahelper(textArea, s); //erstellt eine textdatei falls nicht vorhanden
                System.out.print("erstellt neue Datei");
            }                                      //in diesem Fall immer einer leere
            for (String x : Files.readAllLines(path)) {
                out = out + x + "\n";
            }
            textArea.setText(out);
        } catch (IOException e) {
            System.out.println("Failed to load data");

        }

    }

    public static void saveDatahelper(TextArea texter, String file) {
        final Path path = Paths.get(file);
        try {
            Files.write(path, texter.getParagraphs());
        } catch (IOException e) {
            System.out.println("Failed to save data");
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

    public static void saveData2(TextArea texter, String file) {
        final Path path = Paths.get("build/resources/main/saves/" + file + ".txt");
        try {
            Files.write(path, texter.getParagraphs(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Failed to save data");
        }
    }

    public static void saveErrors(String s,int i){
        if(s.equals("No Errors")) {
        return;
        }
        final Path path = Paths.get("build/resources/main/saves/allerrors.txt");
        final Path path2 = Paths.get("build/resources/main/saves/temperrors.txt");
        try {
            if(i==0){
                if(s.equals("all")) Files.write(path, "All Session Errors:updated if continue is hitted\n".getBytes());
                if(s.equals("temp")) Files.write(path2, "You can watch all of your errors in this session in following file:allerrors.txt\n".getBytes());
            }else {
                Files.write(path, s.getBytes(), StandardOpenOption.APPEND);
                Files.write(path2, s.getBytes(), StandardOpenOption.APPEND);
            }
        }catch (IOException e) {
            System.out.println("Failed to save Errors");
        }
    }

    public static String loadErrors() {
        final Path path2 = Paths.get("build/resources/main/saves/temperrors.txt");
        String out;
        out = null;
        try {
            out = "";
            for (String x : Files.readAllLines(path2)) {
                out = out + x + "\n";
            }
        } catch (IOException e) {
            System.out.println("Failed to load Errors");

        }
        return out;

    }


    public static String chooseFile(Stage stage,int ichooser) {
        FileChooser dialog = new FileChooser();
        if(ichooser==0) {
            dialog.setTitle("Choose Test");
        }else{
            dialog.setTitle("Choose Code");
        }
        dialog.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Save Files", "*.txt"));
        dialog.setInitialDirectory(new File("build/resources/main/saves"));
        try {
            String path = dialog.showOpenDialog(stage).getPath();
            File file = new File(path);
            return file.getPath();

        } catch (Exception e) {
            return "#ERROR";
        }
    }
}