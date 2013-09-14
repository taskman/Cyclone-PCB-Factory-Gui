/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc;

public enum ProgressBarEnum {
    NONE("No progress", 0),
    GERBER_2_GCODE("Gerber2Gcode", 0.17),
    Z_PROBE("ZProbe", 0.25),
    ETCH_DATA("Send ech data", 0.465),
    ETCH_DATA_2("Send ech data 2", 0.575),
    ETCH_DATA_3("Send ech data 3", 0.757),
    DRILL_DATA("Drill data", 0.885),
    EDGE_DATA("Edge data", 1);

    private String description;
    private double progress;

    public String getDescription() {
        return description;
    }

    public double getProgress() {
        return progress;
    }

    private ProgressBarEnum(final String description, final double progress) {
        this.description = description;
        this.progress = progress;
    }

}
