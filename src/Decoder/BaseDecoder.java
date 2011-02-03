/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Decoder;

import GenericLog.GenericLog;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Bryan
 */
public abstract class BaseDecoder implements Runnable {

    private File logFile;
    private GenericLog decodedLog;
    private Thread t;

    @Override
    public void run() {
        try {
            this.getDecodedLog().setLogStatus(GenericLog.LOG_LOADING);
            decodeLog();
            this.getDecodedLog().setLogStatus(GenericLog.LOG_LOADED);
        } catch (IOException IOE) {
            this.getDecodedLog().setLogStatus(GenericLog.LOG_NOT_LOADED);
            System.out.println(IOE.getMessage());
        }
    }

    abstract void decodeLog() throws IOException;

    public GenericLog getDecodedLog() {
        return decodedLog;
    }

    public void setDecodedLog(GenericLog decodedLog) {
        this.decodedLog = decodedLog;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }



}
