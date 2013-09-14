/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc.converter;

public enum ConvertersEnum {
    DIPTRACE("DipTrace", new DipTrace());

    private String description;
    private ConverterBase converter;

    public static ConvertersEnum getDIPTRACE() {
        return DIPTRACE;
    }

    public String getDescription() {
        return description;
    }

    public ConverterBase getConverter() {
        return converter;
    }
    
    private ConvertersEnum(final String description, final ConverterBase converter) {
        this.description = description;
        this.converter = converter;
    }

}
