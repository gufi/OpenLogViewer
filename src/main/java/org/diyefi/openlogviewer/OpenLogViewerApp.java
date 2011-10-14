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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;

import org.diyefi.openlogviewer.decoder.CSVTypeLog;
import org.diyefi.openlogviewer.decoder.FreeEMSBin;
import org.diyefi.openlogviewer.decoder.FreeEMSByteLA;
import org.diyefi.openlogviewer.filefilters.CSVTypeFileFilter;
import org.diyefi.openlogviewer.filefilters.FreeEMSFileFilter;
import org.diyefi.openlogviewer.filefilters.FreeEMSLAFileFilter;
import org.diyefi.openlogviewer.genericlog.GenericLog;
import org.diyefi.openlogviewer.graphing.EntireGraphingPanel;
import org.diyefi.openlogviewer.graphing.GraphPositionPanel;
import org.diyefi.openlogviewer.graphing.MultiGraphLayeredPane;
import org.diyefi.openlogviewer.optionpanel.OptionFrameV2;
import org.diyefi.openlogviewer.propertypanel.PropertiesPane;
import org.diyefi.openlogviewer.propertypanel.SingleProperty;
import org.diyefi.openlogviewer.utils.Utilities;

/**
 *
 * @author Bryan
 */
public class OpenLogViewerApp extends javax.swing.JFrame {

	/** Creates new form OpenLogViewerApp */
    public OpenLogViewerApp() {
        initComponents();
    }

