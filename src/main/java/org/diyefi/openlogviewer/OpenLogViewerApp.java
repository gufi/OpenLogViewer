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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;
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
import org.diyefi.openlogviewer.graphing.GraphPositionPanel;
import org.diyefi.openlogviewer.graphing.MultiGraphLayeredPane;
import org.diyefi.openlogviewer.optionpanel.OptionFrameV2;
import org.diyefi.openlogviewer.propertypanel.PropertiesPane;
import org.diyefi.openlogviewer.propertypanel.SingleProperty;
import org.diyefi.openlogviewer.utils.Utilities;

public final class OpenLogViewerApp extends javax.swing.JFrame {
	public static final String NEWLINE = System.getProperty("line.separator");

	private static final long serialVersionUID = 7987394054547975563L;
	private static final String PROPERTIES_NAME = "Properties";
	private static final String PROPERTIES_FILENAME = "OLVAllProperties.olv";
	private static final String NAME_OF_LAST_FILE_KEY = "lastFingFile";
	private static final String NAME_OF_LAST_DIR_KEY = "lastFingDir";
	private static final String NAME_OF_LAST_CHOOSER_CLASS = "chooserClass";

	private static OpenLogViewerApp mainAppRef;
	private javax.swing.JMenu fileMenu;
	private javax.swing.JMenu editMenu;
	private javax.swing.JMenuBar mainMenuBar;
	private javax.swing.JMenuItem openFileMenuItem;
	private javax.swing.JMenuItem quitFileMenuItem;
	private javax.swing.JMenuItem propertiesOptionMenuItem;
	private javax.swing.JPanel mainPanel;
	private EntireGraphingPanel graphingPanel;
	private PlayBarPanel playBar;
	private GraphMenu graphMenu;
	private OptionFrameV2 optionFrame;
	private PropertiesPane prefFrame;
	private List<SingleProperty> properties;
	private AbstractDecoder decoderInUse;

	/** Creates new form OpenLogViewerApp */
	public OpenLogViewerApp() {
		initComponents();
	}

	private void initComponents() {
		properties = new ArrayList<SingleProperty>();
		prefFrame = new PropertiesPane(PROPERTIES_NAME);
		prefFrame.setProperties(properties);
		optionFrame = new OptionFrameV2();
		mainPanel = new javax.swing.JPanel();
		graphingPanel = new EntireGraphingPanel();
		playBar = new PlayBarPanel();
		graphMenu = new GraphMenu();
		mainMenuBar = new javax.swing.JMenuBar();
		fileMenu = new javax.swing.JMenu();
		editMenu = new javax.swing.JMenu();
		openFileMenuItem = new javax.swing.JMenuItem();
		quitFileMenuItem = new javax.swing.JMenuItem();
		propertiesOptionMenuItem = new javax.swing.JMenuItem();

		this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		this.setLayout(new java.awt.BorderLayout());
		this.setFocusable(true);

		// Setup the main panel
		mainPanel.setName("Main Panel");
		mainPanel.setLayout(new java.awt.BorderLayout());
		this.add(mainPanel, java.awt.BorderLayout.CENTER);
		graphingPanel.setPreferredSize(new Dimension(600, 420));
		mainPanel.add(graphingPanel, java.awt.BorderLayout.CENTER);
		mainPanel.add(playBar, java.awt.BorderLayout.SOUTH);

		// File Menu
		openFileMenuItem.setText("Open Log");
		openFileMenuItem.setName("openlog");
		openFileMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				openFileMenuItemMouseReleased(e);
			}
		});

		quitFileMenuItem.setText("Quit");
		quitFileMenuItem.setName("quit");
		quitFileMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				System.exit(0);
			}
		});

		fileMenu.setText("File");
		fileMenu.setName("file");
		fileMenu.add(openFileMenuItem);
		fileMenu.add(quitFileMenuItem);

		// Edit Menu
		editMenu.setText("Edit");
		editMenu.setName("edit");

		propertiesOptionMenuItem.setText(PROPERTIES_NAME);
		propertiesOptionMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				OpenLogViewerApp.getInstance().getPropertyPane().setVisible(true);
				OpenLogViewerApp.getInstance().getPropertyPane().setAlwaysOnTop(true);
				OpenLogViewerApp.getInstance().getPropertyPane().setAlwaysOnTop(false);

			}
		});
		editMenu.add(propertiesOptionMenuItem);


		// Add to mainMenu
		mainMenuBar.setName("Main Menu");
		mainMenuBar.add(fileMenu);
		mainMenuBar.add(editMenu);

		// Graph menu
		mainMenuBar.add(graphMenu);

		setJMenuBar(mainMenuBar);
		this.addKeyListener(graphingPanel);
		pack();
	}

	private void openFileMenuItemMouseReleased(final ActionEvent evt) {
		openFile();
	}

	public void setLog(final GenericLog genericLog) {
		graphingPanel.setLog(genericLog);
	}

	/**
	 * Returns the reference to this instance, it is meant to be a method to make getting the main frame simpler
	 * @return <code>this</code> instance
	 */
	public static OpenLogViewerApp getInstance() {
		return mainAppRef;
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
				try {
					// Set cross-platform Java L&F (also called "Metal")
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				} catch (UnsupportedLookAndFeelException e) {
					throw new RuntimeException("Your system would look ugly, sorry!", e);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException("Metal look not found! (And plastic sucks)", e);
				} catch (InstantiationException e) {
					throw new RuntimeException("Couldn't create the look we felt you needed!", e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException("This should be handled more appropriately!", e);
				}

				mainAppRef = new OpenLogViewerApp();
				mainAppRef.setVisible(true);
				mainAppRef.setTitle("OpenLogViewer -");
			}
		});
	}

	public EntireGraphingPanel getEntireGraphingPanel() {
		return graphingPanel;
	}

	public MultiGraphLayeredPane getMultiGraphLayeredPane() {
		return graphingPanel.getMultiGraphLayeredPane();
	}

	public GraphPositionPanel getGraphPositionPanel() {
		return graphingPanel.getGraphPositionPanel();
	}

	public GraphMenu getGraphMenu() {
		return graphMenu;
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
				System.out.println("Could access class! chooserClass removed from props!");
			}
		}

		final int acceptValue = fileChooser.showOpenDialog(OpenLogViewerApp.getInstance());
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
			if ("bin".equals(Utilities.getExtension(openFile)) || "la".equals(Utilities.getExtension(openFile)) || (fileChooser.getFileFilter() instanceof FreeEMSFileFilter)) {
				decoderInUse = new FreeEMSBin(openFile);
			} else {
				decoderInUse = new CSVTypeLog(openFile);
			}

			if (openFile != null) {
				OpenLogViewerApp.getInstance().setTitle("OpenLogViewer - " + openFile.getName());
				saveApplicationWideProperty(NAME_OF_LAST_DIR_KEY, openFile.getParent());
				saveApplicationWideProperty(NAME_OF_LAST_FILE_KEY, openFile.getPath());
				saveApplicationWideProperty(NAME_OF_LAST_CHOOSER_CLASS, fileChooser.getFileFilter().getClass().getCanonicalName());
			}
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
			AppWideFile = new File(AppWideFile, ".OpenLogViewer");
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
}
