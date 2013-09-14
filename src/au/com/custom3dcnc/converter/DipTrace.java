/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.com.custom3dcnc.converter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class DipTrace extends ConverterBase {

    @Override
    public String execute(String fileName) throws IOException, FileNotFoundException {
        String line;
        String previousLine = "";
        StringBuilder builder = new StringBuilder();
        File file = new File(fileName);
        try (FileReader fileReader = new FileReader(file)) {
            try (BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                while ((line = bufferedReader.readLine()) != null) {
                    boolean startsWithX = line.startsWith("X");
                    boolean startsWithY = line.startsWith("Y");
                    boolean containsY = line.contains("Y");

                    if (line.equals("D3*")) {
                        //the gerber to gcode crashes when there is a D3
                        continue;
                    }
                    else if (startsWithX && containsY) {
                        //perfect line, just add it to the new file
                        builder.append(line).append("\n");
                        previousLine = line;
                    } else if ((!startsWithX) && (!containsY)) {
                        //probably not an X,Y coordinate, add it
                        builder.append(line).append("\n");
                    } else if (startsWithX && (!containsY)) {
                        //has X but no Y, get Y from previous line
                        String yValue = getYValue(previousLine);
                        String splitLine[] = line.split("D");
                        line = splitLine[0] + yValue + "D" + splitLine[1];
                        builder.append(line).append("\n");
                        previousLine = line;
                    } else if (startsWithY) {
                        String xValue = getXValue(previousLine);
                        line = xValue + line;
                        builder.append(line).append("\n");
                        previousLine = line;
                    }


                }
            }
        }

        return  builder.toString();
    }

    private String getYValue(String line) {
        int locationY = line.indexOf("Y");
        int locationD = line.indexOf("D");

        return line.substring(locationY, locationD);
    }

    private String getXValue(String line) {
        String splitLine[] = line.split("Y");
        return splitLine[0];
    }
}
