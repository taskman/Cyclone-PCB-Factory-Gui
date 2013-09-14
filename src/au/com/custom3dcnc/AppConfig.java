/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc;

import static au.com.custom3dcnc.ApplicationPropertyConstants.CONVERTER_BOARD_NAME;
import static au.com.custom3dcnc.ApplicationPropertyConstants.CONVERTER_CIRCUIT_FILE;
import static au.com.custom3dcnc.ApplicationPropertyConstants.CONVERTER_CUT_FILE;
import static au.com.custom3dcnc.ApplicationPropertyConstants.CONVERTER_DRILL_FILE;
import static au.com.custom3dcnc.ApplicationPropertyConstants.DEVICE_PORT;
import static au.com.custom3dcnc.ApplicationPropertyConstants.GERBER_OUTPUT_DIR;
import static au.com.custom3dcnc.ApplicationPropertyConstants.LAST_BOARD_NAME;
import static au.com.custom3dcnc.ApplicationPropertyConstants.LAST_PROFILE_NAME;
import static au.com.custom3dcnc.ApplicationPropertyConstants.PROFILES_DIR;
import java.util.Properties;

public final class AppConfig implements ApplicationPropertyConstants {
    private Properties properties;
    private String workingDir;
    private String pythonNumpy;
    private String pypy;
    private String gerber2GcodeConfigFile;
    private String lastBoardName;
    private String etchConfigFile;
    private String gerberInputDir;
    private String gerberOutputDir;
    private String devicePort;
    private String converterBoardName;
    private String converterDrillFile;
    private String converterCutFile;
    private String converterCircuitFile;
    private String lastProfileName;
    private String profileDir;
    private String disableCommChecking;
    private String java3dDir;

    private static final AppConfig appConfig = new AppConfig();

    public String getJava3dLocation() {
        return java3dDir;
    }

    public void setJava3dLocation(String java3dLocation) {
        this.java3dDir = java3dLocation;
    }

    public String getDisableCommChecking() {
        return disableCommChecking;
    }

    public void setDisableCommChecking(String disableCommChecking) {
        this.disableCommChecking = disableCommChecking;
    }

    public String getLastProfileName() {
        return lastProfileName;
    }

    public void setLastProfileName(String lastProfileName) {
        this.lastProfileName = lastProfileName;
    }

    public String getProfileDir() {
        return profileDir;
    }

    public void setProfileDir(String profileDir) {
        this.profileDir = profileDir;
    }

    public String getConverterBoardName() {
        return converterBoardName;
    }

    public void setConverterBoardName(String converterBoardName) {
        this.converterBoardName = converterBoardName;
    }

    public String getConverterDrillFile() {
        return converterDrillFile;
    }

    public void setConverterDrillFile(String converterDrillFile) {
        this.converterDrillFile = converterDrillFile;
    }

    public String getConverterCutFile() {
        return converterCutFile;
    }

    public void setConverterCutFile(String converterCutFile) {
        this.converterCutFile = converterCutFile;
    }

    public String getConverterCircuitFile() {
        return converterCircuitFile;
    }

    public void setConverterCircuitFile(String converterCircuitFile) {
        this.converterCircuitFile = converterCircuitFile;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getPythonNumpy() {
        return pythonNumpy;
    }

    public void setPythonNumpy(String pythonNumpy) {
        this.pythonNumpy = pythonNumpy;
    }

    public String getPypy() {
        return pypy;
    }

    public void setPypy(String pypy) {
        this.pypy = pypy;
    }

    public String getGerber2GcodeConfigFile() {
        return gerber2GcodeConfigFile;
    }

    public void setGerber2GcodeConfigFile(String gerber2GcodeConfigFile) {
        this.gerber2GcodeConfigFile = gerber2GcodeConfigFile;
    }

    public String getLastBoardName() {
        return lastBoardName;
    }

    public void setLastBoardName(String lastBoardName) {
        this.lastBoardName = lastBoardName;
    }

    public String getEtchConfigFile() {
        return etchConfigFile;
    }

    public void setEtchConfigFile(String etchConfigFile) {
        this.etchConfigFile = etchConfigFile;
    }

    public String getGerberInputDir() {
        return gerberInputDir;
    }

    public void setGerberInputDir(String gerberInputDir) {
        this.gerberInputDir = gerberInputDir;
    }

    public String getGerberOutputDir() {
        return gerberOutputDir;
    }

    public void setGerberOutputDir(String gerberOutputDir) {
        this.gerberOutputDir = gerberOutputDir;
    }

    public String getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    public Properties getProperties() {
        return properties;
    }

    private AppConfig() {
        loadProperties();
    }

    private String getProperty(final String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            return "";
        } else {
            return value;
        }
    }

    public void saveProperties() {
        properties.setProperty(GERBER_2_GCODE_CONFIG_FILE, gerber2GcodeConfigFile);
        properties.setProperty(WORKING_DIR, workingDir);
        properties.setProperty(PYTHON_NUMPY, pythonNumpy);
        properties.setProperty(PYPY, pypy);
        properties.setProperty(ETCH_CONFIG_FILE, etchConfigFile);
        properties.setProperty(GERBER_INPUT_DIR, gerberInputDir);
        properties.setProperty(GERBER_OUTPUT_DIR, gerberOutputDir);
        properties.setProperty(LAST_BOARD_NAME, lastBoardName);
        properties.setProperty(DEVICE_PORT, devicePort);

        properties.setProperty(CONVERTER_BOARD_NAME, converterBoardName);
        properties.setProperty(CONVERTER_DRILL_FILE, converterDrillFile);
        properties.setProperty(CONVERTER_CUT_FILE, converterCutFile);
        properties.setProperty(CONVERTER_CIRCUIT_FILE, converterCircuitFile);

        properties.setProperty(LAST_PROFILE_NAME, lastProfileName);
        properties.setProperty(PROFILES_DIR, profileDir);
        properties.setProperty(DISABLE_COMM_CHECKING, disableCommChecking);
        properties.setProperty(JAVA3D_DIR, java3dDir);

        Util.saveApplicationProperties(properties);
    }

    public void loadProperties() {
        properties = Util.readApplicationProperties();

        gerber2GcodeConfigFile = getProperty(GERBER_2_GCODE_CONFIG_FILE);
        workingDir = getProperty(WORKING_DIR);
        pythonNumpy = getProperty(PYTHON_NUMPY);
        pypy = getProperty(PYPY);
        lastBoardName = getProperty(LAST_BOARD_NAME);
        etchConfigFile = getProperty(ETCH_CONFIG_FILE);
        gerberInputDir = getProperty(GERBER_INPUT_DIR);
        gerberOutputDir = getProperty(GERBER_OUTPUT_DIR);
        devicePort = getProperty(DEVICE_PORT);

        converterBoardName = getProperty(CONVERTER_BOARD_NAME);
        converterDrillFile = getProperty(CONVERTER_DRILL_FILE);
        converterCutFile = getProperty(CONVERTER_CUT_FILE);
        converterCircuitFile = getProperty(CONVERTER_CIRCUIT_FILE);

        lastProfileName = getProperty(LAST_PROFILE_NAME);
        profileDir = getProperty(PROFILES_DIR);
        disableCommChecking = getProperty(DISABLE_COMM_CHECKING).toLowerCase().trim();
        if (disableCommChecking.equals("")) {
            disableCommChecking = "false";
        }
        java3dDir = getProperty(JAVA3D_DIR);
    }

    public static AppConfig getInstance() {
        return appConfig;
    }


}