    private void initComponents() {
        properties = new ArrayList<SingleProperty>();
        prefFrame = new PropertiesPane("Properties");
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


        ////////////////////////////////////////////////////////////
        ///setup mainpanel
        ///////////////////////////////////////////////////////////
        mainPanel.setName("Main Panel");
        mainPanel.setLayout(new java.awt.BorderLayout());
        this.add(mainPanel, java.awt.BorderLayout.CENTER);
        graphingPanel.setPreferredSize(new Dimension(600, 420));
        mainPanel.add(graphingPanel, java.awt.BorderLayout.CENTER);
        mainPanel.add(playBar, java.awt.BorderLayout.SOUTH);


        //////////////////////////////////////////////////////////////////
        ///////////File Menu
        //////////////////////////////////////////////////////////////////
        openFileMenuItem.setText("Open Log");
        openFileMenuItem.setName("openlog");
        openFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openFileMenuItemMouseReleased(e);
            }
        });


        quitFileMenuItem.setText("Quit");
        quitFileMenuItem.setName("quit");
        quitFileMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        fileMenu.setText("File");
        fileMenu.setName("file");
        fileMenu.add(openFileMenuItem);
        fileMenu.add(quitFileMenuItem);

        ////////////////////////////////////////////////////////////////////
        ///////////////Edit Menu
        ////////////////////////////////////////////////////////////////////
        editMenu.setText("Edit");
        editMenu.setName("edit"); // NOI18N

        propertiesOptionMenuItem.setText("Properties");
        propertiesOptionMenuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OpenLogViewerApp.getInstance().getPropertyPane().setVisible(true);
                OpenLogViewerApp.getInstance().getPropertyPane().setAlwaysOnTop(true);
                OpenLogViewerApp.getInstance().getPropertyPane().setAlwaysOnTop(false);

            }
        });
        editMenu.add(propertiesOptionMenuItem);


        //////////////////////////////////////////////////////////////////
        //////////////Add to mainMenu
        /////////////////////////////////////////////////////////////////
        mainMenuBar.setName("Main Menu");
        mainMenuBar.add(fileMenu);
        mainMenuBar.add(editMenu);
        ////////////////////////////////////////////////////////////////////
        ///////////////////Graph menu
        /////////////////////GraphMenu.java
        ////////////////////////////////////////////////////////////////////
        mainMenuBar.add(graphMenu);

        setJMenuBar(mainMenuBar);
        this.addKeyListener(graphingPanel);
        pack();
    }

    private void openFileMenuItemMouseReleased(ActionEvent evt) {
        openFile();
    }

    public void setLog(GenericLog genericLog) {
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
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    // Set cross-platform Java L&F (also called "Metal")
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (UnsupportedLookAndFeelException e) {
                    // handle exception
                } catch (ClassNotFoundException e) {
                    // handle exception
                } catch (InstantiationException e) {
                    // handle exception
                } catch (IllegalAccessException e) {
                    // handle exception
                }
                mainAppRef = new OpenLogViewerApp();
                mainAppRef.setVisible(true);
                mainAppRef.setTitle("OpenLogViewer -");
            }
        });
    }

    public EntireGraphingPanel getEntireGraphingPanel(){
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

    public ArrayList<SingleProperty> getProperties() {
        return properties;
    }

    public void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        String lastFingFile = getApplicationWideProperty("lastFingFile");
        if (lastFingFile != null) {
            fileChooser.setSelectedFile(new File(lastFingFile));
        } else {
            String lastFingDir = getApplicationWideProperty("lastFingDir");
            if (lastFingDir != null) {
                fileChooser.setCurrentDirectory(new File(lastFingDir));
            }
        }
        fileChooser.addChoosableFileFilter(new FreeEMSFileFilter());
        fileChooser.addChoosableFileFilter(new CSVTypeFileFilter());
        fileChooser.addChoosableFileFilter(new FreeEMSLAFileFilter());
        String chooserClass = getApplicationWideProperty("chooserClass");
        if (chooserClass != null) {
            try {
                fileChooser.setFileFilter((FileFilter) Class.forName(chooserClass).newInstance());
            } catch (ClassNotFoundException c) {
                removeApplicationWideProperty("chooserClass");
                System.out.println("Class not found! chooserClass removed from props!");
            } catch (InstantiationException i) {
                removeApplicationWideProperty("chooserClass");
                System.out.println("Could not instantiate class! chooserClass removed from props!");
            } catch (IllegalAccessException l) {
                removeApplicationWideProperty("chooserClass");
                System.out.println("Could access class! chooserClass removed from props!");
            }
        }

        int acceptValue = fileChooser.showOpenDialog(OpenLogViewerApp.getInstance());
        if (acceptValue == JFileChooser.APPROVE_OPTION) {
            File openFile = fileChooser.getSelectedFile();
            if (Utilities.getExtension(openFile).equals("bin") || fileChooser.getFileFilter() instanceof FreeEMSFileFilter) {
                new FreeEMSBin(openFile);
            } else if (Utilities.getExtension(openFile).equals("la") || fileChooser.getFileFilter() instanceof FreeEMSLAFileFilter) {
                new FreeEMSByteLA(openFile);
            } else {
                new CSVTypeLog(openFile);
            }
            if (openFile != null) {
                OpenLogViewerApp.getInstance().setTitle("OpenLogViewer - " + openFile.getName());
                saveApplicationWideProperty("lastFingDir", openFile.getParent());
                saveApplicationWideProperty("lastFingFile", openFile.getPath());
                saveApplicationWideProperty("chooserClass", fileChooser.getFileFilter().getClass().getCanonicalName());
                System.gc();
            }
        }
    }

    private String getApplicationWideProperty(String key) {
        try {
            Properties AppWide = new Properties();
            File AppWideFile = openAppWideProps(AppWide);
            if (AppWideFile != null) {
                return AppWide.getProperty(key);
            } else {
                throw new IllegalArgumentException("received null instead of valid file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveApplicationWideProperty(String key, String value) {
        try {
            Properties AppWide = new Properties();
            File AppWideFile = openAppWideProps(AppWide);
            if (AppWideFile != null) {
                AppWide.setProperty(key, value);
                AppWide.store(new FileOutputStream(AppWideFile), "saved");
            } else {
                throw new IllegalArgumentException("received null instead of valid file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void removeApplicationWideProperty(String key) {
        try {
            Properties AppWide = new Properties();
            File AppWideFile = openAppWideProps(AppWide);
            if (AppWideFile != null) {
                AppWide.remove(key);
                AppWide.store(new FileOutputStream(AppWideFile), "saved");
            } else {
                throw new IllegalArgumentException("received null instead of valid file");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private File openAppWideProps(Properties AppWide) throws IOException {
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
                    AppWideFile = new File(AppWideFile, "OLVAllProperties.olv");
                    if (AppWideFile.createNewFile()) {
                        AppWide.load(new FileInputStream(AppWideFile));
                    }
                } else {
                    //find somewhere else
                }
            } catch (IOException IOE) {
                System.out.print(IOE.getMessage());
            }
        } else {
            AppWideFile = new File(AppWideFile, "OLVAllProperties.olv");
            if (!AppWideFile.createNewFile()) {
                AppWide.load(new FileInputStream(AppWideFile));
            }
        }
        return AppWideFile;
    }
    
    // Variables declaration -
    public static OpenLogViewerApp mainAppRef;
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
    private ArrayList<SingleProperty> properties;
	private static final long serialVersionUID = 7987394054547975563L;
}
