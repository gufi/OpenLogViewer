/* OpenLogViewer
 *
 * Copyright 2011
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

/*
 * OpenLogViewerApp.java
 *
 * Created on Jan 26, 2011, 2:55:31 PM
 */
package org.diyefi.openlogviewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.diyefi.openlogviewer.decoder.AbstractDecoder;
import org.diyefi.openlogviewer.decoder.CSVTypeLog;
import org.diyefi.openlogviewer.decoder.FreeEMSBin;
import org.diyefi.openlogviewer.filefilters.CSVFileFilter;
import org.diyefi.openlogviewer.filefilters.FreeEMSBinFileFilter;
import org.diyefi.openlogviewer.filefilters.FreeEMSLAFileFilter;
import org.diyefi.openlogviewer.filefilters.LogFileFilter;
import org.diyefi.openlogviewer.filefilters.MSTypeFileFilter;
import org.diyefi.openlogviewer.filefilters.FreeEMSFileFilter;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.graphing.EntireGraphingPanel;
import org.diyefi.openlogviewer.graphing.MultiGraphLayeredPane;
import org.diyefi.openlogviewer.optionpanel.OptionFrameV2;
import org.diyefi.openlogviewer.propertypanel.PropertiesPane;
import org.diyefi.openlogviewer.propertypanel.SingleProperty;
import org.diyefi.openlogviewer.subframes.AboutFrame;
import org.diyefi.openlogviewer.subframes.MacOSAboutHandler;
import org.diyefi.openlogviewer.utils.Utilities;

public final class OpenLogViewer extends JFrame {
	public static final String NEWLINE = System.getProperty(Keys.LINE_SEPARATOR);

	private static final long serialVersionUID = 1L;

	private static final Properties buildInfo = new Properties();

	private static final String APPLICATION_NAME = OpenLogViewer.class.getSimpleName();
	private static final String SETTINGS_DIRECTORY = "." + APPLICATION_NAME;

	private static final String GIT_DESCRIBE_KEY = "git.commit.id.describe";

	// TODO localise and refactor these:
	private static final String PROPERTIES_FILENAME = "OLVAllProperties.olv";

	private static final String NAME_OF_LAST_FILE_KEY = "lastFingFile";
	private static final String NAME_OF_LAST_DIR_KEY = "lastFingDir";
	private static final String NAME_OF_LAST_CHOOSER_CLASS = "chooserClass";

	// Real vars start here, many will probably get ripped out later
	private static final int GRAPH_PANEL_WIDTH = 600;
	private static final int GRAPH_PANEL_HEIGHT = 420;



	private static OpenLogViewer mainAppRef;
	private static ResourceBundle labels;

	private final String applicationTitle;
	private final String applicationVersion;
	private final EntireGraphingPanel graphingPanel;
	private final FooterPanel footerPanel;
	private final OptionFrameV2 optionFrame;
	private final PropertiesPane prefFrame;

	private final List<SingleProperty> properties;
	private AbstractDecoder decoderInUse;
        /**
         * Executor service requires a Runnable object it will begin on execute
         */
        private ExecutorService executorService;
	private final JMenuBar menuBar;
	private boolean fullscreen;

	private int extendedState;
	private Point location;
	private Dimension size;
	private int containingDevice;

