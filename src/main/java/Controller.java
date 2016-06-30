import data.Config;
import data.Saves;
import data.TaskDecoder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
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
import sound.Sound;
import sound.Volume;
import vk.core.api.CompilationUnit;
import vk.core.api.CompilerFactory;
import vk.core.api.JavaStringCompiler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    private Stage stage = Main.primaryStage;
    private String s;
    private int time;

    private enum Status {TEST, CODE, REFACTOR}

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
    private Text status;
    @FXML
    private TabPane tabs;
    @FXML
    private Tab tab_tests;
    @FXML
    private Tab tab_code;
    @FXML
    private Text babysteps;
    @FXML
    private TextArea code;
    @FXML
    private TextArea tests;
    @FXML
    private Pane button_pane;
    @FXML
    private VBox configMenueWrapper;
    @FXML
    private ComboBox<String> combo;
    @FXML
    private CheckBox check_the_baby;

    @FXML
    private LineChart<Integer, Double> graph;

    private int open = 0;
    private LineChart.Series<Integer, Double> series1 = new LineChart.Series<>();

    @FXML
    protected void initialize() {
        initializeComb();
        design();
        stage.setOnCloseRequest(event -> {
            if (babyStepsTimer.isRunning()) babyStepsTimer.cancel();
            stage.close();
        });
        check_the_baby.setSelected(Config.loadBoolFromConfig("ENABLE_BABYSTEPS"));
        status.setText("Select a Task");

        ObservableList<XYChart.Series<Integer, Double>> lineChartData = FXCollections.observableArrayList();

        series1.setName("Time");
        series1.getData().add(new XYChart.Data<>(0, 1.0));
        series1.getData().add(new XYChart.Data<>(1, 1.4));
        series1.getData().add(new XYChart.Data<>(2, 1.9));
        series1.getData().add(new XYChart.Data<>(3, 2.3));
        series1.getData().add(new XYChart.Data<>(4, 0.5));
        lineChartData.add(series1);
        graph.setData(lineChartData);
        graph.createSymbolsProperty();
    }

    @FXML
    protected void task(ActionEvent event) {
        if (combo.getSelectionModel().selectedIndexProperty().intValue() > 0) {
            Main.taskid = combo.getSelectionModel().selectedIndexProperty().intValue() - 1;
            initializeTDDT(Main.taskid);
            combo.setDisable(true);
            status.setFill(Color.RED);
            status.setText(Status.TEST.toString());
        }
    }

    @FXML
    protected boolean compile(ActionEvent event) {
        if (status.getText().equals(Status.TEST.toString())) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), code.getText(), false);
                JavaStringCompiler testJavaStringCompiler = CompilerFactory.getCompiler(codeCompilationUnit, testCompilationUnit);
                testJavaStringCompiler.compileAndRunTests();
                if (testJavaStringCompiler.getCompilerResult().hasCompileErrors()) {
                    compileMessage.setFill(Color.RED);
                    continueButton.setDisable(true);
                    compileMessage.setText(testJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).toString());
                } else {
                    compileMessage.setFill(Color.GREEN);
                    if (testJavaStringCompiler.getTestResult().getNumberOfFailedTests() > 0) {
                        continueButton.setDisable(false);
                        testJavaStringCompiler.getTestResult().getTestFailures().stream().forEach(e -> System.out.println(/*e.getMessage()*/));
                        compileMessage.setText("No Errors while compiling\nYou wrote a failing Test, hit [continue]");
                        s = "test";
                        Saves.saveData(tests, s);
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
        } else if (status.getText().equals(Status.CODE.toString())) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), code.getText(), false);
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
                        compileMessage.setText("No Errors while compiling\n" + codeJavaStringCompiler.getTestResult().getNumberOfFailedTests() + " tests failed!");
                        return false;
                    } else {
                        continueButton.setDisable(false);
                        codeJavaStringCompiler.compileAndRunTests();
                        compileMessage.setText("No Errors while compiling\n" + codeJavaStringCompiler.getTestResult().getNumberOfSuccessfulTests() + " tests succeded");
                        s = "code";
                        Saves.saveData(code, s);
                        return true;
                    }
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (status.getText().equals(Status.REFACTOR.toString())) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), code.getText(), false);
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
                        compileMessage.setText("No Errors while compiling\n" + codeJavaStringCompiler.getTestResult().getNumberOfFailedTests() + " tests failed!");
                        return false;
                    } else {
                        continueButton.setDisable(false);
                        codeJavaStringCompiler.compileAndRunTests();
                        compileMessage.setText("No Errors while compiling\n" + codeJavaStringCompiler.getTestResult().getNumberOfSuccessfulTests() + " tests succeded");
                        s = "code";
                        Saves.saveData(code, s);
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
        if (compile(null) && status.getText().equals(Status.TEST.toString())) {
            status.setText(Status.CODE.toString());
            status.setFill(Color.GREEN);
            tabs.getSelectionModel().select(tab_code);
            tests.setDisable(true);
            code.setDisable(false);
            continueButton.setDisable(true);
            compileMessage.setText("Write some code!");

            try {
                time = new TaskDecoder().getBabystepsTime(Main.taskid);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (compile(null) && status.getText().equals(Status.CODE.toString())) {
            status.setText(Status.REFACTOR.toString());
            status.setFill(Color.BLACK);
            tabs.getSelectionModel().select((int) (Math.random() * 2));
            tests.setDisable(false);
            code.setDisable(false);
            continueButton.setDisable(true);

            babyStepsTimer.cancel();

        } else if (compile(null) && status.getText().equals(Status.REFACTOR.toString())) {
            status.setText(Status.TEST.toString());
            status.setFill(Color.RED);
            tabs.getSelectionModel().select(tab_tests);
            tests.setDisable(false);
            code.setDisable(true);
            continueButton.setDisable(true);

            new Thread(babyStepsTimer).start();

        }
    }

    private void design() {
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
        configMenu.setText("Settings");
        configMenueWrapper.setLayoutX(tab_width - 180);
        configMenu.setPrefWidth(125);
        configMenu.setPrefHeight(32);
        configMenueWrapper.setLayoutY(-45);

        try {
            volSlider.setValue(Volume.initVolume());
            tabs.setMaxWidth(tab_width);
            tabs.setMinWidth(tab_width);
            tests.setMinWidth(tab_width);
            tests.setMaxWidth(tab_width);
            code.setMinWidth(tab_width);
            code.setMaxWidth(tab_width);
            button_pane.setMaxWidth(tab_width);
            button_pane.setMinWidth(tab_width);
            tabs.setMaxHeight(tab_height);
            tabs.setMinHeight(tab_height);
            tests.setMaxHeight(tab_height);
            tests.setMinHeight(tab_height);
            code.setMaxHeight(tab_height);
            code.setMinHeight(tab_height);
            code.setText("");
            tests.setText("");
            combo.setLayoutX(tab_width - 160);
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }

    private Task<Integer> babyStepsTimer = new Task<Integer>() {

        @Override
        protected Integer call() throws Exception {
            TaskDecoder tasks = new TaskDecoder();
            Sound countdownVol = null;
            while (!isCancelled()) {
                babysteps.setFill(Color.BLACK);
                for (time = tasks.getBabystepsTime(Main.taskid); time > 0; time--) {
                    if (isCancelled()) {
                        break;
                    }

                    if (time == 20) {
                        babysteps.setFill(Color.RED);
                        countdownVol = new Sound("build/resources/main/sound/countdown_boom.wav");
                    }
                    if (!(countdownVol == null)) {
                        countdownVol.setVolume(Volume.getVolume());
                        countdownVol.start();
                    }

                    babysteps.setText("Time: " + time + "s");

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {

                        System.out.println("tasks over");
                    }
                }
                if (time == 0) {
                    countdownVol.setVolume(Volume.getMinVol());
                    babysteps.setText("Time: " + time + "s");
                    if (!compile(null)) initializeTDDT(Main.taskid);
                    else continueTab(null);
                }
            }
            return 0;
        }
    };


    private void initializeTDDT(int index) {

        try {
            compile.setDisable(false);
            continueButton.setDisable(false);
            tabs.setDisable(false);
            tabs.getSelectionModel().selectFirst();
            TaskDecoder tasks = new TaskDecoder();
            if (tasks.isBabysteps(index) && check_the_baby.isSelected()) new Thread(babyStepsTimer).start();
            else babysteps.setVisible(false);
            code.setDisable(true);
            tests.setDisable(false);
            continueButton.setDisable(true);

            compileMessage.setFill(Color.BLACK);
            compileMessage.setText("Write a failing Test");
            if (Main.taskid == 0) {
                s = Saves.chooseFile(stage);
                Saves.loadData(tests, s);
            } else {
                tests.setText(tasks.getTest(index));
            }
            if (Main.taskid == 0) {
                Saves.loadData(code, s);
            } else {
                code.setText(tasks.getClass(index));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeComb() {
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
        if (open == 0) {
            Menu.setVisible(true);
            new Thread(getVolume).start();
            open++;
        } else {
            Menu.setVisible(false);
            Config.saveConfig("ENABLE_BABYSTEPS", check_the_baby.isSelected());
            getVolume.cancel();
            open++;
            open = 0;
        }
    }

    private Task<Integer> getVolume = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            while (open < 2) {
                if (Volume.getVolume() != (float) ((Volume.getMinVol() * (1 - (volSlider.getValue() / 100))))) {
                    Volume.setVolume((float) ((Volume.getMinVol() * (1 - (volSlider.getValue() / 100)))));
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

