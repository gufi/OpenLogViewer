package org.diyefi.openlogviewer.eventlisteners;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class KeyboardFocusController implements PropertyChangeListener{
	public void propertyChange(PropertyChangeEvent e) {
        Component oldComp = (Component)e.getOldValue();
        //Component newComp = (Component)e.getNewValue();

        if ("focusOwner".equals(e.getPropertyName())) {
            if (oldComp == null) {
                // the newComp component gained the focus
            	//System.out.println("The newComp component gained the focus.");
            } else {
                // the oldComp component lost the focus
            	//System.out.println("The oldComp component lost the focus.");
            }
        } else if ("focusedWindow".equals(e.getPropertyName())) {
            if (oldComp == null) {
                // the newComp window gained the focus
            	//System.out.println("The newComp window gained the focus.");
            } else {
                // the oldComp window lost the focus
            	//System.out.println("The oldComp window lost the focus.");
            }
        }
	}
}