	public OpenLogViewer() {
		try {
			buildInfo.loadFromXML(getClass().getClassLoader().getResourceAsStream("build/buildInfo.xml"));
		} catch (IOException e) {
			System.out.println("Uh oh, looks like a hacked copy! UNSUPPORTED VERSION! DO NOT USE!");
		} finally {
			String preliminaryVersion = buildInfo.getProperty(GIT_DESCRIBE_KEY);
			if (preliminaryVersion == null || "".equals(preliminaryVersion.trim())) {
				System.out.println("Application version not found! UNSUPPORTED VERSION! DO NOT USE!");
				applicationVersion = "UNSUPPORTED VERSION! DO NOT USE!";
				buildInfo.setProperty(GIT_DESCRIBE_KEY, applicationVersion);
			} else {
				applicationVersion = buildInfo.getProperty(GIT_DESCRIBE_KEY);
				buildInfo.setProperty(GIT_DESCRIBE_KEY, applicationVersion);
			}
		}
                /***
                 * for running the log parsers
                 */
                executorService = Executors.newSingleThreadExecutor();
                
		applicationTitle = APPLICATION_NAME + " " + applicationVersion;
		buildInfo.setProperty("application.title", applicationTitle);

		prefFrame = new PropertiesPane(labels, SETTINGS_DIRECTORY);
		properties = new ArrayList<SingleProperty>();
		prefFrame.setProperties(properties);

		footerPanel = new FooterPanel(labels);
                
                /***
                 * I think i'm going to redo the option frame and use better naming conventions as well
                 */
		optionFrame = new OptionFrameV2(labels);
		graphingPanel = new EntireGraphingPanel(labels);
		graphingPanel.setPreferredSize(new Dimension(GRAPH_PANEL_WIDTH, GRAPH_PANEL_HEIGHT));

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(applicationTitle);
		setLayout(new BorderLayout());
		setFocusable(true);

		final JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(graphingPanel, BorderLayout.CENTER);
		mainPanel.add(footerPanel, BorderLayout.SOUTH);
		add(mainPanel, BorderLayout.CENTER);

		final JMenuItem openFileMenuItem = new JMenuItem(labels.getString(Text.FILE_MENU_ITEM_OPEN_NAME));
		openFileMenuItem.setName(Text.FILE_MENU_ITEM_OPEN_NAME);
		openFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openChosenFile();
			}
		});

		final JMenuItem reloadFileMenuItem = new JMenuItem(labels.getString(Text.FILE_MENU_ITEM_RELOAD_NAME));
		reloadFileMenuItem.setName(Text.FILE_MENU_ITEM_RELOAD_NAME);
		reloadFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openLastFile();
			}
		});

		final JMenuItem quitFileMenuItem = new JMenuItem(labels.getString(Text.FILE_MENU_ITEM_QUIT_NAME));
		quitFileMenuItem.setName(Text.FILE_MENU_ITEM_QUIT_NAME);
		quitFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				OpenLogViewer.getInstance().quit();
			}
		});

		final JMenuItem fullScreenViewMenuItem = new JMenuItem(labels.getString(Text.VIEW_MENU_ITEM_FULL_SCREEN_NAME));
		fullScreenViewMenuItem.setName(Text.VIEW_MENU_ITEM_FULL_SCREEN_NAME);
		fullScreenViewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				enterFullScreen();
			}
		});

		final JMenuItem scaleAndColorViewMenuItem = new JMenuItem(labels.getString(Text.VIEW_MENU_ITEM_SCALE_AND_COLOR_NAME));
		scaleAndColorViewMenuItem.setName(Text.VIEW_MENU_ITEM_SCALE_AND_COLOR_NAME);
		scaleAndColorViewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				prefFrame.setVisible(true);
			}
		});

		final JMenuItem fieldsAndDivisionsViewMenuItem = new JMenuItem(labels.getString(Text.VIEW_MENU_ITEM_FIELDS_AND_DIVISIONS_NAME));
		fieldsAndDivisionsViewMenuItem.setName(Text.VIEW_MENU_ITEM_FIELDS_AND_DIVISIONS_NAME);
		fieldsAndDivisionsViewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				optionFrame.setVisible(true);
			}
		});

		final JMenuItem aboutMenuItem = new JMenuItem(labels.getString(Text.HELP_MENU_ITEM_ABOUT_NAME));
		aboutMenuItem.setName(Text.HELP_MENU_ITEM_ABOUT_NAME);
		aboutMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				AboutFrame.show(buildInfo);
			}
		});

		/*
		 * 13 January 2012 Had Chick-fil-A #1 meal with no pickle and Dr Pepper for lunch.
		 * 		Dr Pepper with no period. "Dude didn't even get his degree." Either that or he is British.
		 * 1 November 2011 Migrated Gufi's menu from the pointless class it's in to here.
		 * 22 October 2011 Left Gufi's menu in place for future dev's enjoyment.
		 *
		 * 5 February 2011 meal for the night DO NOT EDIT MENU!
		 * Sesame chicken alacarte
		 * chicken lo mein alacarte
		 * orange chicken x2
		 */

		final JMenu fileMenu = new JMenu(labels.getString(Text.FILE_MENU_NAME));
		fileMenu.setName(Text.FILE_MENU_NAME);
		fileMenu.add(openFileMenuItem);
		fileMenu.add(reloadFileMenuItem);
