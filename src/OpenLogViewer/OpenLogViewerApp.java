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
package OpenLogViewer;

import OpenLogViewer.filefilter.FreeEMSFileFilter;
import OpenLogViewer.filefilter.FreeEMSLAFileFilter;
import OpenLogViewer.filefilter.CSVTypeFileFilter;
import Decoder.CSVTypeLog;
import Decoder.FreeEMSBin;
import Decoder.FreeEMSByteLA;
import GenericLog.GenericLog;
import Graphing.LayeredGraph;
import OpenLogViewer.Properties.PropertiesPane;
import OpenLogViewer.Properties.SingleProperty;
import Utils.Utilities;
import OpenLogViewer.optionpane.OptionFrame;
import OpenLogViewer.optionpane.OptionFrameV2;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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
        layeredGraph = new LayeredGraph();
        playBar = new PlayBarPanel();
        graphMenu = new GraphMenu();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        editMenu = new javax.swing.JMenu();
        openFileMenuItem = new javax.swing.JMenuItem();
        quitFileMenuItem = new javax.swing.JMenuItem();
        propertiesOptionMenuItem = new javax.swing.JMenuItem() ;



        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        this.setLayout(new java.awt.BorderLayout());

        ////////////////////////////////////////////////////////////
        ///setup mainpanel
        ///////////////////////////////////////////////////////////
        this.add(mainPanel, java.awt.BorderLayout.CENTER);
        mainPanel.setName("Main Panel");
        mainPanel.setLayout(new java.awt.BorderLayout());

        layeredGraph.setPreferredSize(new Dimension(600, 400));


        mainPanel.add(layeredGraph, java.awt.BorderLayout.CENTER);
        mainPanel.add(playBar, java.awt.BorderLayout.SOUTH);




        //////////////////////////////////////////////////////////////////
        ///////////File Menu
        //////////////////////////////////////////////////////////////////
        openFileMenuItem.setText("Open Log");
        openFileMenuItem.setName("openlog");
        openFileMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                openFileMenuItemMouseReleased(e);
            }
        });


        quitFileMenuItem.setText("Quit");
        quitFileMenuItem.setName("quit");
        quitFileMenuItem.addActionListener(new ActionListener() {

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
        //////////////Addto mainMenu
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
        pack();
    }

    private void openFileMenuItemMouseReleased(ActionEvent evt) {
        OpenLogViewerApp.openFile();
    }

    public void setLog(GenericLog genericLog) {
        layeredGraph.setLog(genericLog);
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

    public LayeredGraph getLayeredGraph() {
        return layeredGraph;
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

    public static void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.addChoosableFileFilter(new FreeEMSFileFilter());
        fileChooser.addChoosableFileFilter(new CSVTypeFileFilter());
        fileChooser.addChoosableFileFilter(new FreeEMSLAFileFilter());

        int acceptValue = fileChooser.showOpenDialog(OpenLogViewerApp.getInstance());
        if (acceptValue == JFileChooser.APPROVE_OPTION) {
            File openFile = fileChooser.getSelectedFile();
            if (Utilities.getExtension(openFile).equals("bin") || fileChooser.getFileFilter() instanceof FreeEMSFileFilter) {
                new FreeEMSBin(openFile);
            }else if (Utilities.getExtension(openFile).equals("la") || fileChooser.getFileFilter() instanceof FreeEMSLAFileFilter) {
                new FreeEMSByteLA(openFile);
            } else {
                new CSVTypeLog(openFile);
            }
            if (openFile != null) {
                OpenLogViewerApp.getInstance().setTitle("OpenLogViewer - " + openFile.getName());
                System.gc();
            }
        }
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
    private LayeredGraph layeredGraph;
    private PlayBarPanel playBar;
    private GraphMenu graphMenu;
    private OptionFrameV2 optionFrame;
    private PropertiesPane prefFrame;
    private ArrayList<SingleProperty> properties;
}
