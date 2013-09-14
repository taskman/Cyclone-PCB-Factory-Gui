/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import au.com.custom3dcnc.actions.ZProbeToolchange;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProcessStepsWindow {

    private static final ProcessStepsWindow processStepsWindow = new ProcessStepsWindow();

    private ProcessStepsWindow() {
    }

    public static ProcessStepsWindow getInstance() {
        return processStepsWindow;
    }

    public Stage setup(final TextArea consoleOutput) {
        final Stage processStepsWindowStage = new Stage(StageStyle.UTILITY);
        final StackPane stackPane = new StackPane();
        Node zProbeDoneView = zProbeDoneView(processStepsWindowStage);
        Node probingView = probingView();
        Node toolDidChangeView = toolDidChangeView(probingView, consoleOutput, zProbeDoneView);
        Node askDidToolChangeView = askDidToolChangeView(toolDidChangeView, processStepsWindowStage);
        stackPane.getChildren().addAll(askDidToolChangeView, toolDidChangeView, probingView, zProbeDoneView);

        final Scene scene = new Scene(stackPane, 200, 100);

        processStepsWindowStage.setTitle("Process");
        processStepsWindowStage.initModality(Modality.APPLICATION_MODAL);
        processStepsWindowStage.setScene(scene);
        processStepsWindowStage.setResizable(false);

        return processStepsWindowStage;
    }

    private VBox askDidToolChangeView(final Node toolDidChangeView, final Stage processStepsWindowStage) {
        final VBox vbox = new VBox(10);

        //question
        Label label = new Label("Did the tool change?");
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.getChildren().add(label);

        //buttons
        Button yes = new Button("Yes");
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                vbox.setVisible(false);
                toolDidChangeView.setVisible(true);
            }
        });

        Button no = new Button("No");
        no.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                processStepsWindowStage.close();
            }
        });

        HBox bottom = new HBox(10);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(yes, no);

        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(top, bottom);

        return vbox;
    }

    private VBox toolDidChangeView(final Node probingView, final TextArea consoleOutput, final Node zProbeDoneView) {
        final VBox vbox = new VBox(10);
        vbox.setVisible(false);

        //question
        Label label = new Label("Connect Z-Probe wires.");
        Label label2 = new Label("Click 'OK' when done.");
        VBox top = new VBox(10);
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(label, label2);

        //buttons
        Button yes = new Button("OK");
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                vbox.setVisible(false);
                probingView.setVisible(true);

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        ZProbeToolchange.getInstance().execute(consoleOutput);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                probingView.setVisible(false);
                                zProbeDoneView.setVisible(true);
                            }
                        });
                    }
                };
                new Thread(runnable).start();
            }
        });


        HBox bottom = new HBox(10);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(yes);

        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(top, bottom);

        return vbox;
    }

    private VBox probingView() {
        final VBox vbox = new VBox(10);
        vbox.setVisible(false);

        //question
        Label label = new Label("Probing.  Please wait...");
        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER);
        top.getChildren().add(label);

        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(top);

        return vbox;
    }

    private VBox zProbeDoneView(final Stage processStepsWindowStage) {
        final VBox vbox = new VBox(10);
        vbox.setVisible(false);

        //question
        Label label = new Label("Disconnect Z-Probe wires.");
        Label label2 = new Label("Click 'OK' when done.");
        VBox top = new VBox(10);
        top.setAlignment(Pos.CENTER);
        top.getChildren().addAll(label, label2);

        //buttons
        Button yes = new Button("OK");
        yes.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        processStepsWindowStage.close();
                    }
                });
            }
        });


        HBox bottom = new HBox(10);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(yes);

        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(top, bottom);

        return vbox;
    }
}
