/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datareader;

import java.io.*;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Bryan
 */
public class DLRFileFilter extends FileFilter {
    public DLRFileFilter () {
        super();
    }

    @Override
    public String getDescription() {
       return "FreeEMSBinary Logs";
    }
    private String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }


    @Override
    public boolean accept(File f) {
        if(f.isDirectory()) {
            return true;
        }
        String extension = getExtension(f);
        if( extension.equals("bin")) return true;

        //if nothing is satisfied return false
        return false;
    }

}
