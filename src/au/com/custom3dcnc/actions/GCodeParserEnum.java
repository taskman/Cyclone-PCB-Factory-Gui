/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc.actions;

import au.com.custom3dcnc.*;

public enum GCodeParserEnum {
    PYTHON_PARSER("pyGerber2Gcode_CUI", GenerateGCode.getInstance()),
    VISOLATE_PARSER("Visolate", GenerateVisolateGCode.getInstance());

    private String description;
    private GenerateGCode generateGCodeParser;

    public String getDescription() {
        return description;
    }

    public GenerateGCode getGenerateGCodeParser() {
        return generateGCodeParser;
    }

    private GCodeParserEnum(final String description, final GenerateGCode generateGCodeParser) {
        this.description = description;
        this.generateGCodeParser = generateGCodeParser;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }

}
