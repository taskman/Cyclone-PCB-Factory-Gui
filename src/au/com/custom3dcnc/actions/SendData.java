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

public class SendData {
    private static final SendData sendData = new SendData();

    public static SendData getInstance() {
        return sendData;
    }

    private SendData() {
    }

    public void execute(final TextArea consoleOutput, final SendActionEnum sendAction) {
        try {
            AppConfig appConfig = AppConfig.getInstance();
            File workingDir = new File(appConfig.getWorkingDir()).getCanonicalFile();

            ActionsUtil.setupPythonConfigFile(appConfig, appConfig.getLastBoardName(), sendAction);

            ProcessBuilder builder = new ProcessBuilder(appConfig.getPythonNumpy(),
                    "3_Send.py");
            builder.directory(workingDir);
            builder.redirectErrorStream(true);
            final Process p = builder.start();

            new PrintConsole(p.getErrorStream(), p.getInputStream(), consoleOutput).start();

            p.waitFor();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(GenerateGCode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*private void modifyPythonConfigFile(final AppConfig appConfig, final SendActionEnum sendAction) throws IOException {
        //setup configuration.py
        File pythonConfigurationFile = new File(appConfig.getEtchConfigFile());
        Properties pythonConfigProperties = ActionsUtil.loadProperties(pythonConfigurationFile);

        //change properties and store
        SendActionEnum[] sendActionValues = SendActionEnum.values();
        for (SendActionEnum sendActionEnum : sendActionValues) {
            pythonConfigProperties.setProperty(sendActionEnum.getDescription(), "0");
        }
        pythonConfigProperties.setProperty(sendAction.getDescription(), "1");
        pythonConfigProperties.setProperty("filePath", "\"" + appConfig.getGerberOutputDir() + "/\"");
//        pythonConfigProperties.setProperty("fileName", "\"" + appConfig.getLastBoardName() + "\"");
        ActionsUtil.storeProperties(pythonConfigurationFile, pythonConfigProperties);

        ActionsUtil.fixPathVariable(pythonConfigurationFile);
    }*/
}
