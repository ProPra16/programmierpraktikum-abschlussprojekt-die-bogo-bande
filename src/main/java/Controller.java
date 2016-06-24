import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import vk.core.api.CompilationUnit;
import vk.core.api.CompilerFactory;
import vk.core.api.JavaStringCompiler;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private Stage stage = Main.primaryStage;

    Path codePath = Paths.get("/tmp.java");
    Path testPath = Paths.get(Main.taskid + ".java");


    @FXML
    private Button continueButton;
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
    private Text babysteps;
    @FXML
    private TextArea Code;
    @FXML
    private TextArea Tests;
    @FXML
    private Pane button_pane;

    @FXML
    private ComboBox<String> combo;

    @FXML
    protected void initialize() {
        initializeComb();
        design();
    }

    @FXML
    protected void task(ActionEvent event) {
        if (combo.getSelectionModel().selectedIndexProperty().intValue() > 0)
            initializeTDDT(combo.getSelectionModel().selectedIndexProperty().intValue() - 1);
        combo.setDisable(true);
    }

    @FXML
    protected boolean compile(ActionEvent event) {
        if (tab_tests.isSelected()) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), Tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), Code.getText(), false);
                JavaStringCompiler testJavaStringCompiler = CompilerFactory.getCompiler(codeCompilationUnit, testCompilationUnit);
                testJavaStringCompiler.compileAndRunTests();
                System.out.println();
                if (testJavaStringCompiler.getCompilerResult().hasCompileErrors()) {
                    compileMessage.setFill(Color.RED);
                    continueButton.setDisable(true);
                    compileMessage.setText(testJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).toString());
                } else {
                    compileMessage.setFill(Color.GREEN);
                    if (testJavaStringCompiler.getTestResult().getNumberOfFailedTests() > 0) {
                        continueButton.setDisable(false);
                        testJavaStringCompiler.getTestResult().getTestFailures().stream().forEach(e -> System.out.println(e.getMessage()));
                        compileMessage.setText("No Errors while compiling\nYou wrote a failing Test, hit [continue]");
                        return true;
                    } else {
                        continueButton.setDisable(true);
                        compileMessage.setText("No Errors while compiling\nNo Test failed, write a failing Test!");
                    }
                }

                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (tab_code.isSelected()) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), Tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), Code.getText(), false);
                JavaStringCompiler codeJavaStringCompiler = CompilerFactory.getCompiler(codeCompilationUnit, testCompilationUnit);
                codeJavaStringCompiler.compileAndRunTests();
                if (codeJavaStringCompiler.getCompilerResult().hasCompileErrors()) {
                    compileMessage.setFill(Color.RED);
                    continueButton.setDisable(true);
                    compileMessage.setText(codeJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(codeCompilationUnit).toString() + codeJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).toString());
                } else {
                    if (codeJavaStringCompiler.getTestResult().getNumberOfFailedTests() > 0) {
                        codeJavaStringCompiler.compileAndRunTests();
                        continueButton.setDisable(true);
                        compileMessage.setText("No Errors while compiling\n" + codeJavaStringCompiler.getTestResult().getNumberOfFailedTests() + " Tests failed!");
                        return false;
                    } else {
                        continueButton.setDisable(false);
                        codeJavaStringCompiler.compileAndRunTests();
                        compileMessage.setText("No Errors while compiling\n" + codeJavaStringCompiler.getTestResult().getNumberOfSuccessfulTests() + " Tests succeded");
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @FXML
    protected void continueTab(ActionEvent event) {
        if (compile(null) && tab_tests.isSelected()) {
            tabs.getSelectionModel().select(tab_code);
            Tests.setDisable(true);
            Code.setDisable(false);
            continueButton.setDisable(true);
            compileMessage.setText("Write some Code!");
        } else if (compile(null) && tab_code.isSelected()) {
            tabs.getSelectionModel().select(tab_tests);
            Tests.setDisable(false);
            Code.setDisable(true);
            continueButton.setDisable(true);
        }
    }

    protected void design() {
        tabs.setDisable(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        double tab_width = width / 2;
        double tab_height = height - 50;

        try {
            tabs.setMaxWidth(tab_width);
            tabs.setMinWidth(tab_width);
            Tests.setMinWidth(tab_width);
            Tests.setMaxWidth(tab_width);
            Code.setMinWidth(tab_width);
            Code.setMaxWidth(tab_width);
            button_pane.setMaxWidth(tab_width);
            button_pane.setMinWidth(tab_width);
            tabs.setMaxHeight(tab_height);
            tabs.setMinHeight(tab_height);
            Tests.setMaxHeight(tab_height);
            Tests.setMinHeight(tab_height);
            Code.setMaxHeight(tab_height);
            Code.setMinHeight(tab_height);
            Code.setText("");
            Tests.setText("");
            combo.setLayoutX(tab_width - 160);
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }

    Task<Integer> babyStepsTimer = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            TaskDecoder tasks = new TaskDecoder();
            int i;
            while(!isCancelled()) {
                for (i = tasks.getBabystepsTime(Main.taskid); i > 0; i--) {
                    if (isCancelled()) {
                        break;
                    }
                    babysteps.setText("time: " + i + "s");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {
                        System.out.println("tasks over");
                    }
                }
                if (i == 0) {//reset;
                    if(!compile(null))initializeTDDT(Main.taskid);
                    else continueTab(null);
                }
            }
            return null;
        }
    };

    protected void initializeTDDT(int index) {

        try {
            //test
            tabs.setDisable(false);
            TaskDecoder tasks = new TaskDecoder();
            if (tasks.isBabysteps(index)) new Thread(babyStepsTimer).start();
            else babysteps.setVisible(false);
            task_name.setText(tasks.getExcercise(index));
            task_discripton.setText(tasks.getDescription(index));
            Code.setDisable(true);
            continueButton.setDisable(true);

            compileMessage.setFill(Color.BLACK);
            compileMessage.setText("Write a failing Test");

            if (Tests != null) {
                Tests.setText(tasks.getTest(index));
            }

            if (Code != null) {
                Code.setText(tasks.getClass(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initializeComb() {
        try {
            TaskDecoder tasks = new TaskDecoder();
            int i = tasks.getTasks().getLength();
            List<String> taskList = new ArrayList<>();
            taskList.add("Select a Task");
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

