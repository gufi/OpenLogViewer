package org.diyefi.openlogviewer.eventlisteners;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.diyefi.openlogviewer.OpenLogViewer;

public class KeyboardFocusController implements PropertyChangeListener{
	public void propertyChange(PropertyChangeEvent e) {
		final OpenLogViewer appRef = OpenLogViewer.getInstance();
		final boolean goingFullscreen = appRef.isFullscreen();

		if(goingFullscreen){
			Component newComp = ((Component)e.getNewValue());
			if(newComp == null || !newComp.equals(appRef)){
				appRef.requestFocusInWindow();
			}
		}
	}
}
