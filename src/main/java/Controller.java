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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sound.Sound;
import sound.Volume;
import vk.core.api.CompilationUnit;
import vk.core.api.CompilerFactory;
import vk.core.api.JavaStringCompiler;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private Stage stage = Main.primaryStage;
    private int methodeerror=0;
    private String s;
    private int time;
    private int cycles = 0;
    private int errors = 0;
    private int pause =0;            //"pausiert" gegebenenfalls den Babysteptimer
    private enum Status {TEST, CODE, REFACTOR}

    @FXML
    public Slider volSlider;
    @FXML
    private Button compileButton;
    @FXML
    private Button returnButton;
    @FXML
    private Button continueButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button statsButton;
    @FXML
    private Text compileMessage;
    @FXML
    private Text statusMessage;
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
    private ComboBox<String> combo;
    @FXML
    private CheckBox check_the_baby;
    @FXML
    private CheckBox check_stalker;
    @FXML
    private HBox menu;
    @FXML
    private HBox graphShow;

    @FXML
    private LineChart<Integer, Integer> graph;
    private LineChart.Series<Integer, Integer> timeData = new LineChart.Series<>();
    private LineChart.Series<Integer, Integer> errorData = new LineChart.Series<>();

    @FXML
    protected void initialize() {
        initializeTaskSelection();
        stage.setOnCloseRequest(event -> {
            if (babyStepsTimer.isRunning()) babyStepsTimer.cancel();
            stage.close();
        });
        check_the_baby.setSelected(Config.loadBoolFromConfig("ENABLE_BABYSTEPS"));
        check_stalker.setSelected(Config.loadBoolFromConfig("TRACKING"));
        statsButton.setVisible(check_stalker.isSelected());
        saveButton.setVisible(false);
        returnButton.setDisable(true);
        volSlider.setValue(100+(((Config.loadFloatFromConfig("SOUNDVOLUME")/80)*100)));

        statusMessage.setText("Select a Task");

        ObservableList<XYChart.Series<Integer, Integer>> lineChartData = FXCollections.observableArrayList();

        timeData.setName("Time");
        lineChartData.add(timeData);
        errorData.setName("Errors");
        lineChartData.add(errorData);
        graph.setData(lineChartData);
        graph.createSymbolsProperty();
    }

    @FXML
    protected void task(ActionEvent event) {
        if (combo.getSelectionModel().selectedIndexProperty().intValue() > 0) {
            Main.taskid = combo.getSelectionModel().selectedIndexProperty().intValue() - 1;
            initializeTask(Main.taskid);
            combo.setDisable(true);
            new Thread(timer).start();
            statusMessage.setFill(Color.RED);
            statusMessage.setText(Status.TEST.toString());
        }
    }

    @FXML
    protected boolean compile(ActionEvent event) {
        if (statusMessage.getText().equals(Status.TEST.toString())) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), code.getText(), false);
                JavaStringCompiler testJavaStringCompiler = CompilerFactory.getCompiler(codeCompilationUnit, testCompilationUnit);
                testJavaStringCompiler.compileAndRunTests();
                errors += testJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).size();
                if (testJavaStringCompiler.getCompilerResult().hasCompileErrors()) {
                    if (testJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).size()==1){
                        if(testJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).toString().contains(":error:cannot find symbol\n  symbol:   method")) { // symbol:   method
                            compileMessage.setText("Error:Methode not found...You may continue if you want to write a new Methode");
                            s = "test";
                            Saves.saveData(tests, s);
                            continueButton.setDisable(false);
                            return true;
                        }
                    }
                    compileMessage.setFill(Color.RED);
                    continueButton.setDisable(true);
                    compileMessage.setText(testJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).toString());
                } else {
                    compileMessage.setFill(Color.GREEN);
                    if (testJavaStringCompiler.getTestResult().getNumberOfFailedTests() > 0) {
                        if (testJavaStringCompiler.getTestResult().getNumberOfFailedTests() == 1) {
                            continueButton.setDisable(false);
                            testJavaStringCompiler.getTestResult().getTestFailures().stream().forEach(e -> System.out.println(/*e.getMessage()*/));
                            compileMessage.setText("No Errors while compiling\nYou wrote a failing Test, hit [continue]");
                            s = "test";
                            Saves.saveData(tests, s);
                            methodeerror=1;
                            return true;    //Momentan: nur dann wenn 1Test fehlschlägt darf der Benutzer weiterarbeiten
                        }
                        else{
                            compileMessage.setText("You wrote to many failed Tests. You are only allowed to write one failing Test!");
                        }
                    } else {
                        continueButton.setDisable(true);
                        compileMessage.setText("No Errors while compiling\nNo Test failed, write a failing Test!");
                        //savetab(null);
                    }
                }

                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (statusMessage.getText().equals(Status.CODE.toString())) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), code.getText(), false);
                JavaStringCompiler codeJavaStringCompiler = CompilerFactory.getCompiler(codeCompilationUnit, testCompilationUnit);
                codeJavaStringCompiler.compileAndRunTests();
                errors += codeJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(codeCompilationUnit).size();
                if (codeJavaStringCompiler.getCompilerResult().hasCompileErrors()) {
                    compileMessage.setFill(Color.RED);
                    continueButton.setDisable(true);
                    compileMessage.setText(codeJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(codeCompilationUnit).toString() + codeJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(testCompilationUnit).toString());
                    return false;
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
                        returnButton.setDisable(true);
                        s = "code";
                        Saves.saveData(code, s);
                        return true;        //wenn code compiliert und alle Tests funktionieren darf der Benutzer weitermachen
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (statusMessage.getText().equals(Status.REFACTOR.toString())) {
            try {
                TaskDecoder tasks = new TaskDecoder();
                CompilationUnit testCompilationUnit = new CompilationUnit(tasks.getTestName(Main.taskid), tests.getText(), true);
                CompilationUnit codeCompilationUnit = new CompilationUnit(tasks.getClassName(Main.taskid), code.getText(), false);
                JavaStringCompiler codeJavaStringCompiler = CompilerFactory.getCompiler(codeCompilationUnit, testCompilationUnit);
                codeJavaStringCompiler.compileAndRunTests();
                errors += codeJavaStringCompiler.getCompilerResult().getCompilerErrorsForCompilationUnit(codeCompilationUnit).size();
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
                        s= "test";
                        Saves.saveData(tests, s);
                        return true;        //nur dann wenn alles compiliert und alle tests durchlaufen, kann der Benutzer
                    }                       //weiter machen und alles wird gespeichert
                }
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    @FXML
    protected void savetab(ActionEvent event){

        String s ="lastcode";
        String t ="lasttest";
        Saves.saveData(code,s);
        Saves.saveData(tests,t);
        compileMessage.setText("Data saved to lastcode.txt and lasttest.txt");
    }
    @FXML
    protected void returnTab(ActionEvent event){
        statusMessage.setFill(Color.RED);
        statusMessage.setText(Status.TEST.toString());
        code.setDisable(true);
        tests.setDisable(false);
        returnButton.setDisable(true);
        tabs.getSelectionModel().select(tab_tests);
        compileMessage.setText("You returned to Test.You code has been resetted");
        Saves.loadData(code,"build/resources/main/saves/code.txt");
    }

    @FXML
    protected void continueTab(ActionEvent event) {
        if ((compile(null) && statusMessage.getText().equals(Status.TEST.toString())) || methodeerror==1) {
            methodeerror=0;
            returnButton.setDisable(false);
            cycles++;
            statusMessage.setText(Status.CODE.toString());
            statusMessage.setFill(Color.GREEN);
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

        } else if (compile(null) && statusMessage.getText().equals(Status.CODE.toString())) {
            returnButton.setDisable(true);
            cycles++;
            statusMessage.setText(Status.REFACTOR.toString());
            statusMessage.setFill(Color.BLACK);
            tabs.getSelectionModel().select((int) (Math.random() * 2));
            tests.setDisable(false);
            code.setDisable(false);
            continueButton.setDisable(true);
            pause=1;


        } else if (compile(null) && statusMessage.getText().equals(Status.REFACTOR.toString())) {
            returnButton.setDisable(true);
            cycles++;
            statusMessage.setText(Status.TEST.toString());
            statusMessage.setFill(Color.RED);
            tabs.getSelectionModel().select(tab_tests);
            tests.setDisable(false);
            code.setDisable(true);
            continueButton.setDisable(true);
            pause=0;

        }
    }

    @FXML
    protected void settings(ActionEvent event) {
        if (menu.isVisible()) {
            menu.setVisible(false);
            Config.saveConfig("ENABLE_BABYSTEPS", check_the_baby.isSelected());
            Config.saveConfig("TRACKING",check_stalker.isSelected());
            statsButton.setVisible(check_stalker.isSelected());
            //getVolume.cancel(); warum soll er canceln?
        } else {
            menu.setVisible(true);
            new Thread(getVolume).start();
        }
    }

    @FXML
    protected void stats(ActionEvent event) {
        if (graphShow.isVisible()) graphShow.setVisible(false);
        else graphShow.setVisible(true);
    }

    private void initializeTaskSelection() {
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

    private void initializeTask(int index) {

        try {
            compileButton.setDisable(false);
            continueButton.setDisable(false);
            tabs.setDisable(false);
            tabs.getSelectionModel().selectFirst();
            TaskDecoder tasks = new TaskDecoder();
            if (tasks.isBabysteps(index) && check_the_baby.isSelected()) new Thread(babyStepsTimer).start();
            else babysteps.setVisible(false);
            code.setDisable(true);
            tests.setDisable(false);
            continueButton.setDisable(true);
            saveButton.setVisible(true);

            compileMessage.setFill(Color.BLACK);
            compileMessage.setText("Write a failing Test");
            if (Main.taskid == 0) {
                s = Saves.chooseFile(stage,0);
                Saves.loadData(tests, s);
            } else {
                tests.setText(tasks.getTest(index));
            }
            if (Main.taskid == 0) {
                s=Saves.chooseFile(stage,1);
                Saves.loadData(code, s);
            } else {
                code.setText(tasks.getClass(index));
            }
            String t="code";
            Saves.saveData(code,"code");
            Saves.saveData(tests,"test");
            t="test";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Tasks
    private Task<Integer> babyStepsTimer = new Task<Integer>() {

        @Override
        protected Integer call() throws Exception {
            TaskDecoder tasks = new TaskDecoder();
            Sound countdownVol = null;
            while (!isCancelled()) {
                for (time = tasks.getBabystepsTime(Main.taskid); time >= 0; time--) {
                    if (pause == 0) {
                        if (isCancelled()) {
                            break;
                        }

                        if (time == 11) {
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
                        if (time == 0) {
                            if (countdownVol != null) {
                                countdownVol.setVolume(Volume.getMinVol());
                                countdownVol.ende();
                            }
                            babysteps.setText("Time: " + time + "s");
                            if (!compile(null)){
                                if(statusMessage.getText().equals(Status.CODE.toString()))
                                returnTab(null);    //Wenn nicht compiliert geh zu test falls in code
                                else if(statusMessage.getText().equals(Status.TEST.toString())) {  //wenn in test resette Tests
                                    Saves.loadData(tests, "build/resources/main/saves/test.txt");
                                    compileMessage.setText("Test resetted, weil Zeit abgelaufen ist");
                                }
                            }
                            else continueTab(null);
                        }
                    }else{      // Wenn der User im Refactor abschnitt ist, gibt es kein Zeitlimit
                        time = tasks.getBabystepsTime(Main.taskid);
                    }
                }
            }
            return 0;
        }
    };

    private Task<Integer> timer = new Task<Integer>() {

        @Override
        protected Integer call() throws Exception {
            int locCycles;
            errors = 0;
            while (!isCancelled()) {
                locCycles = cycles;
                int timeCount = 0;
                while (locCycles == cycles) {
                    if (isCancelled()) {
                        break;
                    }
                    timeCount++;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException interrupted) {
                        System.out.println("Time expiered");
                    }
                }
                //errorData.getData().add(new XYChart.Data<>(locCycles, errors));
                //timeData.getData().add(new XYChart.Data<>(locCycles, timeCount));
            }
            return 0;
        }
    };

    private Task<Integer> getVolume = new Task<Integer>() {
        @Override
        protected Integer call() throws Exception {
            System.out.println("Start");
            while (!isCancelled()) {
                if (isCancelled()) {
                    break;
                }
                if (Volume.getVolume() != (float) (Volume.getMinVol() * (1 - (volSlider.getValue() / 100)))) {
                    Volume.setVolume((float) (Volume.getMinVol() * (1 - (volSlider.getValue() / 100))));
                    System.out.print("K");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interrupted) {
                  System.out.println(interrupted.getMessage());
                }
            }
            return 0;
        }
    };
}

