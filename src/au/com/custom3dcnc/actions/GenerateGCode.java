/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc.actions;

import au.com.custom3dcnc.AppConfig;
import au.com.custom3dcnc.SendActionEnum;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

public class GenerateGCode {
    private static final GenerateGCode generateGCode = new GenerateGCode();

    public static GenerateGCode getInstance() {
        return generateGCode;
    }

    protected GenerateGCode() {
    }

    public void execute(final String boardFileName, final TextArea consoleOutput) {
        try {
            //setup gerber to gcode config file
            AppConfig appConfig = AppConfig.getInstance();
            modifyGerber2GcodeConfigFile(appConfig, boardFileName);
            ActionsUtil.setupPythonConfigFile(appConfig, boardFileName, SendActionEnum.SHOW_ETCH);

            //execute the python script
            File workingDir = new File(appConfig.getWorkingDir()).getCanonicalFile();
            ProcessBuilder builder = new ProcessBuilder(appConfig.getPythonNumpy(),
                    "1_GenerateGcode.py",
                    appConfig.getPypy());
            builder.directory(workingDir);
            builder.redirectErrorStream(true);
            final Process p = builder.start();

            new PrintConsole(p.getErrorStream(), p.getInputStream(), consoleOutput).start();

            p.waitFor();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(GenerateGCode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected void modifyGerber2GcodeConfigFile(AppConfig appConfig, final String boardFileName) throws IOException {
        File gerber2GcodeConfigFile = new File(appConfig.getGerber2GcodeConfigFile());
        Properties gerberProperties = ActionsUtil.loadProperties(gerber2GcodeConfigFile);
        //change the properties and store
        File file = new File(appConfig.getGerberInputDir()).getCanonicalFile();
        gerberProperties.setProperty("GERBER_DIR", "\"" + file.getAbsolutePath() + "\"");

        file = new File(appConfig.getGerberOutputDir()).getCanonicalFile();
        gerberProperties.setProperty("OUT_DIR", "\"" + file.getAbsolutePath() + "\"");
        gerberProperties.setProperty("BACK_FILE", "\"" + boardFileName + "-B_Cu.gbl\"");
        gerberProperties.setProperty("DRILL_FILE", "\"" + boardFileName + ".drl\"");
        gerberProperties.setProperty("EDGE_FILE", "\"" + boardFileName + "-Edge_Cuts.gbr\"");
        gerberProperties.setProperty("OUT_BACK_FILE", "\"" + boardFileName + "_etch.gcode\"");
        gerberProperties.setProperty("OUT_BACK_2PASS_FILE", "\"" + boardFileName + "_etch2pass.gcode\"");
        gerberProperties.setProperty("OUT_BACK_3PASS_FILE", "\"" + boardFileName + "_etch3pass.gcode\"");
        gerberProperties.setProperty("OUT_DRILL_FILE", "\"" + boardFileName + "_drill.gcode\"");
        gerberProperties.setProperty("OUT_EDGE_FILE", "\"" + boardFileName + "_edge.gcode\"");
        ActionsUtil.storeProperties(gerber2GcodeConfigFile, gerberProperties);
        ActionsUtil.fixPathVariable(gerber2GcodeConfigFile);
    }
}
