/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import j.extensions.comm.SerialComm;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PropertiesWindow {
    private static final PropertiesWindow propertiesWindow = new PropertiesWindow();

    public static PropertiesWindow getInstance() {
        return propertiesWindow;
    }

    private PropertiesWindow() {
    }

    private void configureFileChooserInitialDirectory(final File initDir, final FileChooser fileChooser) {
        if (initDir.exists()) {
            fileChooser.setInitialDirectory(initDir);
        }
    }

    private void configureDirectoryChooserInitialDirectory(final File initDir, final DirectoryChooser directoryChooser) {
        if (initDir.exists()) {
            directoryChooser.setInitialDirectory(initDir);
        }
    }

    public Stage setup() {
        final AppConfig appConfig = AppConfig.getInstance();
        appConfig.loadProperties();
        final FileChooser fileChooser = new FileChooser();
        final DirectoryChooser directoryChooser = new DirectoryChooser();

        //setup the grid
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        //setup the window
        final Scene scene = new Scene(gridPane, 950, 390);
        final Stage propertiesWindowStage = new Stage(StageStyle.UTILITY);
        propertiesWindowStage.setTitle("Properties");
        propertiesWindowStage.initModality(Modality.APPLICATION_MODAL);
        propertiesWindowStage.setScene(scene);
        propertiesWindowStage.setResizable(false);

        int row = 1;
        int col = 1;
        //location of the gerber2gcode config file
        final Label configFileLabel = new Label("Gerber2GCode config file");
        gridPane.add(configFileLabel, col, row);

        final TextField configFileLocation = new TextField(appConfig.getGerber2GcodeConfigFile());
        configFileLocation.setMinWidth(700);
        gridPane.add(configFileLocation, col + 1, row);

        final Button configFileBrowse = new Button("...");
        configFileBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Config file", "*.conf"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(configFileLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(propertiesWindowStage);
                if (file != null) {
                    configFileLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(configFileBrowse, col + 2, row++);

        //working directory
        final Label workingDirLabel = new Label("Working dir");
        gridPane.add(workingDirLabel, col, row);

        final TextField workingDirLocation = new TextField(appConfig.getWorkingDir());
        workingDirLocation.setMinWidth(700);
        gridPane.add(workingDirLocation, col + 1, row);

        final Button workingDirBrowse = new Button("...");
        workingDirBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File initDir = new File(workingDirLocation.getText());
                configureDirectoryChooserInitialDirectory(initDir, directoryChooser);
                File file = directoryChooser.showDialog(propertiesWindowStage);
                if (file != null) {
                    workingDirLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(workingDirBrowse, col + 2, row++);


        //location of python with NumPy installed
        final Label pythonWithNumPyLabel = new Label("Python with NumPy");
        gridPane.add(pythonWithNumPyLabel, col, row);

        final TextField pythonWithNumPyLocation = new TextField(appConfig.getPythonNumpy());
        pythonWithNumPyLocation.setMinWidth(700);
        gridPane.add(pythonWithNumPyLocation, col + 1, row);

        final Button pythonNumPyBrowse = new Button("...");
        pythonNumPyBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Python executable", "*.exe"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(pythonWithNumPyLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(propertiesWindowStage);
                if (file != null) {
                    pythonWithNumPyLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(pythonNumPyBrowse, col + 2, row++);


        //location of PyPy
        final Label pyPyLabel = new Label("PyPy");
        gridPane.add(pyPyLabel, col, row);

        final TextField pyPyLocation = new TextField(appConfig.getPypy());
        pyPyLocation.setMinWidth(700);
        gridPane.add(pyPyLocation, col + 1, row);

        final Button pyPyBrowse = new Button("...");
        pyPyBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PyPy executable", "*.exe"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(pyPyLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(propertiesWindowStage);
                if (file != null) {
                    pyPyLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(pyPyBrowse, col + 2, row++);

        //location of etch and send config file
        final Label etchAndSendConfigLabel = new Label("Etch and send config file");
        gridPane.add(etchAndSendConfigLabel, col, row);

        final TextField etchAndSendConfigLocation = new TextField(appConfig.getEtchConfigFile());
        etchAndSendConfigLocation.setMinWidth(700);
        gridPane.add(etchAndSendConfigLocation, col + 1, row);

        final Button etchAndSendConfigBrowse = new Button("...");
        etchAndSendConfigBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Etch and send config file", "*.py"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(etchAndSendConfigLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(propertiesWindowStage);
                if (file != null) {
                    etchAndSendConfigLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(etchAndSendConfigBrowse, col + 2, row++);


        //gerber input directory
        final Label gerberInputDirLabel = new Label("Gerber input dir");
        gridPane.add(gerberInputDirLabel, col, row);

        final TextField gerberInputDirLocation = new TextField(appConfig.getGerberInputDir());
        gerberInputDirLocation.setMinWidth(700);
        gridPane.add(gerberInputDirLocation, col + 1, row);

        final Button gerberInputDirBrowse = new Button("...");
        gerberInputDirBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File initDir = new File(gerberInputDirLocation.getText());
                configureDirectoryChooserInitialDirectory(initDir, directoryChooser);
                File file = directoryChooser.showDialog(propertiesWindowStage);
                if (file != null) {
                    gerberInputDirLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(gerberInputDirBrowse, col + 2, row++);

        //gerber output directory
        final Label gerberOutputDirLabel = new Label("Gerber output dir");
        gridPane.add(gerberOutputDirLabel, col, row);

        final TextField gerberOutputDirLocation = new TextField(appConfig.getGerberOutputDir());
        gerberOutputDirLocation.setMinWidth(700);
        gridPane.add(gerberOutputDirLocation, col + 1, row);

        final Button gerberOutputDirBrowse = new Button("...");
        gerberOutputDirBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File initDir = new File(gerberOutputDirLocation.getText());
                configureDirectoryChooserInitialDirectory(initDir, directoryChooser);
                File file = directoryChooser.showDialog(propertiesWindowStage);
                if (file != null) {
                    gerberOutputDirLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(gerberOutputDirBrowse, col + 2, row++);

        //select comm port to use
        final Label devicePort = new Label("Device port");
        gridPane.add(devicePort, col, row);

        final ComboBox comboBoxPorts = new ComboBox();
        final TextField textFieldPorts = new TextField(appConfig.getDevicePort());
        if (appConfig.getDisableCommChecking().equals("true")) {
            gridPane.add(textFieldPorts, col + 1, row++);
        } else {
            comboBoxPorts.setValue(appConfig.getDevicePort());
            SerialComm[] portList = SerialComm.getCommPorts();
            for (SerialComm serialCommItem : portList) {
                comboBoxPorts.getItems().add(serialCommItem.getSystemPortName());
            }
            gridPane.add(comboBoxPorts, col + 1, row++);
        }

        //enable or disable comm port checking code
        final Label disableCommPortCheckingLabel = new Label("Disable comm port checking");
        gridPane.add(disableCommPortCheckingLabel, col, row);
        final ComboBox disableCommPortChecking = new ComboBox();
        disableCommPortChecking.setValue(appConfig.getDisableCommChecking());
        disableCommPortChecking.getItems().addAll("true", "false");

        gridPane.add(disableCommPortChecking, col + 1, row++);

        //profiles directory
        final Label profilesDirLabel = new Label("Profiles dir");
        gridPane.add(profilesDirLabel, col, row);

        final TextField profilesDirLocation = new TextField(appConfig.getProfileDir());
        profilesDirLocation.setMinWidth(700);
        gridPane.add(profilesDirLocation, col + 1, row);

        final Button profilesDirBrowse = new Button("...");
        profilesDirBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File initDir = new File(profilesDirLocation.getText());
                configureDirectoryChooserInitialDirectory(initDir, directoryChooser);
                File file = directoryChooser.showDialog(propertiesWindowStage);
                if (file != null) {
                    profilesDirLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(profilesDirBrowse, col + 2, row++);

        //profiles directory
        final Label java3dDirLabel = new Label("Java 3D dir");
        gridPane.add(java3dDirLabel, col, row);

        final TextField java3dDirLocation = new TextField(appConfig.getJava3dLocation());
        java3dDirLocation.setMinWidth(700);
        gridPane.add(java3dDirLocation, col + 1, row);

        final Button java3dDirBrowse = new Button("...");
        java3dDirBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File initDir = new File(java3dDirLocation.getText());
                configureDirectoryChooserInitialDirectory(initDir, directoryChooser);
                File file = directoryChooser.showDialog(propertiesWindowStage);
                if (file != null) {
                    java3dDirLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(java3dDirBrowse, col + 2, row++);

        //button bar
        HBox hbox = new HBox(10);
        gridPane.add(hbox, col, row);

        final Button close = new Button("Close");
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                propertiesWindowStage.close();
            }
        });
        hbox.getChildren().add(close);

        final Button save = new Button("Save");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                appConfig.setGerber2GcodeConfigFile(configFileLocation.getText());
                appConfig.setWorkingDir(workingDirLocation.getText());
                appConfig.setPythonNumpy(pythonWithNumPyLocation.getText());
                appConfig.setPypy(pyPyLocation.getText());
                appConfig.setEtchConfigFile(etchAndSendConfigLocation.getText());
                appConfig.setGerberInputDir(gerberInputDirLocation.getText());
                appConfig.setGerberOutputDir(gerberOutputDirLocation.getText());
                if (appConfig.getDisableCommChecking().equals("true")) {
                    appConfig.setDevicePort(textFieldPorts.getText());
                } else {
                    appConfig.setDevicePort((String) comboBoxPorts.getValue());
                }
                appConfig.setProfileDir(profilesDirLocation.getText());
                appConfig.setDisableCommChecking((String) disableCommPortChecking.getValue());

                String lastProfile = appConfig.getLastProfileName();
                if (lastProfile == null || (lastProfile.equals(""))) {
                    appConfig.setLastProfileName("default");
                }
                appConfig.setJava3dLocation(java3dDirLocation.getText());

                appConfig.saveProperties();
                propertiesWindowStage.close();
            }
        });
        hbox.getChildren().add(save);

        return propertiesWindowStage;
    }
}
