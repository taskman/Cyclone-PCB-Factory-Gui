/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc.actions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class PrintConsole extends Thread {
    private BufferedReader error;
    private BufferedReader input;
    private TextArea consoleOutput;

    public PrintConsole(final InputStream error, final InputStream input, final TextArea consoleOutput) {
        this.error = new BufferedReader(new InputStreamReader(error));
        this.input = new BufferedReader(new InputStreamReader(input));
        this.consoleOutput = consoleOutput;

        this.consoleOutput.setEditable(false);
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = error.readLine()) != null) {
                System.out.println(line);
                consoleOutput.appendText(line);
                consoleOutput.appendText("\n");
            }
            error.close();

            final StringBuilder builder = new StringBuilder(consoleOutput.getText());
            while ((line = input.readLine()) != null) {
                System.out.println(line);
                if (line != null) {
                    builder.append(line);
                    builder.append("\n");

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            consoleOutput.setText(builder.toString());
                            consoleOutput.appendText("");
                        }
                    });
                }
            }

            input.close();

        } catch (NullPointerException | IOException ex) {
            Logger.getLogger(GenerateGCode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
