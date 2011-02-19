/* DataReader
 *
 * Copyright 2011
 *
 * This file is part of the DataReader project.
 *
 * DataReader software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DataReader software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any DataReader software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */

/*
 * DataLogReaderApp.java
 *
 * Created on Jan 26, 2011, 2:55:31 PM
 */

package datareader;

import Decoder.CVSTypeLog;
import Decoder.FreeEMSBin;
import GenericLog.GenericLog;
import Graphing.DrawnGraph;
import Graphing.LayeredGraph;
import Utils.Utilities;
import java.awt.Dimension;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Bryan
 */
public class DataLogReaderApp extends javax.swing.JFrame {

    /** Creates new form DataLogReaderApp */
    public DataLogReaderApp() {
        initComponents();
    }

    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        //drawnGraph = new DrawnGraph();
        layeredGraph = new LayeredGraph();
        playBar = new PlayBarPanel();
        
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openFileMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        graphMenu = new GraphMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        setLayout(new java.awt.BorderLayout());
        add(mainPanel, java.awt.BorderLayout.CENTER);

        mainPanel.setName("jPanel1"); // NOI18N
        mainPanel.setLayout(new java.awt.BorderLayout());

        //drawnGraph.setName("pl"); // NOI18N
        //drawnGraph.setPreferredSize(new Dimension(600,400));
        //drawnGraph.setLayout(new java.awt.FlowLayout());
        

        //mainPanel.add(drawnGraph, java.awt.BorderLayout.CENTER);
        layeredGraph.setPreferredSize(new Dimension(600,400));
        mainPanel.add(layeredGraph,java.awt.BorderLayout.CENTER);

        

        mainPanel.add(playBar, java.awt.BorderLayout.SOUTH);

        mainMenuBar.setName("jMenuBar1"); // NOI18N

        fileMenu.setText("File");
        fileMenu.setName("jMenu1"); // NOI18N

        openFileMenuItem.setText("Open Log");
        openFileMenuItem.setName("openlog"); // NOI18N
        openFileMenuItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                openFileMenuItemMouseReleased(evt);
            }
        });
        fileMenu.add(openFileMenuItem);

        mainMenuBar.add(fileMenu);

        editMenu.setText("Edit");
        editMenu.setName("jMenu2"); // NOI18N
        mainMenuBar.add(editMenu);
        mainMenuBar.add(graphMenu);

        setJMenuBar(mainMenuBar);
        pack();
    }
    private void openFileMenuItemMouseReleased( java.awt.event.MouseEvent evt) {
        DataLogReaderApp.openFile();
    }

    public void setLog(GenericLog genericLog) {
        //drawnGraph.setLog(genericLog);
        layeredGraph.setLog(genericLog);
    }
    
    /**
     * Returns the reference to this instance, it is meant to be a method to make getting the main frame simpler
     * @return <code>this</code> instance
     */
    public static DataLogReaderApp getInstance() {
        return mainAppRef;
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               mainAppRef = new DataLogReaderApp();
                       mainAppRef.setVisible(true);
            }
        });
    }

   // public DrawnGraph getDrawnGraph() {
   //     return drawnGraph;
    //}

    public LayeredGraph getLayeredGraph() {
        return layeredGraph;
    }

    public GraphMenu getGraphMenu() {
        return graphMenu;
    }

    public static void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        FreeEMSFileFilter filter = new FreeEMSFileFilter();
        CVSTypeFileFilter filter2 = new CVSTypeFileFilter();
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.addChoosableFileFilter(filter2);
        //fileChooser.setAcceptAllFileFilterUsed(false);
        int acceptValue = fileChooser.showOpenDialog(DataLogReaderApp.getInstance());
        if(acceptValue == JFileChooser.APPROVE_OPTION) {
            File openFile = fileChooser.getSelectedFile();
            if(Utilities.getExtension(openFile).equals("bin")) new FreeEMSBin(openFile);
            else new CVSTypeLog(openFile);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static DataLogReaderApp mainAppRef;

    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem openFileMenuItem;
    private javax.swing.JPanel mainPanel;
    //private DrawnGraph drawnGraph;
    private LayeredGraph layeredGraph;
    private PlayBarPanel playBar;
    private GraphMenu graphMenu;
    
    // End of variables declaration//GEN-END:variables
    private GenericLog genLog;
    private FreeEMSBin fems;
    //DrawnGraph drawnGraph;
}
