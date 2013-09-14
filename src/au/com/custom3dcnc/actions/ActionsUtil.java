/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc.actions;

import au.com.custom3dcnc.AppConfig;
import au.com.custom3dcnc.SendActionEnum;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class ActionsUtil {
    public static void setupPythonConfigFile(final AppConfig appConfig, final String boardFileName, final SendActionEnum sendAction) throws IOException {
        //setup configuration.py
        File pythonConfigurationFile = new File(appConfig.getEtchConfigFile()).getCanonicalFile();
        Properties pythonConfigProperties = ActionsUtil.loadProperties(pythonConfigurationFile);

        //change properties and store
        //set all the etch data to 0
        SendActionEnum[] sendActionValues = SendActionEnum.values();
        for (SendActionEnum sendActionEnum : sendActionValues) {
            pythonConfigProperties.setProperty(sendActionEnum.getDescription(), "0");
        }

        //activate the correct etch
        pythonConfigProperties.setProperty(sendAction.getDescription(), "1");

        //change the config so that the python code knows it is being called by the gui
        pythonConfigProperties.setProperty("runInGui", "True");

        //set the device name
        pythonConfigProperties.setProperty("DEVICE", "\"" + appConfig.getDevicePort() + "\"");

        //this fixes the problem with directories
        File gerberOutputDir = new File(appConfig.getGerberOutputDir()).getCanonicalFile();
        pythonConfigProperties.setProperty("filePath", "\"" + gerberOutputDir.getAbsolutePath() + "/\"");
        pythonConfigProperties.setProperty("fileName", "\"" + boardFileName + "\"");
        ActionsUtil.storeProperties(pythonConfigurationFile, pythonConfigProperties);
        ActionsUtil.fixPathVariable(pythonConfigurationFile);
    }

    public  static Properties loadProperties(final File file) throws IOException {
        //load the gcode config file
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader(file)) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                properties.load(bufferedReader);
            }
        }
        return properties;
    }

    public static void storeProperties(final File file, final Properties properties) throws IOException {
        try (FileWriter fileWriter = new FileWriter(file)) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                properties.store(bufferedWriter, null);
            }
        }
    }

    public static void fixPathVariable(File configFile) throws IOException {
        //fix path properties so that it works correctly with python
        //read the contents of the file and make sure to use new line
        String line;
        StringBuilder configFileContentsBuilder = new StringBuilder();
        try (FileReader fileReader = new FileReader(configFile)) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    configFileContentsBuilder.append(line);
                    configFileContentsBuilder.append("\n");
                }
            }
        }

        //replace the characters that cause problems on python
        String configFileContents = configFileContentsBuilder.toString();
        configFileContents = configFileContents.replace("\\:", ":");
        configFileContents = configFileContents.replace("\\\\", "/");
        try (FileWriter fileWriter = new FileWriter(configFile)) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.append(configFileContents);
            }
        }
    }

}