//		if (!IS_MAC_OS_X) {
			fileMenu.add(quitFileMenuItem);
	//	}

		final JMenu viewMenu = new JMenu(labels.getString(Text.VIEW_MENU_NAME));
		viewMenu.setName(Text.VIEW_MENU_NAME);
		viewMenu.add(fullScreenViewMenuItem);
		viewMenu.add(scaleAndColorViewMenuItem);
		viewMenu.add(fieldsAndDivisionsViewMenuItem);

		final JMenu helpMenu = new JMenu(labels.getString(Text.HELP_MENU_NAME));
		helpMenu.setName(Text.HELP_MENU_NAME);
		helpMenu.add(aboutMenuItem);

		menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		//if (IS_MAC_OS_X) {
		//	new MacOSAboutHandler(buildInfo);
		//} else {
			menuBar.add(helpMenu);
		//}
		setJMenuBar(menuBar);

		//Listener stuff
		addComponentListener(graphingPanel);
		setupWindowKeyBindings(this);

		setName(applicationTitle);

		pack();

		setVisible(true);
	}

	

	public void quit() {
		final WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	}

	public void openChosenFile() {
		final JFileChooser fileChooser = generateChooser();
		final int acceptValue = fileChooser.showOpenDialog(this);
		if (acceptValue == JFileChooser.APPROVE_OPTION) {
			final File fileToOpen = fileChooser.getSelectedFile();
			if (!openFile(fileToOpen, fileChooser)) {
				JOptionPane.showMessageDialog(mainAppRef, labels.getObject(Text.OPEN_FILE_ERROR_MESSAGE)
						+ NEWLINE + fileToOpen.getAbsolutePath(),
						labels.getString(Text.OPEN_FILE_ERROR_TITLE),
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void openLastFile() {
		final String lastFingFile = getApplicationWideProperty(NAME_OF_LAST_FILE_KEY);
		final String chooserClass = getApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);
		if (chooserClass != null && lastFingFile != null) {
			final File fileToOpen = new File(lastFingFile);
			final JFileChooser fileChooser = generateChooser();
			if (!openFile(fileToOpen, fileChooser)) {
				JOptionPane.showMessageDialog(mainAppRef, labels.getObject(Text.OPEN_LAST_FILE_ERROR_MESSAGE)
						+ NEWLINE + fileToOpen.getAbsolutePath(),
						labels.getString(Text.OPEN_LAST_FILE_ERROR_TITLE),
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(mainAppRef, labels.getObject(Text.OPEN_LAST_FILE_MISSING_PROPERTY_MESSAGE),
					labels.getString(Text.OPEN_LAST_FILE_MISSING_PROPERTY_TITLE),
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public boolean openFile(final File fileToOpen, final JFileChooser fileChooser) {
		if (fileToOpen.exists()) {
			if (decoderInUse != null) {
				// Clear out all references to data that we don't need and thereby ensure that we have lots of memory free for data we're about to gather!
				final GenericLog logInUse = decoderInUse.getDecodedLog();
				if (logInUse != null) {
					logInUse.clearOut(); // This is the wrong approach. The correct approach is to reuse the object, try that next...
				}
				decoderInUse = null;
				setLog(null);
			} // else haven't read in a log yet.

			setTitle(applicationTitle + " - " + fileToOpen.getName());
			saveApplicationWideProperty(NAME_OF_LAST_DIR_KEY, fileToOpen.getParent());
			saveApplicationWideProperty(NAME_OF_LAST_FILE_KEY, fileToOpen.getPath());
			saveApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS, fileChooser.getFileFilter().getClass().getCanonicalName());

			if (FileExtensions.BIN.equals(Utilities.getExtension(fileToOpen))
					|| FileExtensions.LA.equals(Utilities.getExtension(fileToOpen))
					|| (fileChooser.getFileFilter() instanceof FreeEMSFileFilter)) {
				decoderInUse = new FreeEMSBin(fileToOpen, labels);
			} else {
				decoderInUse = new CSVTypeLog(fileToOpen, labels);
			}
                        executorService.execute(decoderInUse);
                        // add listener
                        decoderInUse.addDecoderProgressLineOfListener(this.graphingPanel.getMultiGraphLayeredPane().getInfoPanel());
                        decoderInUse.addDecoderProgressListener(this.graphingPanel.getMultiGraphLayeredPane().getInfoPanel());
			return true;
		} else {
			setTitle(applicationTitle);
			return false;
		}
	}

	public JFileChooser generateChooser() {
		final JFileChooser fileChooser = new JFileChooser();
		final String lastFingFile = getApplicationWideProperty(NAME_OF_LAST_FILE_KEY);

		if (lastFingFile != null) {
			fileChooser.setSelectedFile(new File(lastFingFile));
		} else {
			final String lastFingDir = getApplicationWideProperty(NAME_OF_LAST_DIR_KEY);
			if (lastFingDir != null) {
				fileChooser.setCurrentDirectory(new File(lastFingDir));
			}
		}

		fileChooser.addChoosableFileFilter(new FreeEMSFileFilter(labels));
		fileChooser.addChoosableFileFilter(new FreeEMSBinFileFilter());
		fileChooser.addChoosableFileFilter(new FreeEMSLAFileFilter());
		fileChooser.addChoosableFileFilter(new CSVFileFilter());
		fileChooser.addChoosableFileFilter(new LogFileFilter());
		fileChooser.addChoosableFileFilter(new MSTypeFileFilter(labels));

		final String chooserClass = getApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);

		if (chooserClass != null) {
			try {
				final FileFilter[] existingFilters = fileChooser.getChoosableFileFilters();
				boolean alreadyHasSavedFilter = false;
				for (int i = 0; i < existingFilters.length; i++) {
					final String thisFilter = existingFilters[i].getClass().getCanonicalName();
					if (thisFilter.equals(chooserClass)) {
						alreadyHasSavedFilter = true;
						fileChooser.setFileFilter(existingFilters[i]); // If set to a new instance the list will contain two!
					}
				}

				// If it's not one of ours, create a new one and set it, though that almost certainly means we'll throw an exception and clean up the prefs...
				if (!alreadyHasSavedFilter) {
					final FileFilter savedFilter = (FileFilter) Class.forName(chooserClass).newInstance();
					fileChooser.setFileFilter(savedFilter);
				}
			} catch (ClassNotFoundException c) {
				removeApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);
				System.out.println(labels.getString(Text.CLASS_NOT_FOUND) + NAME_OF_LAST_CHOOSER_CLASS + labels.getString(Text.REMOVED_FROM_PROPS));
			} catch (InstantiationException i) {
				removeApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);
				System.out.println(labels.getString(Text.COULD_NOT_INSTANTIATE_CLASS) + NAME_OF_LAST_CHOOSER_CLASS + labels.getString(Text.REMOVED_FROM_PROPS));
			} catch (IllegalAccessException l) {
				removeApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);
				System.out.println(labels.getString(Text.COULD_NOT_ACCESS_CLASS) + NAME_OF_LAST_CHOOSER_CLASS + labels.getString(Text.REMOVED_FROM_PROPS));
			}
		}
		return fileChooser;
	}

	private String getApplicationWideProperty(final String key) {
		final Properties appWide = new Properties();
		openAppWideProps(appWide);
		return appWide.getProperty(key);
	}

	private void saveApplicationWideProperty(final String key, final String value) {
		FileOutputStream fos = null;
		try {
			final Properties appWide = new Properties();
			final File appWideFile = openAppWideProps(appWide);
			appWide.setProperty(key, value);
			fos = new FileOutputStream(appWideFile);
			appWide.store(fos, "saved");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(labels.getString(Text.IO_ISSUE_SAVING_PROPERTY) + e.getMessage(), e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private void removeApplicationWideProperty(final String key) {
		FileOutputStream fos = null;
		try {
			final Properties appWide = new Properties();
			final File appWideFile = openAppWideProps(appWide);
			appWide.remove(key);
			fos = new FileOutputStream(appWideFile);
			appWide.store(fos, "removed");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(labels.getString(Text.IO_ISSUE_REMOVING_PROPERTY) + e.getMessage(), e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private File openAppWideProps(final Properties appWide) {
		File appWideFile;
		appWideFile = new File(System.getProperty(Keys.USER_HOME));

		if (!appWideFile.exists() || !appWideFile.canRead() || !appWideFile.canWrite()) {
			System.out.println(labels.getString(Text.HOME_DIRECTORY_NOT_ACCESSIBLE));
		} else {
			appWideFile = new File(appWideFile, SETTINGS_DIRECTORY);
		}

		if (!appWideFile.exists()) {
			FileInputStream fis = null;
			try {
				if (appWideFile.mkdir()) {
					appWideFile = new File(appWideFile, PROPERTIES_FILENAME);
					if (appWideFile.createNewFile()) {
						fis = new FileInputStream(appWideFile);
						appWide.load(fis);
					}
				} else {
					throw new RuntimeException(labels.getString(Text.FAILED_TO_CREATE_DIRECTORY_MESSAGE));
					// This should be passed up to the GUI as a dialog that tells you it can't do what it has to be able to...
				}
			} catch (IOException e) {
				System.out.print(e.getMessage());
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		} else {
			appWideFile = new File(appWideFile, PROPERTIES_FILENAME);
			FileInputStream fis = null;
			try {
				if (!appWideFile.createNewFile()) {
					fis = new FileInputStream(appWideFile);
					appWide.load(fis);
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					if (fis != null) {
						fis.close();
					}
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
		return appWideFile;
	}

	public static void setupWindowKeyBindings(final JFrame window) {
		final Action closeWindow = new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(final ActionEvent e) {
				final WindowEvent wev = new WindowEvent(window, WindowEvent.WINDOW_CLOSING);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
			}
		};

		boolean isMainApp = false;
		if (window instanceof OpenLogViewer) {
			isMainApp = true;
		}

		// Close any window
		//if (IS_WINDOWS || IS_LINUX) {
			window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Keys.CONTROL_W), Keys.CLOSE_WINDOW);
		//} else if (IS_MAC_OS_X) {
		//	window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Keys.COMMAND_W), Keys.CLOSE_WINDOW);
		//}

		// Just close the main app window
		//if (IS_LINUX && isMainApp) {
		//	window.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(Keys.CONTROL_Q), Keys.CLOSE_WINDOW);
		//}

		window.getRootPane().getActionMap().put(Keys.CLOSE_WINDOW, closeWindow);
	}

	public void enterFullScreen() {
		if (!fullscreen) {
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final GraphicsDevice[] device = ge.getScreenDevices();

			for (int i = 0; i < device.length; i++) { // Cycle through available devices (monitors) looking for device that has center of app
				final Rectangle bounds = device[i].getDefaultConfiguration().getBounds();
				final int centerX = (int) Math.round(getBounds().getCenterX());
				final int centerY = (int) Math.round(getBounds().getCenterY());
				final Point center = new Point(centerX, centerY);
				if (bounds.contains(center)) { // Found the device (monitor) that contains the center of the app
					containingDevice = i;
					if (device[containingDevice].isFullScreenSupported()) {
						try {
							fullscreen = true;      // Remember so that we can react accordingly.
							saveScreenState();      // Save the current state of things to restore later when exiting fullscreen mode.
							setVisible(false);      // Hide how the sausage is made!
							setJMenuBar(null);      // Remove the menu bar for maximum space, load files with the buttons?
							dispose();              // Make the JFrame undisplayable so setUndecorated(true) will work!
							setUndecorated(true);   // Remove the window frame/bezel!
							setVisible(true);       // Make the JFrame displayable again!
//							setResizable(false);    // Fred: doesn't make sense and could be dangerous, according to oracle.
							                        // Ben: Removed setResizable(false) because it causes GNOME menu bar
							                        // and GNOME task bar to show in front of the app!
							device[containingDevice].setFullScreenWindow(this);
							validate();             // Required after rearranging component hierarchy
							toFront();              // Might as well
							requestFocusInWindow(); // Put keyboard focus here so toggling fullscreen works
							graphingPanel.moveGraphDueToResize(); // Done so centering still works on Mac
						} catch (IllegalComponentStateException e) {
							e.printStackTrace();
							System.out.println(labels.getString(Text.FAILED_TO_GO_FULLSCREEN_MESSAGE));
							fullscreen = false;
						}
					} else {
						System.out.println(labels.getString(Text.CANT_GO_FULLSCREEN_MESSAGE));
					}
				}
			}
		}
	}

	public void exitFullScreen() {
		if (fullscreen) {
			fullscreen = false;
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final GraphicsDevice[] device = ge.getScreenDevices();
			// Do the reverse of what we did to put it into full screen!
			device[containingDevice].setFullScreenWindow(null); // Exit full screen
			dispose();              // Make the JFrame undisplayable so setUndecorated(false) will work
			setUndecorated(false);  // Restore the window frame/bezel
			setJMenuBar(menuBar);   // Remove the menu bar
			validate();             // Required after rearranging component hierarchy
			restoreScreenState();   // Size and place the window where it was before
			setVisible(true);       // Make the JFrame displayable again
			requestFocusInWindow(); // Put keyboard focus here so toggling fullscreen works
			graphingPanel.moveGraphDueToResize(); // Done so centering still works on Mac
		}
	}

	public void toggleFullScreen() {
		if (fullscreen) {
			exitFullScreen();
		} else {
			enterFullScreen();
		}
	}

	private void saveScreenState() {
		extendedState = getExtendedState();
		location = getLocation();
		size = getSize();
	}

	private void restoreScreenState() {
		setExtendedState(extendedState);
		setLocation(location);
		setSize(size);
	}

	public void setLog(final GenericLog genericLog) {
		graphingPanel.setLog(genericLog);
	}

	public void defaultBrowserNotFound() {
		final Object message = labels.getObject(Text.DEFAULT_BROWSER_ERROR_MESSAGE);
		final String title = labels.getString(Text.DEFAULT_BROWSER_ERROR_TITLE);
		JOptionPane.showMessageDialog(mainAppRef, message, title, JOptionPane.ERROR_MESSAGE); // DIRTY
	}

	// All of the references below are indicators of bad design, marking with DIRTY:

	/**
	 * Returns the reference to this instance, it is meant to be a method to make getting the main frame simpler
	 * @return <code>this</code> instance
	 */
	public static OpenLogViewer getInstance() {
		return mainAppRef;
	}

	public NavBarPanel getNavBarPanel() {
		return footerPanel.getNavBarPanel();
	}

	public EntireGraphingPanel getEntireGraphingPanel() {
		return graphingPanel;
	}

	public MultiGraphLayeredPane getMultiGraphLayeredPane() {
		return graphingPanel.getMultiGraphLayeredPane();
	}

	public OptionFrameV2 getOptionFrame() {
		return optionFrame;
	}

	public PropertiesPane getPropertyPane() {
		return prefFrame;
	}

	public List<SingleProperty> getProperties() {
		return properties;
	}
}
