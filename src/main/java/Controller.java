import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import loader.SaveData;
import loader.TaskDecoder;
import sound.Sound;
import sound.Volume;
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

    private String s;

    @FXML
    private VBox Menu;
    @FXML
    public Slider volSlider;
    @FXML
    private Button compile;
    @FXML
    private Button configMenu;
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
    private VBox configMenueWrapper;
    @FXML
    private ComboBox<String> combo;

    public int open = 0;

    @FXML
    protected void initialize() {
        initializeComb();
        design();
        stage.setOnCloseRequest(event -> {
            if (babyStepsTimer.isRunning()) babyStepsTimer.cancel();
            stage.close();
        });

    }

    @FXML
    protected void task(ActionEvent event) {
        if (combo.getSelectionModel().selectedIndexProperty().intValue() > 0)
            initializeTDDT(combo.getSelectionModel().selectedIndexProperty().intValue() - 1);
        Main.taskid = combo.getSelectionModel().selectedIndexProperty().intValue() - 1;
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
                        s="test";
                        SaveData.saveToTextFile(Tests,s);
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
                        s="code";
                        SaveData.saveToTextFile(Code,s);
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

        Image configIconImage = new Image("file:build/resources/main/images/gear.png");
        ImageView configIcon = new ImageView(configIconImage);
        Image compileIconImage = new Image("file:build/resources/main/images/run.png");
        ImageView compileIcon = new ImageView(compileIconImage);
        Image continueIconImage = new Image("file:build/resources/main/images/arrow_right.png");
        ImageView continueIcon = new ImageView(continueIconImage);
        continueButton.setGraphic(continueIcon);
        continueButton.setDisable(true);
        compile.setGraphic(compileIcon);
        compile.setDisable(true);
        configMenu.setGraphic(configIcon);
        //configMenu.setBorder(null);
        //configMenu.setBackground(null);
        configMenu.setText("Settings");
        configMenueWrapper.setLayoutX(tab_width - 180);
        configMenu.setPrefWidth(125);
        configMenu.setPrefHeight(32);
        configMenueWrapper.setLayoutY(-45);

        try {
            volSlider.setValue(Volume.initVolume());
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
        private int i;

        public void setI(int i) {
            this.i = i;
        }

        @Override
        protected Integer call() throws Exception {
            TaskDecoder tasks = new TaskDecoder();
            Sound countdownVol = null;
            while (!isCancelled()) {
                babysteps.setFill(Color.BLACK);
                for (i = tasks.getBabystepsTime(Main.taskid); i > 0; i--) {
                    if (isCancelled()) {
                        break;
                    }

                    if (i == 10) {
                        babysteps.setFill(Color.RED);
                        countdownVol = new Sound("build/resources/main/sound/countdown.wav");
                    }
                    if (!(countdownVol==null)){
                        countdownVol.setVolume(Volume.getVolume());
                        countdownVol.start();
                    }

                    babysteps.setText("Time: " + i + "s");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {

                        System.out.println("tasks over");
                    }
                }
                if (i == 0) {
                    Sound timeoverVol = new Sound("build/resources/main/sound/over.wav");
                    timeoverVol.setVolume(Volume.getVolume());
                    timeoverVol.start();

                    babysteps.setText("Time: " + i + "s");
                    if (!compile(null)) initializeTDDT(Main.taskid);
                    else continueTab(null);
                }
            }
            return 0;
        }
    };



    protected void initializeTDDT(int index) {

        try {
            compile.setDisable(false);
            continueButton.setDisable(false);
            tabs.setDisable(false);
            tabs.getSelectionModel().selectFirst();
            TaskDecoder tasks = new TaskDecoder();
            if (tasks.isBabysteps(index)) new Thread(babyStepsTimer).start();
            else babysteps.setVisible(false);
            task_name.setText(tasks.getExcercise(index));
            task_discripton.setText(tasks.getDescription(index));
            Code.setDisable(true);
            Tests.setDisable(false);
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
            taskList.add("Select a Task ...");
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

    @FXML
    protected void configMenu(ActionEvent event) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double height = screenSize.getHeight();
        Menu.setLayoutX(-Menu.getWidth() / 2);
        Menu.setLayoutY((height - Menu.getHeight()) / 3);
        Menu.setVisible(true);
        if (open==0) {
            Menu.setVisible(true);
            new Thread(getVolume).start();
            open++;
        } else {
            Menu.setVisible(false);
            getVolume.cancel();
            open++;
            open=0;
        }
    }

    Task<Integer> getVolume = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            while(open<2){
                if(Volume.getVolume()!=(float)((Volume.getMinVol() * (1-(volSlider.getValue()/100))))){
                    Volume.setVolume((float)((Volume.getMinVol() * (1-(volSlider.getValue()/100)))));
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interrupted) {
                    System.out.println("tasks over");
                }
            }
            return null;
        }
    };
}

