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
package datareader;

import GenericLog.GenericLog;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Bryan
 */
public class GraphMenu extends JMenu {

    JCheckBoxMenuItem antiAliasing;
    JMenuItem optionPaneItem;
    GFrame optionPane;

    public GraphMenu(String s, boolean b) {
        super(s, b);
    }

    public GraphMenu(String s) {
        super(s);
    }

    public GraphMenu() {
        super();
        initMenu();
    }



    private void initMenu() {
        this.setText("Graphing");
        this.setName("Graphing Menu");

        optionPane = null;
        optionPaneItem = new JMenuItem();
        optionPaneItem.setText("Option Pane");
        optionPaneItem.setName("Option Pane");
        optionPaneItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                optionPane.setVisible(true);
            }
        });
        antiAliasing = new JCheckBoxMenuItem();
        antiAliasing.setText("AntiAliasing");
        antiAliasing.setName("antialiasing");
        antiAliasing.setSelected(false);
        antiAliasing.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem j = (JCheckBoxMenuItem)e.getSource();
                DataLogReaderApp.getInstance().getDrawnGraph().setAntiAlising(j.isSelected());
            }

        });
        this.add(antiAliasing);
        
    }

    public void updateFromLog(GenericLog gl) {

        if(optionPane == null) {
            this.add(optionPaneItem);
            optionPane = new GFrame("Option Pane");
            optionPane.setPreferredSize(DataLogReaderApp.getInstance().getPreferredSize());
            optionPane.setSize(optionPane.getPreferredSize());
        }else optionPane.removeAll();
        Iterator i = gl.keySet().iterator();
        String head = "";
        GCheckBox toBeAdded = null;
        while (i.hasNext()) {
            head = (String) i.next();
            toBeAdded = new GCheckBox();
            toBeAdded.setName(head);
            toBeAdded.setText(head);
            toBeAdded.setSelected(false);
            optionPane.add(toBeAdded);
        }
        optionPane.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        optionPane.setVisible(true);
    }

    private class GFrame extends JFrame {

        public GFrame(String title) throws HeadlessException {
            super(title);
            this.setLayout(new GridLayout(10,5));
        }

        public GFrame() throws HeadlessException {
        }

    }
    

    private class GCheckBox extends JCheckBox implements ActionListener {

        public GCheckBox() {
            super();
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            GCheckBox i = (GCheckBox) e.getSource();
            if(i.isSelected()) {
                DataLogReaderApp.getInstance().getDrawnGraph().addActiveHeader(i.getName());
                DataLogReaderApp.getInstance().getDrawnGraph().reInitGraph();
            } else {
                if(DataLogReaderApp.getInstance().getDrawnGraph().removeActiveHeader(i.getName())) {
                    DataLogReaderApp.getInstance().getDrawnGraph().reInitGraph();
                }
            }
        }
    }


}
/**
 * 2/5/2011 meal for the night DO NOT EDIT MENU!
 * Sesame chicken alacarte
 * chicken lomein alacarte
 * orange chicken x2
 * 
 */
