/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc.actions;

import au.com.custom3dcnc.AppConfig;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

public class ZProbe {
    private static final ZProbe zProbe = new ZProbe();

    public static ZProbe getInstance() {
        return zProbe;
    }

    private ZProbe() {
    }

    public void execute(final TextArea consoleOutput) {        
        try {
            AppConfig appConfig = AppConfig.getInstance();
            File workingDir = new File(appConfig.getWorkingDir()).getCanonicalFile();
            ProcessBuilder builder = new ProcessBuilder(appConfig.getPythonNumpy(),
                    "2_Zprobe.py");
            builder.directory(workingDir);
            builder.redirectErrorStream(true);
            final Process p = builder.start();

            new PrintConsole(p.getErrorStream(), p.getInputStream(), consoleOutput).start();

            p.waitFor();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(GenerateGCode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
