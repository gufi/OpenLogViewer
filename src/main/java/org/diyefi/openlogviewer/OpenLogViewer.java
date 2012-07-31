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
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
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

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import org.diyefi.openlogviewer.decoder.AbstractDecoder;
import org.diyefi.openlogviewer.decoder.CSVTypeLog;
import org.diyefi.openlogviewer.decoder.FreeEMSBin;
import org.diyefi.openlogviewer.eventlisteners.KeyboardFocusController;
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
import org.diyefi.openlogviewer.utils.Utilities;

public final class OpenLogViewer extends JFrame {
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String APP_NAME = "OpenLogViewer";

	private static final long serialVersionUID = 1L;

	private static final String APPLICATION_NAME = OpenLogViewer.class.getSimpleName();
	private static final String SETTINGS_DIRECTORY = "." + APPLICATION_NAME;

	private static final String FILE_MENU_KEY = "FileMenuName";
	private static final String FILE_MENU_ITEM_OPEN_KEY = "FileMenuItemOpenName";
	private static final String FILE_MENU_ITEM_RELOAD_KEY = "FileMenuItemReloadName";
	private static final String FILE_MENU_ITEM_QUIT_KEY = "FileMenuItemQuitName";

	private static final String VIEW_MENU_KEY = "ViewMenuName";
	private static final String VIEW_MENU_ITEM_FULL_SCREEN_KEY = "ViewMenuItemFullScreenName";
	private static final String VIEW_MENU_ITEM_SCALE_AND_COLOR_KEY = "ViewMenuItemScaleAndColorName";
	private static final String VIEW_MENU_ITEM_FIELDS_AND_DIVISIONS_KEY = "ViewMenuItemFieldsAndDivisionsName";

	private static final String FAILED_TO_GO_FULLSCREEN_MESSAGE_KEY = "FailedToGoFullScreenMessage";
	private static final String CANT_GO_FULLSCREEN_MESSAGE_KEY = "CantGoFullScreenMessage";

	private static final String OPEN_FILE_ERROR_TITLE_KEY = "OpenFileErrorTitle";
	private static final String OPEN_FILE_ERROR_MESSAGE_KEY = "OpenFileErrorMessage";
	private static final String OPEN_LAST_FILE_ERROR_TITLE_KEY = "OpenLastFileErrorTitle";
	private static final String OPEN_LAST_FILE_ERROR_MESSAGE_KEY = "OpenLastFileErrorMessage";

	// TODO localise and refactor these:
	private static final String PROPERTIES_FILENAME = "OLVAllProperties.olv";

	private static final String NAME_OF_LAST_FILE_KEY = "lastFingFile";
	private static final String NAME_OF_LAST_DIR_KEY = "lastFingDir";
	private static final String NAME_OF_LAST_CHOOSER_CLASS = "chooserClass";

	// Real vars start here, many will probably get ripped out later
	private static OpenLogViewer mainAppRef;
	private static ResourceBundle labels;

	private JPanel mainPanel;
	private EntireGraphingPanel graphingPanel;
	private FooterPanel footerPanel;
	private OptionFrameV2 optionFrame;
	private PropertiesPane prefFrame;
	private KeyboardFocusController keyboardFocusController;

	private List<SingleProperty> properties;
	private AbstractDecoder decoderInUse;
	private JMenuBar menuBar;
	private boolean fullscreen;

	private int extendedState;
	private Point location;
	private Dimension size;
	private int containingDevice;

