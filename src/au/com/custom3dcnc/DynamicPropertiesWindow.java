/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import au.com.custom3dcnc.actions.ActionsUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DynamicPropertiesWindow {

    private static final DynamicPropertiesWindow dynamicPropertiesWindow = new DynamicPropertiesWindow();

    public static DynamicPropertiesWindow getInstance() {
        return dynamicPropertiesWindow;
    }

    public Stage setup(final String propertiesFileName) {
        //setup the grid
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        ScrollPane scollPane = new ScrollPane();
        scollPane.setContent(gridPane);

        final Scene scene = new Scene(scollPane, 450, 750);
        final Stage propertiesWindowStage = new Stage(StageStyle.UTILITY);
        propertiesWindowStage.setTitle("Properties");
        propertiesWindowStage.initModality(Modality.APPLICATION_MODAL);
        propertiesWindowStage.setScene(scene);
        propertiesWindowStage.setResizable(false);
        try {
            final File propertiesFile = new File(propertiesFileName).getCanonicalFile();
            final Properties properties = ActionsUtil.loadProperties(propertiesFile);
            List<String> keys = new ArrayList(properties.keySet());
            Collections.sort(keys);

            final ArrayList<TextField> textFieldList = new ArrayList<>();
            int row = 1;
            for (String key : keys) {
                Label label = new Label(key);
                gridPane.add(label, 1, row);

                final TextField textField = new TextField(properties.getProperty(key));
                textField.setId(key);
                textField.setMinWidth(250);
                gridPane.add(textField, 2, row++);
                textFieldList.add(textField);
            }

            HBox hbox = new HBox(10);
            gridPane.add(hbox, 1, row++);

            final Button close = new Button("Close");
            close.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    propertiesWindowStage.close();
                }
            });

            final Button save = new Button("Save");
            save.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    for (TextField textField : textFieldList) {
                        String key = textField.getId();
                        String value = textField.getText();
                        properties.setProperty(key, value);
                    }
                    try {
                        ActionsUtil.storeProperties(propertiesFile, properties);
                    } catch (IOException ex) {
                        Logger.getLogger(DynamicPropertiesWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    propertiesWindowStage.close();
                }
            });
            hbox.getChildren().addAll(close, save);

            //add some space below the buttons
            gridPane.add(new HBox(10), 1, row++);


        } catch (IOException ex) {
            Logger.getLogger(DynamicPropertiesWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return propertiesWindowStage;
    }
}
