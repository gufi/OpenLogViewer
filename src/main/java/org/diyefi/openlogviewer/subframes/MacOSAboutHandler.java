package org.diyefi.openlogviewer.subframes;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.Application;

public class MacOSAboutHandler {

	private final AboutFrame aboutFrame;

	public MacOSAboutHandler(final AboutFrame newAboutFrame) {
		aboutFrame = newAboutFrame;
		final AboutHandler handler = new AboutBoxHandler();
		Application.getApplication().setAboutHandler(handler);
	}

	class AboutBoxHandler implements AboutHandler {
		@Override
		public void handleAbout(final AboutEvent e) {
			aboutFrame.setVisible(true);
		}
	}
}
