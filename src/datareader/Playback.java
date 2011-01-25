/* DataReader
 *
 * Copyright 2011
 *
 * This file is part of the DataReader project.
 *
 * DataReader software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DataReader software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any DataReader software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
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