	public OpenLogViewer() {

		prefFrame = new PropertiesPane(labels.getString(VIEW_MENU_ITEM_SCALE_AND_COLOR_KEY));
		properties = new ArrayList<SingleProperty>();
		prefFrame.setProperties(properties);

		footerPanel = new FooterPanel();
		optionFrame = new OptionFrameV2();
		graphingPanel = new EntireGraphingPanel();
		graphingPanel.setPreferredSize(new Dimension(600, 420));

		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle(APPLICATION_NAME);
		this.setLayout(new BorderLayout());
		this.setFocusable(true);

		mainPanel = new JPanel();
		mainPanel.setName("mainPanel");
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(graphingPanel, BorderLayout.CENTER);
		mainPanel.add(footerPanel, BorderLayout.SOUTH);
		this.add(mainPanel, BorderLayout.CENTER);

		keyboardFocusController = new KeyboardFocusController();

		final JMenuItem openFileMenuItem = new JMenuItem(labels.getString(FILE_MENU_ITEM_OPEN_KEY));
		openFileMenuItem.setName(FILE_MENU_ITEM_OPEN_KEY);
		openFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openFile();
			}
		});

		final JMenuItem reloadFileMenuItem = new JMenuItem(labels.getString(FILE_MENU_ITEM_RELOAD_KEY));
		reloadFileMenuItem.setName(FILE_MENU_ITEM_RELOAD_KEY);
		reloadFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				openLastFile();
			}
		});

		final JMenuItem quitFileMenuItem = new JMenuItem(labels.getString(FILE_MENU_ITEM_QUIT_KEY));
		quitFileMenuItem.setName(FILE_MENU_ITEM_QUIT_KEY);
		quitFileMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				final WindowEvent wev = new WindowEvent(OpenLogViewer.getInstance(), WindowEvent.WINDOW_CLOSING);
				Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
			}
		});

		final JMenuItem fullScreenViewMenuItem = new JMenuItem(labels.getString(VIEW_MENU_ITEM_FULL_SCREEN_KEY));
		fullScreenViewMenuItem.setName(VIEW_MENU_ITEM_FULL_SCREEN_KEY);
		fullScreenViewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				enterFullScreen();
			}
		});

		final JMenuItem scaleAndColorViewMenuItem = new JMenuItem(labels.getString(VIEW_MENU_ITEM_SCALE_AND_COLOR_KEY));
		scaleAndColorViewMenuItem.setName(VIEW_MENU_ITEM_SCALE_AND_COLOR_KEY);
		scaleAndColorViewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				prefFrame.setVisible(true);
				prefFrame.setAlwaysOnTop(true);  //Used to bring panel to top
				prefFrame.setAlwaysOnTop(false);
			}
		});

		final JMenuItem fieldsAndDivisionsViewMenuItem = new JMenuItem(labels.getString(VIEW_MENU_ITEM_FIELDS_AND_DIVISIONS_KEY));
		fieldsAndDivisionsViewMenuItem.setName(VIEW_MENU_ITEM_FIELDS_AND_DIVISIONS_KEY);
		fieldsAndDivisionsViewMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				optionFrame.setVisible(true);
				optionFrame.setAlwaysOnTop(true); //Used to bring panel to top
				optionFrame.setAlwaysOnTop(false);
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

		final JMenu fileMenu = new JMenu(labels.getString(FILE_MENU_KEY));
		fileMenu.setName(FILE_MENU_KEY);
		fileMenu.add(openFileMenuItem);
		fileMenu.add(reloadFileMenuItem);
		fileMenu.add(quitFileMenuItem);

		final JMenu viewMenu = new JMenu(labels.getString(VIEW_MENU_KEY));
		viewMenu.setName(VIEW_MENU_KEY);
		viewMenu.add(fullScreenViewMenuItem);
		viewMenu.add(scaleAndColorViewMenuItem);
		viewMenu.add(fieldsAndDivisionsViewMenuItem);

		menuBar = new JMenuBar();
		menuBar.setName("menuBar");
		menuBar.add(fileMenu);
		menuBar.add(viewMenu);
		this.setJMenuBar(menuBar);

		this.addKeyListener(graphingPanel);
		this.addComponentListener(graphingPanel);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener(keyboardFocusController);

		this.pack();
		this.setName(APP_NAME);
		this.requestFocusInWindow();
		this.setVisible(true);
	}

	/**
	 * The entry point of OLV!
	 *
	 * @param args the command line arguments
	 */
	public static void main(final String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final Locale currentLocale = Locale.getDefault();
				labels = ResourceBundle.getBundle(this.getClass().getPackage().getName() + ".Labels", currentLocale);

				final String lookAndFeel;
				final String systemLookAndFeel = UIManager.getSystemLookAndFeelClassName();
				if ("com.apple.laf.AquaLookAndFeel".equals(systemLookAndFeel)) { // If Mac!
					System.setProperty("apple.laf.useScreenMenuBar", "true");
				}
				lookAndFeel = systemLookAndFeel;

				try {
					UIManager.setLookAndFeel(lookAndFeel);
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
					System.out.println(labels.getString("LookAndFeelExceptionMessageOne"));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.out.println(labels.getString("LookAndFeelExceptionMessageTwo"));
				} catch (InstantiationException e) {
					e.printStackTrace();
					System.out.println(labels.getString("LookAndFeelExceptionMessageThree"));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					System.out.println(labels.getString("LookAndFeelExceptionMessageFour"));
				}

				mainAppRef = new OpenLogViewer();
			}
		});
	}

	public void openFile() {
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

		fileChooser.addChoosableFileFilter(new FreeEMSFileFilter());
		fileChooser.addChoosableFileFilter(new FreeEMSBinFileFilter());
		fileChooser.addChoosableFileFilter(new FreeEMSLAFileFilter());
		fileChooser.addChoosableFileFilter(new CSVFileFilter());
		fileChooser.addChoosableFileFilter(new LogFileFilter());
		fileChooser.addChoosableFileFilter(new MSTypeFileFilter());

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
				System.out.println("Class not found! chooserClass removed from props!");
			} catch (InstantiationException i) {
				removeApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);
				System.out.println("Could not instantiate class! chooserClass removed from props!");
			} catch (IllegalAccessException l) {
				removeApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS);
				System.out.println("Could not access class! chooserClass removed from props!");
			}
		}

		final int acceptValue = fileChooser.showOpenDialog(OpenLogViewer.getInstance());
		if (acceptValue == JFileChooser.APPROVE_OPTION) {
			if (decoderInUse != null) {
				// Clear out all references to data that we don't need and thereby ensure that we have lots of memory free for data we're about to gather!
				final GenericLog logInUse = decoderInUse.getDecodedLog();
				if (logInUse != null) {
					logInUse.clearOut(); // This is the wrong approach. The correct approach is to reuse the object, try that next...
				}
				decoderInUse = null;
				setLog(null);
			} // else haven't read in a log yet.

			final File openFile = fileChooser.getSelectedFile();
			if(openFile.exists()){
				if ("bin".equals(Utilities.getExtension(openFile)) || "la".equals(Utilities.getExtension(openFile)) || (fileChooser.getFileFilter() instanceof FreeEMSFileFilter)) {
					decoderInUse = new FreeEMSBin(openFile);
				} else {
					decoderInUse = new CSVTypeLog(openFile);
				}
			} else {
				JOptionPane.showMessageDialog(mainAppRef, labels.getObject(OPEN_FILE_ERROR_MESSAGE_KEY) + "\n" + openFile.getAbsolutePath(), labels.getObject(OPEN_FILE_ERROR_TITLE_KEY).toString(), JOptionPane.ERROR_MESSAGE);
			}

			if (openFile != null) {
				OpenLogViewer.getInstance().setTitle(APPLICATION_NAME + " - " + openFile.getName());
				saveApplicationWideProperty(NAME_OF_LAST_DIR_KEY, openFile.getParent());
				saveApplicationWideProperty(NAME_OF_LAST_FILE_KEY, openFile.getPath());
				saveApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS, fileChooser.getFileFilter().getClass().getCanonicalName());
			}
		}
	}

	public void openLastFile() {
		final JFileChooser fileChooser = new JFileChooser();
		final String lastFingFile = getApplicationWideProperty(NAME_OF_LAST_FILE_KEY);
		if (lastFingFile != null) {
			final File openFile = new File(lastFingFile);
			if (decoderInUse != null) {
				// Clear out all references to data that we don't need and thereby ensure that we have lots of memory free for data we're about to gather!
				final GenericLog logInUse = decoderInUse.getDecodedLog();
				if (logInUse != null) {
					logInUse.clearOut(); // This is the wrong approach. The correct approach is to reuse the object, try that next...
				}
				decoderInUse = null;
				setLog(null);
			} // else haven't read in a log yet.

			if(openFile.exists()){
				if ("bin".equals(Utilities.getExtension(openFile)) || "la".equals(Utilities.getExtension(openFile)) || (fileChooser.getFileFilter() instanceof FreeEMSFileFilter)) {
					decoderInUse = new FreeEMSBin(openFile);
				} else {
					decoderInUse = new CSVTypeLog(openFile);
				}
			} else {
				JOptionPane.showMessageDialog(mainAppRef, labels.getObject(OPEN_LAST_FILE_ERROR_MESSAGE_KEY) + "\n" + openFile.getAbsolutePath(), labels.getObject(OPEN_LAST_FILE_ERROR_TITLE_KEY).toString(), JOptionPane.ERROR_MESSAGE);
			}

			if (openFile != null) {
				OpenLogViewer.getInstance().setTitle(APPLICATION_NAME + " - " + openFile.getName());
				saveApplicationWideProperty(NAME_OF_LAST_DIR_KEY, openFile.getParent());
				saveApplicationWideProperty(NAME_OF_LAST_FILE_KEY, openFile.getPath());
				saveApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS, fileChooser.getFileFilter().getClass().getCanonicalName());
			}
		} else {
			// uh oh
		}
	}

	private String getApplicationWideProperty(final String key) {
		try {
			final Properties AppWide = new Properties();
			final File AppWideFile = openAppWideProps(AppWide);
			if (AppWideFile != null) {
				return AppWide.getProperty(key);
			} else {
				throw new IllegalArgumentException("Problem getting property, got null instead of file!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("IO issue: " + e.getMessage(), e);
		}
	}

	private void saveApplicationWideProperty(final String key, final String value) {
		try {
			final Properties AppWide = new Properties();
			final File AppWideFile = openAppWideProps(AppWide);
			if (AppWideFile != null) {
				AppWide.setProperty(key, value);
				AppWide.store(new FileOutputStream(AppWideFile), "saved");
			} else {
				throw new IllegalArgumentException("Problem saving property, got null instead of file!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Another IO issue: " + e.getMessage(), e);
		}
	}

	private void removeApplicationWideProperty(final String key) {
		try {
			final Properties AppWide = new Properties();
			final File AppWideFile = openAppWideProps(AppWide);
			if (AppWideFile != null) {
				AppWide.remove(key);
				AppWide.store(new FileOutputStream(AppWideFile), "removed");
			} else {
				throw new IllegalArgumentException("Problem removing property, got null instead of file!");
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("YAIO issue: " + e.getMessage(), e);
		}
	}

	private File openAppWideProps(final Properties AppWide) throws IOException {
		File AppWideFile;
		AppWideFile = new File(System.getProperty("user.home"));

		if (!AppWideFile.exists() || !AppWideFile.canRead() || !AppWideFile.canWrite()) {
			System.out.println("Either you dont have a home director, or it isnt read/writeable... fix it!");
		} else {
			AppWideFile = new File(AppWideFile, SETTINGS_DIRECTORY);
		}

		if (!AppWideFile.exists()) {
			try {
				if (AppWideFile.mkdir()) {
					AppWideFile = new File(AppWideFile, PROPERTIES_FILENAME);
					if (AppWideFile.createNewFile()) {
						AppWide.load(new FileInputStream(AppWideFile));
					}
				} else {
					throw new RuntimeException("Failed to create directory, no code to handle this at this time.");
					// This should be passed up to the GUI as a dialog that tells you it can't do what it has to be able to...
				}
			} catch (IOException IOE) {
				System.out.print(IOE.getMessage());
			}
		} else {
			AppWideFile = new File(AppWideFile, PROPERTIES_FILENAME);
			if (!AppWideFile.createNewFile()) {
				AppWide.load(new FileInputStream(AppWideFile));
			}
		}
		return AppWideFile;
	}

	public void enterFullScreen() {
		if (!fullscreen) {
			final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final GraphicsDevice[] device = ge.getScreenDevices();

			for (int i = 0; i < device.length; i++){ // Cycle through available devices (monitors) looking for device that has center of app
				Rectangle bounds = device[i].getDefaultConfiguration().getBounds();
				int centerX = (int)Math.round(this.getBounds().getCenterX());
				int centerY = (int)Math.round(this.getBounds().getCenterY());
				Point center = new Point(centerX, centerY);
				if (bounds.contains(center)){ // Found the device (monitor) that contains the center of the app
					containingDevice = i;
					if (device[containingDevice].isFullScreenSupported()) {
						try {
							fullscreen = true;    // Remember so that we can react accordingly.
							saveScreenState();    // Save the current state of things to restore later when exiting fullscreen mode.
							setJMenuBar(null);    // remove the menu bar for maximum space, load files in non fullscreen mode! :-p
							removeNotify();       // Without this we can't do setUndecorated(true)!
							setUndecorated(true); // Remove the window frame/bezel!
							addNotify();          // turn things back on again!
							//setResizable(false);// Fred: doesn't make sense and could be dangerous, according to oracle.
							                      // Ben: Removed setResizable(false) because it causes GNOME menu bar and task bar to show in front of the app!
							device[containingDevice].setFullScreenWindow(this);
							validate();           // required after rearranging component hierarchy
							requestFocusInWindow(); // Put keyboard focus here so toggling fullscreen works
							graphingPanel.moveGraphDueToResize(); // Done so centering still works on Mac
						} catch (Exception e) {
							e.printStackTrace();
							System.out.println(labels.getObject(FAILED_TO_GO_FULLSCREEN_MESSAGE_KEY));
							fullscreen = false;
						}
					} else {
						System.out.println(labels.getObject(CANT_GO_FULLSCREEN_MESSAGE_KEY));
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
			device[containingDevice].setFullScreenWindow(null);
			removeNotify();
			setUndecorated(false);
			addNotify();
			setJMenuBar(menuBar);
			setVisible(false); // Hide while packing and restoring to avoid showing window resize
			pack();
			restoreScreenState();
			setVisible(true);
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

	public boolean isFullscreen(){
		return fullscreen;
	}

	private void saveScreenState(){
		extendedState = getExtendedState();
		location = getLocation();
		size = getSize();
	}

	private void restoreScreenState(){
		setExtendedState(extendedState);
		setLocation(location);
		setSize(size);
	}

	public void setLog(final GenericLog genericLog) {
		graphingPanel.setLog(genericLog);
	}

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
