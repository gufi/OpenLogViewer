/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.diyefi.openlogviewer.decoder;

/**
 *
 * @author Bryan
 */
public interface DecoderProgressListener {
    public void onProgressChanged(int percentage);
    public void onProgressLinesOf(int line, int total);
}
