package main;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeFactory.*;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.GregorianCalendar;
import java.util.List;

public class Controller {

    private Stage stage = Main.primaryStage;
    private String task = Main.task;

    Path codePath = Paths.get("/" + task + ".java");
    Path testPath = Paths.get("/" + task + "Test.java");

    @FXML
    private Tab tab_tests;

    @FXML
    private Tab tab_code;

    @FXML
    private Text task_name;

    @FXML
    private TabPane tabs;

    @FXML
    private Text task_discripton;

    @FXML
    private TextArea Code;

    @FXML
    private TextArea Tests;

    @FXML
    protected void initialize() {
        task_name.setText("Task");
        task_discripton.setText("Taskdescripton");
        tab_code.setDisable(true);

        if (Tests != null) {
            try {
                List<String> input = Files.readAllLines(testPath);
                String load = "";
                for (String s : input) {
                    load = load + s + "\n";
                }
                Tests.setText(load);

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
                Code.setText(load);

            } catch (IOException e) {
            }
        }
    }

    @FXML
    protected boolean compileCode(ActionEvent event) {

        try {
            Files.write(codePath, Code.getText().getBytes());
            tab_tests.setDisable(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        tab_tests.setDisable(true);
        return false;
    }

    @FXML
    protected boolean compile(ActionEvent event) {

        if(tab_tests.isSelected()) {
            try {
                Files.write(testPath, Tests.getText().getBytes());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if(tab_code.isSelected()){
            try {
                Files.write(codePath, Code.getText().getBytes());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @FXML
    protected void continueToTest(ActionEvent event) {
        if (compileCode(null)) {
            tabs.getSelectionModel().selectFirst();
        }
    }

    @FXML
    protected void continueTab(ActionEvent event) {
        if (compile(null)&&tab_tests.isSelected()) {
            tabs.getSelectionModel().select(tab_code);
            tab_code.setDisable(false);
            tab_tests.setDisable(true);
        }else if (compile(null)&&tab_code.isSelected()) {
            tabs.getSelectionModel().select(tab_tests);
            tab_tests.setDisable(false);
            tab_code.setDisable(true);
        }
    }

    public void test(){}


    }
}
