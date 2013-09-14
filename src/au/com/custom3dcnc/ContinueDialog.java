/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import java.net.URL;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBuilder;
import javafx.scene.control.LabelBuilder;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.HBoxBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.web.WebView;

public class ContinueDialog {

    private static final ContinueDialog continueDialog = new ContinueDialog();

    public static ContinueDialog getInstance() {
        return continueDialog;
    }

    private ContinueDialog() {
    }

    public Stage setup(final Stage primaryStage) {
        final WebView webView = new WebView(); webView.getEngine().load("http://docs.oracle.com/javafx/");
        
        final Stage dialog = new Stage(StageStyle.TRANSPARENT);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setScene(
                new Scene(
                HBoxBuilder.create().styleClass("modal-dialog").children(
                LabelBuilder.create().text("Will you like this page?").build(),
                ButtonBuilder.create().text("Yes").defaultButton(true).onAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // take action and close the dialog.
                System.out.println("Liked: " + webView.getEngine().getTitle());
                primaryStage.getScene().getRoot().setEffect(null);
                dialog.close();
            }
        }).build(),
                ButtonBuilder.create().text("No").cancelButton(true).onAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // abort action and close the dialog.
                System.out.println("Disliked: " + webView.getEngine().getTitle());
                primaryStage.getScene().getRoot().setEffect(null);
                dialog.close();
            }
        }).build()).build(), Color.TRANSPARENT));
        URL url = getClass().getResource("/resources/css/modal-dialog.css");

        dialog.getScene().getStylesheets().add(url.toExternalForm());

        // allow the dialog to be dragged around.
        final Node root = dialog.getScene().getRoot();
        final Delta dragDelta = new Delta();
        /*root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = dialog.getX() - mouseEvent.getScreenX();
                dragDelta.y = dialog.getY() - mouseEvent.getScreenY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                dialog.setX(mouseEvent.getScreenX() + dragDelta.x);
                dialog.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });*/

        // show the confirmation dialog each time a new page is loaded.
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State newState) {
                if (newState.equals(Worker.State.SUCCEEDED)) {
                    primaryStage.getScene().getRoot().setEffect(new BoxBlur());
                    dialog.show();
                }
            }
        });
        return dialog;
    }

    class Delta {

        double x, y;
    }
}
