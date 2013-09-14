/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc;

public enum SendActionEnum {
    SHOW_ETCH("showEtch", "_etch.png", "_etch_incomplete.png"),
    SHOW_ETCH_2("showEtch2", "_etch2.png", "_etch2_incomplete.png"),
    SHOW_ETCH_3("showEtch3", "_etch3.png", "_etch3_incomplete.png"),
    SHOW_DRILL("showDrill", "_drill.png", "_drill_incomplete.png"),
    SHOW_EDGE("showEdge", "_edge.png", "_edge_incomplete.png");

    private String description;
    private String fileName;
    private String incompleteFileName;

    public String getFileName() {
        return fileName;
    }

    public String getIncompleteFileName() {
        return incompleteFileName;
    }

    public String getDescription() {
        return description;
    }

    private SendActionEnum(final String description, final String fileName, final String incompleteFileName) {
        this.description = description;
        this.fileName = fileName;
        this.incompleteFileName = incompleteFileName;
    }

}
