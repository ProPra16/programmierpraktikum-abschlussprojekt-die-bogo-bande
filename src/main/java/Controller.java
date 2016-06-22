import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vk.core.api.CompilationUnit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private Stage stage = Main.primaryStage;

    Path codePath = Paths.get("/tmp.java");
    Path testPath = Paths.get("/tmpTest.java");


    @FXML
    private Text compileMessage;
    @FXML
    private TabPane tabs;
    @FXML
    private Tab tab_tests;
    @FXML
    private Tab tab_code;
    @FXML
    private Text task_name;
    @FXML
    private Text task_discripton;
    @FXML
    private TextArea Code;
    @FXML
    private TextArea Tests;

    @FXML
    private ComboBox<String> combo;

    @FXML
    protected void initialize() {
        if(combo!=null){
            initializeComb();
        }else{
            initializeTDDT(Main.taskid);
        }

    }

    @FXML
    protected void changeScene(ActionEvent event){
        Parent root = null;
        Main.taskid= combo.getSelectionModel().selectedIndexProperty().intValue();
        try {
            root = FXMLLoader.load(getClass().getResource("EditorDesign.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        CompilationUnit test = new CompilationUnit("peter parker", "läderlappen",true);
        stage.setTitle("TDDP - Write a Test");
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.show();
    }

    @FXML
    protected boolean compile(ActionEvent event) {

        if (tab_tests.isSelected()) {
            try {
                Files.write(testPath, Tests.getText().getBytes());
                compileMessage.setText("ERRORXY");

                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (tab_code.isSelected()) {
            try {
                Files.write(codePath, Code.getText().getBytes());
                compileMessage.setText("ERROR");
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @FXML
    protected void continueTab(ActionEvent event) {
        if (compile(null) && tab_tests.isSelected()) {
            tabs.getSelectionModel().select(tab_code);
            tab_code.setDisable(false);
            tab_tests.setDisable(true);
        } else if (compile(null) && tab_code.isSelected()) {
            tabs.getSelectionModel().select(tab_tests);
            tab_tests.setDisable(false);
            tab_code.setDisable(true);
        }
    }

    protected void initializeTDDT(int index) {

        try {
            TaskDecoder tasks = new TaskDecoder();
            task_name.setText(tasks.getExcercise(index));
            task_discripton.setText(tasks.getDescription(index));
            tab_code.setDisable(true);
            compileMessage.setFill(Color.RED);

            if (Tests != null) {
                try {
                    List<String> input = Files.readAllLines(testPath);
                    String load = "";
                    for (String s : input) {
                        load = load + s + "\n";
                    }
                    Tests.setText(tasks.getTest(index));

                } catch (IOException e) {
                }
            }

            if (Code != null) {
                try {
                    List<String> input = Files.readAllLines(codePath);
                    String load = "";
                    for (String s : input) {
                        load = load + s + "\n";
                    }
                    Code.setText(tasks.getClass(index));

                } catch (IOException e) {
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initializeComb(){
        try {
            TaskDecoder tasks = new TaskDecoder();

            int i = tasks.getTasks().getLength();
            List<String> taskList = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                taskList.add(tasks.getTasks().item(j).getAttributes().getNamedItem("name").getTextContent());
            }
            combo.getItems().clear();
            combo.getItems().addAll(taskList);
            combo.getSelectionModel().selectFirst();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

