package org.diyefi.openlogviewer.subframes;

import java.util.Properties;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.Application;

public class MacOSAboutHandler {

	private final Properties buildInfo;

	public MacOSAboutHandler(final Properties buildInfo) {
		this.buildInfo = buildInfo;
		final AboutHandler handler = new AboutBoxHandler();
		Application.getApplication().setAboutHandler(handler);
	}

	class AboutBoxHandler implements AboutHandler {
		@Override
		public void handleAbout(final AboutEvent e) {
			AboutFrame.show(buildInfo);
		}
	}
}
