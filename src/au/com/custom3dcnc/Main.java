/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 *
 * @author dutoitk
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainWindow.getInstance().setup(primaryStage);
        primaryStage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
