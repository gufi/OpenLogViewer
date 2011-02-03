/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datareader;

import Utils.Utilities;
import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Bryan
 */
public class CVSTypeFileFilter extends FileFilter {

    public CVSTypeFileFilter() {
        super();
    }

    @Override
    public String getDescription() {
        return "Compatable MegaSquirt DataLogs";
    }

    

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }
        String extension = Utilities.getExtension(f);
        if (extension.equals("log")) {
            return true;
        }

        //if nothing is satisfied return false
        return false;
    }
}


