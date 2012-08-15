/* OpenLogViewer
 *
 * Copyright 2012
 *
 * This file is part of the OpenLogViewer project.
 *
 * OpenLogViewer software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenLogViewer software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any OpenLogViewer software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */
package org.diyefi.openlogviewer.subframes;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.diyefi.openlogviewer.OpenLogViewer;
import org.diyefi.openlogviewer.utils.MathUtils;

public final class AboutFrame extends JFrame {
	private static final double bytesPerMegaByte = 1048576;
	private static final long serialVersionUID = 1L;
	private static final int FRAME_WIDTH = 450;
	private static final int SPACER_HEIGHT = 20;
	private static final String LINK_CLOSE = "</a>";

	private final Properties buildInfo;

	private static final HyperlinkListener HLL = new HyperlinkListener() {
		public void hyperlinkUpdate(final HyperlinkEvent hle) {
			if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
				try {
					java.awt.Desktop.getDesktop().browse(java.net.URI.create(hle.getDescription()));
				} catch (IOException e) {
					OpenLogViewer.getInstance().defaultBrowserNotFound();
				}
			}
		}
	};

	private static AboutFrame aboutFrame; // Must not be changed outside of synchronized blocks

	/**
	 * Shows the about box. If the box is already open, just hidden, it is simply raised.
	 * If the about box is not open, then it has been disposed of and is created again.
	 *
	 * @param buildInfo the properties from which to retrieve build details.
	 */
	public static void show(final Properties buildInfo) {
		synchronized(HLL) { // Mutex must not change, however it doesn't matter what it is, so we reuse this.
			if (aboutFrame != null) {
				aboutFrame.setVisible(true);
			} else {
				aboutFrame = new AboutFrame(buildInfo);
			}
		}
	}

	private AboutFrame(final Properties buildInfo) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		this.buildInfo = buildInfo;
		final BorderLayout lm = new BorderLayout();
		lm.setHgap(SPACER_HEIGHT);
		setLayout(lm);

		OpenLogViewer.setupWindowKeyBindings(this);

		final JPanel north = createNorthPanel();
		final JPanel center = createCenterPanel();
		final JPanel south = createSouthPanel();
		add(north, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(south, BorderLayout.SOUTH);

		setTitle("About - " + buildInfo.getProperty("application.title"));

		pack(); // Gets insets to be correct

		final Insets insets = getInsets();
		final int width = FRAME_WIDTH + insets.left + insets.right;
		setPreferredSize(new Dimension(width, 0)); // Set the width so that component heights are correct

		final int height = north.getHeight() + center.getHeight() + south.getHeight() + insets.top + insets.bottom;
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));

		pack(); // Makes the window be the size we want.

		setVisible(true);
	}

	private JPanel createNorthPanel() {
		final String appName = OpenLogViewer.class.getSimpleName();
		final JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));

		final ImageIcon icon = createImageIcon("logo64x64.png", appName + " Logo");
		final JLabel logo = new JLabel(icon);
		logo.setAlignmentX(Component.CENTER_ALIGNMENT);
		northPanel.add(logo);

		addTextToPanel(northPanel, appName);
		addTextToPanel(northPanel, "Version: " + buildInfo.getProperty("project.version"));
		addTextToPanel(northPanel, "Detailed version: " + buildInfo.getProperty("git.commit.id.describe"));
		addTextToPanel(northPanel, "Git SHA1 hash: " + buildInfo.getProperty("git.commit.id"));

		return northPanel;
	}

	private JPanel createCenterPanel() {
		final JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

		final JTextPane description = new JTextPane();
		final SimpleAttributeSet simpleAttrib = new SimpleAttributeSet();
		StyleConstants.setAlignment(simpleAttrib, StyleConstants.ALIGN_JUSTIFIED);
		description.setParagraphAttributes(simpleAttrib, false);

		description.setText(buildInfo.getProperty("project.description"));
		description.setEditable(false);
		description.setOpaque(false);
		description.setBorder(BorderFactory.createEmptyBorder(SPACER_HEIGHT, SPACER_HEIGHT, SPACER_HEIGHT, SPACER_HEIGHT));
		description.setAlignmentX(Component.CENTER_ALIGNMENT);
		description.setSize(new Dimension(FRAME_WIDTH, Integer.MAX_VALUE)); // Must be set, or height shows up as one line (+other components) for the center panel
		centerPanel.add(description);

		final String website = buildInfo.getProperty("project.url");
		final String forumUrl = buildInfo.getProperty("project.forum.url");
		final String issuesUrl = buildInfo.getProperty("project.issueManagement.url");
		addTextToPanel(centerPanel, "Website: <a href='" + website + "'>" + website + LINK_CLOSE);
		addTextToPanel(centerPanel, "Issues: <a href='" + issuesUrl + "'>" + issuesUrl + LINK_CLOSE);
		addTextToPanel(centerPanel, "Support: <a href='" + forumUrl + "'>" + forumUrl + LINK_CLOSE);

		addTextToPanel(centerPanel, "Licensed under the <a href='http://www.gnu.org/licenses/quick-guide-gplv3.html'>GNU General Public License (GPL) V3</a>");

		centerPanel.add(Box.createVerticalStrut(SPACER_HEIGHT));

		return centerPanel;
	}

	private JPanel createSouthPanel() {
		final JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));

		addTextToPanel(southPanel, "Built by " + buildInfo.getProperty("git.build.user.name") + " using <a href='http://maven.apache.org/'>Maven " + buildInfo.getProperty("maven.version") + LINK_CLOSE);
		addTextToPanel(southPanel, "Built on " + buildInfo.getProperty("os.name") + " " + buildInfo.getProperty("os.arch") + " " + buildInfo.getProperty("os.version"));
		addTextToPanel(southPanel, "Built using Java " + buildInfo.getProperty("java.version") + " on a " + buildInfo.getProperty("java.vm.version") + " VM");
		addTextToPanel(southPanel, "Build date and time: " + buildInfo.getProperty("git.build.time"));

		southPanel.add(Box.createVerticalStrut(SPACER_HEIGHT));

		final Properties sys = System.getProperties();
		addTextToPanel(southPanel, "Running on " + sys.getProperty("os.name") + " " + sys.getProperty("os.arch") + " " + sys.getProperty("os.version"));
		addTextToPanel(southPanel, "Running using Java " + sys.getProperty("java.version") + " on a " + sys.getProperty("java.vm.version") + " VM");
		final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy @ HH:mm:ss z", Locale.ENGLISH);
		addTextToPanel(southPanel, "Current date and time: " + dateFormat.format(new Date()));

		southPanel.add(Box.createVerticalStrut(SPACER_HEIGHT));

		final Runtime ourRuntime = Runtime.getRuntime();
		final double limitMax = ourRuntime.maxMemory() / bytesPerMegaByte;
		final double currentMax = ourRuntime.totalMemory() / bytesPerMegaByte;
		final double currentFree = ourRuntime.freeMemory() / bytesPerMegaByte;
		final double futureFree = limitMax - currentMax;
		final double limitFree = currentFree + futureFree;
		final double used = currentMax - currentFree;

		addTextToPanel(southPanel, "Memory usage details: "
				+ "Used: "           // How much memory the application is actually using
				+ MathUtils.roundDecimalPlaces(used, 2)
				+ "MB");
		addTextToPanel(southPanel, "Current free/max: " // Instantaneous memory that the JVM can put new objects in
				+ MathUtils.roundDecimalPlaces(currentFree, 2)
				+ "MB/"  // Current maximum memory that the JVM exposes for use
				+ MathUtils.roundDecimalPlaces(currentMax, 2)
				+ "MB");
		addTextToPanel(southPanel, "Hard limit free/max: "   // Absolute maximum memory that the JVM can put new objects in
				+ MathUtils.roundDecimalPlaces(limitFree, 2)
				+ "MB/"    // Absolute total maximum memory that the JVM can allocate for use
				+ MathUtils.roundDecimalPlaces(limitMax, 2)
				+ "MB");

		return southPanel;
	}

	private void addTextToPanel(final JPanel panel, final String text) {
		final Font font = UIManager.getFont("Label.font");
		final StringBuilder bodyRule = new StringBuilder("body { font-family: ");
		bodyRule.append(font.getFamily());
		bodyRule.append("; font-size: ");
		bodyRule.append(font.getSize());
		bodyRule.append("pt; text-align: center;}");
		final JEditorPane newPane = new JEditorPane("text/html", text);
		((javax.swing.text.html.HTMLDocument) newPane.getDocument()).getStyleSheet().addRule(bodyRule.toString());
		newPane.addHyperlinkListener(HLL);
		newPane.setEditable(false);
		newPane.setBorder(null);
		newPane.setOpaque(false);
		newPane.setAlignmentX(Component.CENTER_ALIGNMENT);

		panel.add(newPane);
	}

	private ImageIcon createImageIcon(final String path, final String description) {
		final java.net.URL imgURL = OpenLogViewer.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	// If closing, free resources
	@Override
	public void dispose() {
		synchronized (HLL) {
			aboutFrame = null;
			super.dispose();
		}
	}
}
