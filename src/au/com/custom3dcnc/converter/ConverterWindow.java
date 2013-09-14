/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc.converter;

import au.com.custom3dcnc.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConverterWindow {
    private static final ConverterWindow converterWindow = new ConverterWindow();

    public static ConverterWindow getInstance() {
        return converterWindow;
    }

    private ConverterWindow() {
    }

    private void configureFileChooserInitialDirectory(final File initDir, final FileChooser fileChooser) {
        if (initDir.exists()) {
            fileChooser.setInitialDirectory(initDir);
        }
    }

    public Stage setup(final ConvertersEnum converter) {
        final AppConfig appConfig = AppConfig.getInstance();
        appConfig.loadProperties();
        final FileChooser fileChooser = new FileChooser();

        //setup the grid
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        //setup the window
        final Scene scene = new Scene(gridPane, 950, 170);
        final Stage converterWindowStage = new Stage(StageStyle.UTILITY);
        converterWindowStage.setTitle(converter.getDescription() + " converter");
        converterWindowStage.initModality(Modality.APPLICATION_MODAL);
        converterWindowStage.setScene(scene);
        converterWindowStage.setResizable(false);

        int row = 1;
        int col = 1;
        //board name
        final Label converterBoardLabel = new Label("Board name");
        gridPane.add(converterBoardLabel, col, row);

        final TextField converterBoard = new TextField(appConfig.getConverterBoardName());
        converterBoard.setMinWidth(700);
        gridPane.add(converterBoard, col + 1, row++);

        //top/bottom file
        final Label circuitLabel = new Label("Circuit file");
        gridPane.add(circuitLabel, col, row);

        final TextField circuitDirLocation = new TextField(appConfig.getConverterCircuitFile());
        circuitDirLocation.setMinWidth(700);
        gridPane.add(circuitDirLocation, col + 1, row);

        final Button circuitDirBrowse = new Button("...");
        circuitDirBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Top", "*.gbr"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Bottom", "*.gbr"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(circuitDirLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(converterWindowStage);
                if (file != null) {
                    circuitDirLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(circuitDirBrowse, col + 2, row++);


        //location of python with NumPy installed
        final Label drillFileLabel = new Label("Drill file");
        gridPane.add(drillFileLabel, col, row);

        final TextField drillFileLocation = new TextField(appConfig.getConverterDrillFile());
        drillFileLocation.setMinWidth(700);
        gridPane.add(drillFileLocation, col + 1, row);

        final Button drillFileBrowse = new Button("...");
        drillFileBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Drill file", "*.gbr"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(drillFileLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(converterWindowStage);
                if (file != null) {
                    drillFileLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(drillFileBrowse, col + 2, row++);


        //location of PyPy
        final Label cutFileLabel = new Label("Cut file");
        gridPane.add(cutFileLabel, col, row);

        final TextField cutFileLocation = new TextField(appConfig.getConverterCutFile());
        cutFileLocation.setMinWidth(700);
        gridPane.add(cutFileLocation, col + 1, row);

        final Button cutFileBrowse = new Button("...");
        cutFileBrowse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Cut file", "*.gbr"));
                configureFileChooserInitialDirectory(new File(Util.getOnlyDirectory(cutFileLocation.getText())), fileChooser);
                File file = fileChooser.showOpenDialog(converterWindowStage);
                if (file != null) {
                    cutFileLocation.setText(file.toString());
                }
            }
        });
        gridPane.add(cutFileBrowse, col + 2, row++);

        //button bar
        HBox hbox = new HBox(10);
        gridPane.add(hbox, col, row);

        final Button close = new Button("Close");
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                converterWindowStage.close();
            }
        });
        hbox.getChildren().add(close);

        final Button save = new Button("Convert");
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                appConfig.setConverterBoardName(converterBoard.getText());
                appConfig.setConverterCircuitFile(circuitDirLocation.getText());
                appConfig.setConverterDrillFile(drillFileLocation.getText());
                appConfig.setConverterCutFile(cutFileLocation.getText());
                appConfig.saveProperties();

                convert(converter, converterBoard.getText(), "-B_Cu.gbl", circuitDirLocation.getText(), appConfig);
                convert(converter, converterBoard.getText(), ".drl", drillFileLocation.getText(), appConfig);
                convert(converter, converterBoard.getText(), "-Edge_Cuts.gbr", cutFileLocation.getText(), appConfig);

                converterWindowStage.close();
            }
        });
        hbox.getChildren().add(save);

        return converterWindowStage;
    }

    private void convert(final ConvertersEnum converter, final String boardName, final String extension, final String fileName, final AppConfig appConfig) {
        try {
            String newFileContents = converter.getConverter().execute(fileName);
            String outDir = appConfig.getGerberInputDir();
            File file = new File(outDir + "/" + boardName + extension);

            try (FileWriter fileWriter = new FileWriter(file)) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    bufferedWriter.write(newFileContents);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConverterWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
