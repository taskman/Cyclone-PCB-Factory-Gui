/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc.actions;

import au.com.custom3dcnc.AppConfig;
import au.com.custom3dcnc.SendActionEnum;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;

public class GenerateVisolateGCode extends GenerateGCode {
    private static final GenerateVisolateGCode generateGCode = new GenerateVisolateGCode();

    public static GenerateVisolateGCode getInstance() {
        return generateGCode;
    }

    protected GenerateVisolateGCode() {
    }

    @Override
    public void execute(final String boardFileName, final TextArea consoleOutput) {
        try {
            //setup gerber to gcode config file
            AppConfig appConfig = AppConfig.getInstance();
            modifyGerber2GcodeConfigFile(appConfig, boardFileName);
            ActionsUtil.setupPythonConfigFile(appConfig, boardFileName, SendActionEnum.SHOW_ETCH);

            //delete all unwanted files first
            deleteFile(boardFileName + "_drill.gcode", appConfig);
            deleteFile(boardFileName + "_edge.gcode", appConfig);
            deleteFile(boardFileName + "_etch.gcode", appConfig);
            deleteFile(boardFileName + "_etch2pass.gcode", appConfig);
            deleteFile(boardFileName + "_etch3pass.gcode", appConfig);

            //execute visolate
            File workingDir = new File(appConfig.getWorkingDir()).getCanonicalFile();
            String inFile = appConfig.getGerberInputDir() + "/" + boardFileName + "-B_Cu.gbl";
            String outFile = boardFileName + "_etch.gcode";
            createGcode(inFile, outFile, workingDir, consoleOutput, appConfig);

            inFile = appConfig.getGerberInputDir() + "/" + boardFileName + "-Edge_Cuts.gbr";
            outFile = boardFileName + "_edge.gcode";
            createGcode(inFile, outFile, workingDir, consoleOutput, appConfig);


            inFile = appConfig.getGerberInputDir() + "/" + boardFileName + ".drl";
            outFile = boardFileName + "_drill.gcode";
            createGcode(inFile, outFile, workingDir, consoleOutput, appConfig);

            //execute the python script
            ProcessBuilder builder = new ProcessBuilder(appConfig.getPythonNumpy(),
                    "5_GenerateGcodeImage.py");
            builder.directory(workingDir);
            builder.redirectErrorStream(true);
            final Process p = builder.start();

            new PrintConsole(p.getErrorStream(), p.getInputStream(), consoleOutput).start();

            p.waitFor();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(GenerateVisolateGCode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createGcode(final String inFile, final String outFile, final File workingDir, final TextArea consoleOutput, final AppConfig appConfig) throws InterruptedException, IOException {
        File java3dLocation = new File(appConfig.getJava3dLocation()).getCanonicalFile();

        ProcessBuilder visolate = new ProcessBuilder("java",
                "-Djava.library.path=" + java3dLocation.getAbsolutePath(),
                "-jar",
                "Visolate.jar",
                "-auto",
                "-flip-x",
                "-outfile " + outFile,
                inFile);
        visolate.directory(workingDir);
        visolate.redirectErrorStream(true);
        final Process visolateProcess = visolate.start();
        new PrintConsole(visolateProcess.getErrorStream(), visolateProcess.getInputStream(), consoleOutput).start();
        visolateProcess.waitFor();

        File file = new File(appConfig.getWorkingDir() + "/utfile " + outFile).getCanonicalFile();
        File destFile = new File(appConfig.getGerberOutputDir() + "/" + outFile).getCanonicalFile();
        if (file.exists()) {
            if (destFile.exists()) {
                destFile.delete();
            }
            file.renameTo(new File(appConfig.getGerberOutputDir() + "/" + outFile).getCanonicalFile());
        }
    }

    private void deleteFile(final String fileName, final AppConfig appConfig) {
        try {
            File fileToBeDeleted = new File(appConfig.getGerberOutputDir() + "/" + fileName).getCanonicalFile();
            if (fileToBeDeleted.exists()) {
                fileToBeDeleted.delete();
            }
        } catch (IOException ex) {
            Logger.getLogger(GenerateVisolateGCode.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
