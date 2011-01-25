/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package datareader;

import org.jdesktop.application.FrameView;

/**
 *  DataReaderApp a =   DataReaderApp.getApplication();
 *  DataReaderView v = (DataReaderView)a.getMainView();
 *  v.GraphWindow; // THIS IS HOW YOU CAN ACCESS PUBLIC VARIABLES VIA STATIC FUNCTION
 */

/**
 *
 * @author Bryan
 */
public class Playback {
   private DataReaderView v;

    public Playback( DataReaderView t) {
        v = t;
    }



  public void Play() {

      v.setStatusText("Play");

    
  }
  public void Stop() {
    v.setStatusText("Stop");
  }

  public void Pause() {
    v.setStatusText("Pause");
  }

  public void Faster() {
    v.setStatusText("Faster");
  }

  public void Slower() {
    v.setStatusText("Slower");
  }

  public void Eject() {
    v.setStatusText("Eject");
  }

}
