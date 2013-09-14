/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Util {

    private static final String APPLICATION_PROPERTIES = "cycloneGui.properties";

    private Util() {
    }

    public static String getOnlyFilename(final String fullPathWithFile) {
        // Handle null case specially.
        if (fullPathWithFile == null) {
            return null;
        }

        // Get position of last '.'.
        int pos = fullPathWithFile.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.
        if (pos == -1) {
            return fullPathWithFile;
        }

        // Otherwise return the string, up to the dot.
        return fullPathWithFile.substring(0, pos);
    }

    public static String getOnlyDirectory(final String fullPathWithFile) {
        String result = "";
        if ((fullPathWithFile != null) && (!fullPathWithFile.trim().equals(""))) {
            File file = new File(fullPathWithFile);
            result = file.getParent();
        }
        return result;
    }

    public static void saveApplicationProperties(final Properties properties) {
        saveProperties(APPLICATION_PROPERTIES, properties);
    }

    public static void saveProperties(final String fileName, final Properties properties) {
        File file = new File(fileName);
        try {
            try (FileWriter fileWriter = new FileWriter(file)) {
                try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                    properties.store(bufferedWriter, null);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Properties readProperties(final String fileName) {
        Properties properties = new Properties();
        try {
            File file = new File(fileName).getCanonicalFile();
            //create the file in case it doesn't exist
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //the creation might have failed, only read the file if it is there
            if (file.exists()) {
                try {
                    try (FileReader fileReader = new FileReader(file)) {
                        try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                            properties.load(bufferedReader);
                        }
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }

    public static Properties readApplicationProperties() {
        return readProperties(APPLICATION_PROPERTIES);    }
}
