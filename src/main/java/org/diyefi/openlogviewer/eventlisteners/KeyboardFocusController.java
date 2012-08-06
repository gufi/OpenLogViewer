package org.diyefi.openlogviewer.eventlisteners;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.diyefi.openlogviewer.OpenLogViewer;

public class KeyboardFocusController implements PropertyChangeListener{
	public void propertyChange(PropertyChangeEvent e) {
		final boolean goingFullscreen = OpenLogViewer.getInstance().isFullscreen();
		final String appName = OpenLogViewer.APPLICATION_NAME;

		if(goingFullscreen){
			Component newComp = ((Component)e.getNewValue());
			if(newComp == null || !newComp.equals(appName)){
				OpenLogViewer.getInstance().requestFocusInWindow();
			}
		}
	}
}
