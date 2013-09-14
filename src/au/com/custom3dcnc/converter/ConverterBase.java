/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.custom3dcnc.converter;

import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class ConverterBase {
    public abstract String execute(String fileName) throws IOException, FileNotFoundException;
}
