import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main extends Application {

    static Stage primaryStage;
    static int taskid;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            if (Files.notExists(Paths.get("data"))) Files.createDirectories(Paths.get("data"));
            if (Files.notExists(Paths.get("data/config.cfg"))) Files.createFile(Paths.get("data/config.cfg"));
            if (Files.notExists(Paths.get("saves"))) Files.createDirectories(Paths.get("saves"));
            if (Files.notExists(Paths.get("saves/code.txt"))) Files.createFile(Paths.get("saves/code.txt"));
            if (Files.notExists(Paths.get("saves/test.txt"))) Files.createFile(Paths.get("saves/test.txt"));
            if (Files.notExists(Paths.get("saves/codechange.txt"))) Files.createFile(Paths.get("saves/codechange.txt"));
            if (Files.notExists(Paths.get("saves/testchange.txt"))) Files.createFile(Paths.get("saves/testchange.txt"));
            System.out.println("Created Folders!");
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Main.primaryStage = primaryStage;
        Files.createDirectories(Paths.get("../.tmp"));
        Parent root = FXMLLoader.load(this.getClass().getResource("TDDT.fxml"));
        root.getStylesheets().add("TDDT.css");
        root.applyCss();
        primaryStage.setTitle("TDDP - Select Task");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
}
